package edu.illinois.cs.cogcomp.saul.classifier

import java.io.File
import java.net.URL

import edu.illinois.cs.cogcomp.core.io.IOUtils
import edu.illinois.cs.cogcomp.lbjava.classify.{FeatureVector, TestDiscrete}
import edu.illinois.cs.cogcomp.lbjava.learn.Learner.Parameters
import edu.illinois.cs.cogcomp.lbjava.learn._
import edu.illinois.cs.cogcomp.lbjava.parse.Parser
import edu.illinois.cs.cogcomp.lbjava.util.ExceptionlessOutputStream
import edu.illinois.cs.cogcomp.saul.TestContinuous
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.property.{ CombinedDiscreteProperty, Property }
import edu.illinois.cs.cogcomp.saul.lbjrelated.LBJLearnerEquivalent
import edu.illinois.cs.cogcomp.saul.parser.LBJIteratorParserScala
import org.slf4j.{Logger, LoggerFactory}

import scala.reflect.ClassTag

abstract class Learnable[T <: AnyRef](val node: Node[T], val parameters: Parameters = new Learner.Parameters)(implicit tag: ClassTag[T]) extends LBJLearnerEquivalent {
  /** Whether to use caching */
  val useCache = false

  val logging = true
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  var isTraining = false

  def fromData = node.getTrainingInstances

  def getClassNameForClassifier = this.getClass.getCanonicalName

  def feature: List[Property[T]] = node.properties.toList

  /** filter out the label from the features */
  def combinedProperties = if (label != null) new CombinedDiscreteProperty[T](this.feature.filterNot(_.name == label.name))
  else new CombinedDiscreteProperty[T](this.feature)

  def lbpFeatures = combinedProperties.classifier

  /** classifier need to be defined by the user */
  val classifier: Learner

  /** syntactic suger to create simple calls to the function */
  def apply(example: AnyRef): String = classifier.discreteValue(example: AnyRef)

  /** specifications of the classifier and its model files  */
  classifier.setReadLexiconOnDemand()
  var modelDir = "models" + File.separator
  var lcFilePath = new URL(new URL("file:"), modelDir + getClassNameForClassifier + ".lc")
  var lexFilePath = new URL(new URL("file:"), modelDir + getClassNameForClassifier + ".lex")
  classifier.setModelLocation(lcFilePath)
  classifier.setLexiconLocation(lexFilePath)

  def setExtractor(): Unit = {
    if (feature != null) {
      if (logging)
        logger.info("Setting the feature extractors to be {}", lbpFeatures.getCompositeChildren)
      classifier.setExtractor(lbpFeatures)
    } else {
      logger.warn("Warning: no features found!")
    }
  }

  def setLabeler(): Unit = {
    if (label != null) {
      val oracle = Property.entitiesToLBJFeature(label)
      if (logging) {
        logger.info("Setting the labeler to be '{}", oracle)
      }
      classifier.setLabeler(oracle)
    }
  }

  // set parameters for classifier
  setExtractor()
  setLabeler()

  def setModelDir(directory: String) = {
    classifier.setReadLexiconOnDemand()
    modelDir = directory + File.separator
    lcFilePath = new URL(new URL("file:"), modelDir + getClassNameForClassifier + ".lc")
    lexFilePath = new URL(new URL("file:"), modelDir + getClassNameForClassifier + ".lex")
    classifier.setModelLocation(lcFilePath)
    classifier.setLexiconLocation(lexFilePath)
  }

  def removeModelFiles(): Unit = {
    IOUtils.rm(lcFilePath.getPath)
    IOUtils.rm(lexFilePath.getPath)
    createFiles()
  }

  def save(): Unit = {
    removeModelFiles()
    val dummyClassifier = new SparseNetworkLearner
    classifier.setExtractor(dummyClassifier)
    classifier.setLabeler(dummyClassifier)
    classifier.save()

    // after saving, get rid of the dummyClassifier in the classifier.
    setExtractor()
    setLabeler()
  }

  def createFiles(): Unit = {
    // create the model directory if it does not exist
    IOUtils.mkdir(modelDir)
    // create .lex file if it does not exist
    if (!IOUtils.exists(lexFilePath.getPath)) {
      val lexFile = ExceptionlessOutputStream.openCompressedStream(lexFilePath)
      if (classifier.getCurrentLexicon == null) lexFile.writeInt(0)
      else classifier.getCurrentLexicon.write(lexFile)
      lexFile.close()
    }

    // create .lc file if it does not exist
    if (!IOUtils.exists(lcFilePath.getPath)) {
      val lcFile = ExceptionlessOutputStream.openCompressedStream(lcFilePath)
      classifier.write(lcFile)
      lcFile.close()
    }
  }

  def load(lcFile: String, lexFile: String): Unit = {
    classifier.read(lcFile, lexFile)
  }

  def load(lcFile: URL, lexFile: URL): Unit = {
    load(lcFile.getPath, lexFile.getPath)
  }

  def load(): Unit = {
    load(lcFilePath.getPath, lexFilePath.getPath)
  }

  def learn(iteration: Int): Unit = {
    createFiles()
    isTraining = true
    if (useCache) {
      if (node.derivedInstances.isEmpty) {
        logger.error("No cached data found. Please use dataModel.load()")
      }
      learnWithDerivedInstances(iteration, node.derivedInstances.values)
    } else {
      learn(iteration, this.fromData)
    }
    isTraining = false
  }

  def learn(iteration: Int, data: Iterable[T]): Unit = {
    createFiles()
    if (logging)
      logger.info("Learnable: Learn with data of size {}", data.size)
    isTraining = true
    val crTokenTest = new LBJIteratorParserScala[T](data)
    crTokenTest.reset()

    def learnAll(crTokenTest: Parser, remainingIteration: Int): Unit = {
      val v = crTokenTest.next
      if (v == null) {
        if (logging & remainingIteration % 10 == 0)
          logger.info("Training: {} iterations remain.", remainingIteration)

        if (remainingIteration > 1) {
          crTokenTest.reset()
          //TODO We need a solution for this
          //datamodel.clearPropertyCache()
          learnAll(crTokenTest, remainingIteration - 1)
        }
      } else {
        classifier.learn(v)
        learnAll(crTokenTest, remainingIteration)
      }
    }

    learnAll(crTokenTest, iteration)
    classifier.doneLearning()
    isTraining = false
  }

  def learnWithDerivedInstances(numIterations: Int, featureVectors: Iterable[FeatureVector]): Unit = {
    isTraining = true
    val propertyNameSet = feature.map(_.name).toSet
    (0 until numIterations).foreach { _ =>
      featureVectors.foreach {
        fullFeatureVector =>
          val featureVector = new FeatureVector()
          val numFeatures = fullFeatureVector.size()
          (0 until numFeatures).foreach {
            featureIndex =>
              val feature = fullFeatureVector.getFeature(featureIndex)
              val propertyName = feature.getGeneratingClassifier
              if (label != null && label.name.equals(propertyName)) {
                featureVector.addLabel(feature)
              } else if (propertyNameSet.contains(propertyName)) {
                featureVector.addFeature(feature)
              }
          }
          classifier.learn(featureVector)
      }
    }
    classifier.doneLearning()
    isTraining = false
  }

  def forget() = this.classifier.forget()

  /** Test with given data, use internally
    *
    * @param testData The data to test on
    * @return List of (label, (f1, precision, recall))
    */
  def test(testData: Iterable[T] = null, prediction: Property[T] = null, groundTruth: Property[T] = null, exclude: String = ""): List[(String, (Double, Double, Double))] = {
    isTraining = false
    val testReader = new LBJIteratorParserScala[T](if (testData == null) node.getTestingInstances else testData)
    testReader.reset()
    val tester = if (prediction == null && groundTruth == null)
      TestDiscrete.testDiscrete(classifier, classifier.getLabeler, testReader)
    else
      TestDiscrete.testDiscrete(prediction.classifier, groundTruth.classifier, testReader)
    if (!exclude.isEmpty) {
      tester.addNull(exclude)
    }
    tester.printPerformance(System.out)
    val ret = tester.getLabels.map { label => (label, (tester.getF1(label), tester.getPrecision(label), tester.getRecall(label))) }
    ret.toList
  }

  def testContinuous(testData: Iterable[T] = null): Unit = {
    isTraining = false
    val testReader = new LBJIteratorParserScala[T](if (testData == null) node.getTestingInstances else testData)
    testReader.reset()
    new TestContinuous(classifier, classifier.getLabeler, testReader)
  }

  def chunkData(ts: List[Iterable[T]], i: Int, curr: Int, acc: (Iterable[T], Iterable[T])): (Iterable[T], Iterable[T]) = {
    ts match {
      case head :: more =>
        acc match {
          case (train, test) =>
            if (i == curr) {
              // we found the test part
              chunkData(more, i, curr + 1, (train, head))
            } else {
              chunkData(more, i, curr + 1, (head ++ train, test))
            }
        }
      case Nil => acc
    }
  }

  /** Run k fold cross validation. */
  def crossValidation(k: Int) = {
    val allData = this.fromData

    if (logging)
      logger.info("Running cross validation on {} data", allData.size)

    val groupSize = Math.ceil(allData.size / k).toInt
    val groups = allData.grouped(groupSize).toList

    val loops = if (k == 1) {
      (groups.head, Nil) :: Nil
    } else {
      (0 until k) map {
        i => chunkData(groups, i, 0, (Nil, Nil))
      }
    }

    def printTestResult(result: (String, (Double, Double, Double))): Unit = {
      result match {
        case (label, (f1, precision, recall)) =>
          println(s"  $label    $f1    $precision     $recall   ")
      }
    }

    val results = loops.zipWithIndex map {
      case ((trainingSet, testingSet), idx) =>
        logger.info("Running fold {}", idx)
        logger.info("Learn with {}", trainingSet.size)
        logger.info("Test with {}", testingSet.size)

        this.classifier.forget()
        this.learn(10, trainingSet)
        val testResult = this.test(testingSet)
        testResult
    }

    def sumTuple(a: (Double, Double, Double), b: (Double, Double, Double)): (Double, Double, Double) = {
      (a._1 + b._1, a._2 + b._2, a._3 + b._3)
    }

    def avgTuple(a: (Double, Double, Double), size: Int): (Double, Double, Double) = {
      (a._1 / size, a._2 / size, a._3 / size)
    }

    println("  label    f1    precision     recall   ")

    results.flatten.toList.groupBy({
      tu: (String, (Double, Double, Double)) => tu._1
    }).foreach({
      case (label, l) =>
        val t = l.length
        val avg = avgTuple(l.map(_._2).reduce(sumTuple), t)

        printTestResult((label, avg))
    })
  }

  /** Label property for users classifier */
  def label: Property[T]

  def using(properties: List[Property[T]]*): List[Property[T]] = {
    properties.toList.flatten
  }
}
