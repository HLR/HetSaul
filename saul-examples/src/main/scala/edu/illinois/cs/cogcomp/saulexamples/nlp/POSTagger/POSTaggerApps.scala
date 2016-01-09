package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator
import edu.illinois.cs.cogcomp.lbj.pos.POSLabeledUnknownWordParser
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete
import edu.illinois.cs.cogcomp.nlp.corpusreaders.PennTreebankPOSReader
import edu.illinois.cs.cogcomp.saul.parser.LBJIteratorParserScala
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors

import scala.collection.JavaConversions._

object Constants {
  private val prefix = "../data/POS/"
  val trainData = prefix + "00-18.br"
  val trainDataSmall = prefix + "00-18_small.br"
  val trainAndDevData = prefix + "00-21.br"
  val testData = prefix + "22-24.br"
}

object POSTaggerApp {
  def main(args: Array[String]): Unit = {
    // If you want to use pre-trained model change it to false
    if (true)
      trainAndTest()
    else
      testWithPretrainedModels2()
  }

  /** Reading test and train data */
  lazy val (trainData, testData) = {
    val trainDataReader = new PennTreebankPOSReader("train")
    trainDataReader.readFile(Constants.trainAndDevData)

    var sentenceId = 0
    val trainData = trainDataReader.getTextAnnotations.flatMap(p => {
      val cons = commonSensors.textAnnotationToTokens(p)
      sentenceId += 1
      //      Adding a dummy attribute so that hashCode is different for each constituent
      cons.foreach(c => c.addAttribute("SentenceId", sentenceId.toString))
      cons
    })

    val testDataReader = new PennTreebankPOSReader("test")
    testDataReader.readFile(Constants.testData)
    val testData = testDataReader.getTextAnnotations.flatMap(p => {
      val cons = commonSensors.textAnnotationToTokens(p)
      sentenceId += 1
      //      Adding a dummy attribute so that hashCode is different for each constituent
      cons.foreach(c => c.addAttribute("SentenceId", sentenceId.toString))
      cons
    })
    (trainData, testData)
  }

  def trainAndTest(): Unit = {
    POSDataModel.tokens populate trainData
    POSDataModel.tokens.populate(testData, train = false)

    /** pre-process the baseline systems */
    BaselineClassifier.learn(1)
    MikheevClassifier.learn(1)

    val unknownTrainData = trainData.filter(x => BaselineClassifier.classifier.observedCount(wordForm(x)) <= POSLabeledUnknownWordParser.threshold)

    (0 until 10).foreach(iter => {
      println(s"Training POS Tagger iteration $iter out of 50")
      POSTaggerKnown.learn(1)
      POSTaggerUnknown.learn(1, unknownTrainData)
      //      POSDataModel.propertyCacheMap.clear()
    })

    testPOSTagger()

    // saving all the models
    BaselineClassifier.save()
    MikheevClassifier.save()
    POSTaggerKnown.save()
    POSTaggerUnknown.save()
  }

  /** Loading the serialized models as a dependency */
  def testWithPretrainedModels(): Unit = {
    val toyConstituents = DummyTextAnnotationGenerator.generateBasicTextAnnotation(1).getView(ViewNames.TOKENS)
    POSDataModel.tokens.populate(toyConstituents, train = false)

    POSClassifiers.loadModels()

    val baselineLabelMap = Map("To" -> "TO", "or" -> "CC", "not" -> "RB", ";" -> ":",
      "that" -> "IN", "is" -> "VBZ", "the" -> "DT", "question" -> "NN", "." -> ".")

    toyConstituents.foreach { cons =>
      println(BaselineClassifier.classifier.discreteValue(cons) == baselineLabelMap.getOrElse(cons.getSurfaceForm, ""))
      println(BaselineClassifier.classifier.discreteValue(cons) + cons.getSurfaceForm)
      //  println(MikheevClassifier.classifier.discreteValue(cons) )
      //  println(MikheevClassifier.classifier.discreteValue(cons))
      //  println(POSTaggerKnown.classifier.discreteValue(cons))
      //  println(POSTaggerUnknown.classifier.discreteValue(cons))

      val predicted = POSClassifiers.POSClassifier(cons)
      println("predicted = " + predicted)
    }
  }

  //TODO: remove the above function after making sure that deserialization is fine, and keep this one only
  def testWithPretrainedModels2(): Unit = {
    POSDataModel.tokens.populate(testData, train = false)

    BaselineClassifier.load()
    MikheevClassifier.load()
    POSTaggerKnown.load()
    POSTaggerUnknown.load()

    testPOSTagger()
  }

  def testPOSTagger(): Unit = {
    val tester = new TestDiscrete
    val testReader = new LBJIteratorParserScala[Constituent](testData)
    testReader.reset()

    testReader.data.foreach(cons => {
      val gold = POSDataModel.POSLabel(cons)
      val predicted = POSClassifiers.POSClassifier(cons)
      tester.reportPrediction(predicted, gold)
    })

    tester.printPerformance(System.out)
  }
}

