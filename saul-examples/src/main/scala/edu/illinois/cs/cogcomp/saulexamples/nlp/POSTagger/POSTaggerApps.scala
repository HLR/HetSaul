package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
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
  object POSExperimentType extends Enumeration {
    val TrainAndTest, TestFromModel = Value
  }

  def main(args: Array[String]): Unit = {
    /** Choose the experiment you're interested in by changing the following line */
    val testType = POSExperimentType.TrainAndTest

    testType match {
      case POSExperimentType.TrainAndTest => trainAndTest()
      case POSExperimentType.TestFromModel => testWithPretrainedModels()
    }
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

    /** train the learning models */
    val unknownTrainData = trainData.filter(x => BaselineClassifier.classifier.observedCount(wordForm(x)) <= POSLabeledUnknownWordParser.threshold)
    POSTaggerKnown.learn(50)
    POSTaggerUnknown.learn(50, unknownTrainData)

    /** test the resulting model */
    testPOSTagger()

    // saving all the models
    saveModels()
  }

  /** Loading the serialized models as a dependency */
  def testWithPretrainedModels(): Unit = {
    POSDataModel.tokens.populate(testData, train = false)

    POSClassifiers.loadModelsFromPackage()

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

