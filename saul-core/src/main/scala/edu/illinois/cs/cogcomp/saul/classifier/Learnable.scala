package edu.illinois.cs.cogcomp.saul.classifier

import java.net.URL

import edu.illinois.cs.cogcomp.core.io.IOUtils
import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, TestDiscrete }
import edu.illinois.cs.cogcomp.lbjava.learn.Learner.Parameters
import edu.illinois.cs.cogcomp.lbjava.learn.{ StochasticGradientDescent, SparsePerceptron, SparseAveragedPerceptron, Learner }
import edu.illinois.cs.cogcomp.lbjava.parse.Parser
import edu.illinois.cs.cogcomp.lbjava.util.ExceptionlessOutputStream
import edu.illinois.cs.cogcomp.saul.TestContinuous
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.property.{ RelationalFeature, PropertyWithWindow, CombinedDiscreteProperty, Property }
import edu.illinois.cs.cogcomp.saul.lbjrelated.LBJLearnerEquivalent
import edu.illinois.cs.cogcomp.saul.parser.LBJIteratorParserScala

import scala.reflect.ClassTag

abstract class Learnable[T <: AnyRef](val datamodel: DataModel, val parameters: Parameters = new Learner.Parameters)(implicit tag: ClassTag[T]) extends LBJLearnerEquivalent {

  def getClassNameForClassifier = this.getClass.getCanonicalName

  def fromData = datamodel.getNodeWithType[T].getTrainingInstances

  def feature: List[Property[T]] = datamodel.getFeaturesOf[T].toList
  def algorithm: String = "SparseNetwork"
  val featureExtractor = new CombinedDiscreteProperty[T](this.feature)

  val lbpFeatures: Classifier = {
    featureExtractor.classifier
  }

  val classifier: Learner = {

    // TODO: if we found a learned model in disk
    // Then we load the model

    // TODO: do caching instead of deleting caching when finished develop
    // TODO: define default paramters

    val modelDir = "models"
    IOUtils.mkdir(modelDir)
    IOUtils.rm(modelDir + getClassNameForClassifier + ".lc")
    IOUtils.rm(modelDir + getClassNameForClassifier + ".lex")

    // Else we re train one (if that is what user want)
    //new SparsePerceptron(0.1,0,3.5)
    if (algorithm.equals("Regression")) {
      new StochasticGradientDescent() {
        if (label != null) {
          val oracle = Property.entitiesToLBJFeature(label)
          setLabeler(oracle)
        }

        if (feature != null) {
          setExtractor(lbpFeatures)
        }

        // Looks like we have to build the lexicon
        lcFilePath = new URL(new URL("file:"), modelDir + getClassNameForClassifier + ".lc")
        lexFilePath = new URL(new URL("file:"), modelDir + getClassNameForClassifier + ".lex")
        val out1: ExceptionlessOutputStream = ExceptionlessOutputStream.openCompressedStream(lexFilePath)
        if (lexicon == null) out1.writeInt(0)
        else lexicon.write(out1)
        out1.close()
        val out2: ExceptionlessOutputStream = ExceptionlessOutputStream.openCompressedStream(lcFilePath)
        write(out2)
        out2.close()
        readLexiconOnDemand = true

        //val p: SparseAveragedPerceptron.Parameters  =
        // new SparseAveragedPerceptron.Parameters();
        ///p.learningRate = .1;
        //p.thickness = 3;
        //p.thickness = {{ 1 -> 3 : .5 }};
        // baseLTU = new SparseAveragedPerceptron(p);
        //setParameters(p)
        //Thickness(3)
        //learningRate=0.1
      }
    } else if (algorithm.equals("SparsePerceptron")) {
      new SparsePerceptron() ////
      {
        // net=network
        if (label != null) {
          val oracle = Property.entitiesToLBJFeature(label)
          setLabeler(oracle)
        }

        if (feature != null) {
          setExtractor(lbpFeatures)
        }

        // Looks like we have to build the lexicon
        lcFilePath = new URL(new URL("file:"), modelDir + getClassNameForClassifier + ".lc")
        lexFilePath = new URL(new URL("file:"), modelDir + getClassNameForClassifier + ".lex")
        val out1: ExceptionlessOutputStream = ExceptionlessOutputStream.openCompressedStream(lexFilePath)
        if (lexicon == null) out1.writeInt(0)
        else lexicon.write(out1)
        out1.close()
        val out2: ExceptionlessOutputStream = ExceptionlessOutputStream.openCompressedStream(lcFilePath)
        write(out2)
        out2.close()
        readLexiconOnDemand = true

        //val p: SparseAveragedPerceptron.Parameters  =
        // new SparseAveragedPerceptron.Parameters();
        ///p.learningRate = .1;
        //p.thickness = 3;
        //p.thickness = {{ 1 -> 3 : .5 }};
        // baseLTU = new SparseAveragedPerceptron(p);
        //setParameters(p)
        //Thickness(3)
        //learningRate=0.1
      }
    } else new SparseNetworkLBP() // new SparsePerceptron()////
    {
      // net=network
      if (label != null) {
        val oracle = Property.entitiesToLBJFeature(label)
        setLabeler(oracle)
      }

      if (feature != null) {
        setExtractor(lbpFeatures)
      }

      // Looks like we have to build the lexicon
      lcFilePath = new URL(new URL("file:"), modelDir + getClassNameForClassifier + ".lc")
      lexFilePath = new URL(new URL("file:"), modelDir + getClassNameForClassifier + ".lex")
      val out1: ExceptionlessOutputStream = ExceptionlessOutputStream.openCompressedStream(lexFilePath)
      if (lexicon == null) out1.writeInt(0)
      else lexicon.write(out1)
      out1.close()
      val out2: ExceptionlessOutputStream = ExceptionlessOutputStream.openCompressedStream(lcFilePath)
      write(out2)
      out2.close()
      readLexiconOnDemand = true

      val p: SparseAveragedPerceptron.Parameters =
        new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 3
      //p.thickness = {{ 1 -> 3 : .5 }};
      baseLTU = new SparseAveragedPerceptron(p)
      //setParameters(p)
      //Thickness(3)
      //learningRate=0.1
    }
  }

  def learn(iteration: Int): Unit = {
    learn(iteration, this.fromData)
  }

  def learn(iteration: Int, data: Iterable[T]): Unit = {
    println("Learnable: Learn with data of size" + data.size)
    val crTokenTest = new LBJIteratorParserScala[T](data)
    crTokenTest.reset()

    def learnAll(crTokenTest: Parser, remainingIteration: Int): Unit = {

      val v = crTokenTest.next
      if (v == null) {
        if (remainingIteration > 0) {
          crTokenTest.reset()
          learnAll(crTokenTest, remainingIteration - 1)
        }
      } else {
        classifier.learn(v)
        learnAll(crTokenTest, remainingIteration)
      }
    }
    if (algorithm.equals("Regression")) {
      println("BIAS:" + classifier.asInstanceOf[StochasticGradientDescent].getParameters)
    }
    learnAll(crTokenTest, iteration)
    classifier.doneLearning()
  }

  def forget() = this.classifier.forget()

  def test(): List[(String, (Double, Double, Double))] = {
    val data = this.datamodel.getNodeWithType[T].getTestingInstances
    test(data)
  }

  /** Test with given data, use internally
    * @param testData
    * @return List of (label, (f1,precision,recall))
    */
  def test(testData: Iterable[T]): List[(String, (Double, Double, Double))] = {
    println()
    val testReader = new LBJIteratorParserScala[T](testData)
    testReader.reset()
    val tester = TestDiscrete.testDiscrete(classifier, classifier.getLabeler, testReader)
    tester.printPerformance(System.out)
    val ret = tester.getLabels.map({
      label => (label, (tester.getF1(label), tester.getPrecision(label), tester.getRecall(label)))
    })

    ret.toList
  }

  def testContinuos(): Unit = {
    val data = this.datamodel.getNodeWithType[T].getTestingInstances
    testContinuos(data)
  }
  def testContinuos(testData: Iterable[T]): Unit = {
    println()
    val testReader = new LBJIteratorParserScala[T](testData)
    //    println("Here is the test!")
    testReader.reset()
    val tester = new TestContinuous(classifier, classifier.getLabeler, testReader)
    // tester.printPerformance(System.out)
    //val ret = tester.getLabels.map({
    // label => (label , (tester.getF1(label),tester.getPrecision(label),tester.getRecall(label)))
    // })

    //ret toList
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

    println(s"Running cross validation on ${allData.size} data   ")

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
        println(s"Running fold $idx")
        println(s"Learn with ${trainingSet.size}")
        println(s"Test with ${testingSet.size}")

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

  /** User defines the following parts */
  /** Labels
    * @return
    */
  def label: Property[T]

  /** A windows of properties
    * @param before always negative (or 0)
    * @param after always positive (or 0)
    */
  def windowWithIn[U <: AnyRef](before: Int, after: Int, properties: List[Property[T]])(implicit uTag: ClassTag[U]): Property[T] = {
    val fls = datamodel.getRelatedFieldsBetween[T, U]
    getWindowWithFilters(before, after, fls.map(e => (t: T) => e.neighborsOf(t).head), properties)
  }

  def window(before: Int, after: Int)(properties: List[Property[T]]): Property[T] = {
    getWindowWithFilters(before, after, Nil, properties)
  }

  private def getWindowWithFilters(before: Int, after: Int, filters: Iterable[T => Any], properties: List[Property[T]]): Property[T] = {
    new PropertyWithWindow[T](this.datamodel, before, after, filters, properties)
  }

  def using(properties: List[Property[T]]*): List[Property[T]] = {
    properties.toList.flatten
  }

  def using(l: Learner): Learner = {
    l
  }
  def nextWithIn[U <: AnyRef](properties: List[Property[T]])(implicit uTag: ClassTag[U]): Property[T] = {
    this.windowWithIn[U](0, 1, properties.toList)
  }

  def prevWithIn[U <: AnyRef](property: Property[T]*)(implicit uTag: ClassTag[U]): Property[T] = {
    this.windowWithIn[U](-1, 0, property.toList)
  }

  def nextOf(properties: List[Property[T]]): Property[T] = {
    window(0, 1)(properties)
  }

  def prevOf(properties: List[Property[T]]): Property[T] = {
    window(0, 1)(properties)
  }

  def relationalProperty[U <: AnyRef](implicit uTag: ClassTag[U]): Property[T] = {
    val fts: List[Property[U]] = this.datamodel.getAllPropertyOf[U]
    relationalProperty[U](fts)
  }
  def relationalProperty[U <: AnyRef](ls: List[Property[U]])(implicit uTag: ClassTag[U]): Property[T] = {
    val fts = this.datamodel.getAllPropertyOf[U]
    new RelationalFeature[T, U](this.datamodel, fts)
  }
}
