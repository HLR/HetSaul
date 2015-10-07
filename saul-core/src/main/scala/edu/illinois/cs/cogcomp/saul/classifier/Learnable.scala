package edu.illinois.cs.cogcomp.saul.classifier

import java.net.URL

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, TestDiscrete }
import edu.illinois.cs.cogcomp.lbjava.learn.{ StochasticGradientDescent, SparsePerceptron, SparseAveragedPerceptron, Learner }
import edu.illinois.cs.cogcomp.lbjava.parse.Parser
import edu.illinois.cs.cogcomp.lbjava.util.ExceptionlessOutputStream
import edu.illinois.cs.cogcomp.saul.TestContinuous
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.{ Attribute, AttributeWithWindow, CombinedDiscreteAttribute, RelationalFeature }
import edu.illinois.cs.cogcomp.saul.lbjrelated.LBJLearnerEquivalent
import edu.illinois.cs.cogcomp.saul.parser.LBJIteratorParserScala

import scala.reflect.ClassTag

abstract class Learnable[T <: AnyRef](val datamodel: DataModel)(implicit tag: ClassTag[T]) extends LBJLearnerEquivalent {

  def getClassNameForClassifier = this.getClass.getCanonicalName

  def fromData = datamodel.getNodeWithType[T].getTrainingInstances

  def feature: List[Attribute[T]] = datamodel.getFeaturesOf[T].toList
  def algorithm: String = "SparseNetwork"
  val featureExtractor = new CombinedDiscreteAttribute[T](this.feature)

  val lbpFeatures: Classifier = {
    featureExtractor.classifier
  }

  val classifier: Learner = {

    // TODO: if we found a learned model in disk
    // Then we load the model

    // TODO: do caching instead of deleting caching when finished develop

    import scala.sys.process._

    ("rm ./data/" + getClassNameForClassifier + ".lc") !

    ("rm ./data/" + getClassNameForClassifier + ".lex") !

    // Else we re train one (if that is what user want)
    //new SparsePerceptron(0.1,0,3.5)
    if (algorithm.equals("Regression")) {
      new StochasticGradientDescent() {
        if (label != null) {
          val oracle = Attribute.entitiesToLBJFeature(label)
          setLabeler(oracle)
        }

        if (feature != null) {
          setExtractor(lbpFeatures)
        }

        // Looks like we have to build the lexicon
        lcFilePath = new URL(new URL("file:"), "./data/" + getClassNameForClassifier + ".lc") //new java.net.URL("file:/home/kordjam/Downloads/mylbjtest/target/classes/Pclassifier_scala.lc")
        lexFilePath = new URL(new URL("file:"), "./data/" + getClassNameForClassifier + ".lex") //new java.net.URL("file:/home/kordjam/Downloads/mylbjtest/target/classes/Pclassifier_scala.lex")
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
          val oracle = Attribute.entitiesToLBJFeature(label)
          setLabeler(oracle)
        }

        if (feature != null) {
          setExtractor(lbpFeatures)
        }

        // Looks like we have to build the lexicon
        lcFilePath = new URL(new URL("file:"), "./data/" + getClassNameForClassifier + ".lc") //new java.net.URL("file:/home/kordjam/Downloads/mylbjtest/target/classes/Pclassifier_scala.lc")
        lexFilePath = new URL(new URL("file:"), "./data/" + getClassNameForClassifier + ".lex") //new java.net.URL("file:/home/kordjam/Downloads/mylbjtest/target/classes/Pclassifier_scala.lex")
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
        val oracle = Attribute.entitiesToLBJFeature(label)
        setLabeler(oracle)
      }

      if (feature != null) {
        setExtractor(lbpFeatures)
      }

      // Looks like we have to build the lexicon
      lcFilePath = new URL(new URL("file:"), "./data/" + getClassNameForClassifier + ".lc") //new java.net.URL("file:/home/kordjam/Downloads/mylbjtest/target/classes/Pclassifier_scala.lc")
      lexFilePath = new URL(new URL("file:"), "./data/" + getClassNameForClassifier + ".lex") //new java.net.URL("file:/home/kordjam/Downloads/mylbjtest/target/classes/Pclassifier_scala.lex")
      val out1: ExceptionlessOutputStream = ExceptionlessOutputStream.openCompressedStream(lexFilePath)
      if (lexicon == null) out1.writeInt(0)
      else lexicon.write(out1)
      out1.close()
      val out2: ExceptionlessOutputStream = ExceptionlessOutputStream.openCompressedStream(lcFilePath)
      write(out2)
      out2.close()
      readLexiconOnDemand = true

      val p: SparseAveragedPerceptron.Parameters =
        new SparseAveragedPerceptron.Parameters();
      p.learningRate = .1;
      p.thickness = 3;
      //p.thickness = {{ 1 -> 3 : .5 }};
      baseLTU = new SparseAveragedPerceptron(p);
      //setParameters(p)
      //Thickness(3)
      //learningRate=0.1
    }

  }

  def learn(iteration: Int): Unit = {
    learn(iteration, this.fromData)
  }

  def learn(iteration: Int, data: Iterable[T]): Unit = {
    println("Learn with" + data.size)
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
      case head :: more => {
        acc match {
          case (train, test) => {
            if (i == curr) {
              // we found the test part
              chunkData(more, i, curr + 1, (train, head))
            } else {
              chunkData(more, i, curr + 1, (head ++ train, test))
            }
          }
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
        i =>
          {
            chunkData(groups, i, 0, (Nil, Nil))
          }
      }
    }

    def printTestResult(result: (String, (Double, Double, Double))): Unit = {
      result match {
        case (label, (f1, precision, recall)) => {
          println(s"  $label    $f1    $precision     $recall   ")
        }
      }
    }

    val results = loops.zipWithIndex map {
      case ((trainingSet, testingSet), idx) => {
        println(s"Running fold $idx")
        println(s"Learn with ${trainingSet.size}")
        println(s"Test with ${testingSet.size}")

        this.classifier.forget()
        this.learn(10, trainingSet)
        val testResult = this.test(testingSet)
        testResult
      }
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
      case (label, l) => {
        val t = l.length
        val avg = avgTuple(l.map(_._2).reduce(sumTuple), t)

        printTestResult((label, avg))
      }
    })
  }

  /** User defines the following parts */
  /** Labels
    * @return
    */
  def label: Attribute[T]

  /** A windows of attributes
    * @param before always negative (or 0)
    * @param after always positive (or 0)
    */
  def windowWithIn[U <: AnyRef](before: Int, after: Int, att: List[Attribute[T]])(implicit uTag: ClassTag[U]): Attribute[T] = {
    val fls = datamodel.getRelatedFieldsBetween[T, U]
    getWindowWithFilters(before, after, fls.toList, att)
  }

  def window(before: Int, after: Int)(att: List[Attribute[T]]): Attribute[T] = {
    getWindowWithFilters(before, after, Nil, att)
  }

  private def getWindowWithFilters(before: Int, after: Int, filters: List[Symbol], att: List[Attribute[T]]): Attribute[T] = {
    new AttributeWithWindow[T](this.datamodel, before, after, filters, att)
  }

  def using(atts: List[Attribute[T]]*): List[Attribute[T]] = {
    atts.toList.flatten
  }

  def using(l: Learner): Learner = {
    l
  }
  def nextWithIn[U <: AnyRef](att: List[Attribute[T]])(implicit uTag: ClassTag[U]): Attribute[T] = {
    this.windowWithIn[U](0, 1, att.toList)
  }

  def prevWithIn[U <: AnyRef](att: Attribute[T]*)(implicit uTag: ClassTag[U]): Attribute[T] = {
    this.windowWithIn[U](-1, 0, att.toList)
  }

  def nextOf(att: List[Attribute[T]]): Attribute[T] = {
    window(0, 1)(att)
  }

  def prevOf(att: List[Attribute[T]]): Attribute[T] = {
    window(0, 1)(att)
  }

  // TODO: remove this
  def ~~[T](atts: Attribute[T]*) = atts.toList

  def relationalAttribute[U <: AnyRef](implicit uTag: ClassTag[U]): Attribute[T] = {
    val fts: List[Attribute[U]] = this.datamodel.getAllAttributeOf[U]
    relationalAttribute[U](fts)
  }
  def relationalAttribute[U <: AnyRef](ls: List[Attribute[U]])(implicit uTag: ClassTag[U]): Attribute[T] = {
    val fts = this.datamodel.getAllAttributeOf[U]
    new RelationalFeature[T, U](this.datamodel, fts)
  }
}
