package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator
import edu.illinois.cs.cogcomp.lbj.pos.{ POSLabeledUnknownWordParser }
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete
import edu.illinois.cs.cogcomp.nlp.corpusreaders.PennTreebankPOSReader
import edu.illinois.cs.cogcomp.saul.parser.LBJIteratorParserScala
import edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures.toyDataGenerator
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

    POSDataModel.tokens populate trainData
    POSDataModel.tokens.populate(testData, train = false)

    POSDataModel.isTraining = true

    /** pre-process the baseline systems */
    BaselineClassifier.learn(1)
    MikheevClassifier.learn(1)

    // This is doubled for some reason
    println(s"Should be 1044112 but is ")
    print(trainData.map(x => wordForm(x)).distinct.map(w => BaselineClassifier.classifier.observed(w)).sum)
    println()

    val unknownTrainData = trainData.filter(x => BaselineClassifier.classifier.observedCount(wordForm(x)) <= 2 * POSLabeledUnknownWordParser.threshold)

    (0 until 50).foreach(iter => {
      println(s"Training POS Tagger iteration $iter out of 50")
      POSTaggerKnown.learn(1)
      POSTaggerUnknown.learn(1, unknownTrainData)
      POSDataModel.featureCacheMap.clear()
    })

    // Inconsistent values here
    println(POSTaggerKnown.isTraining)
    println(POSTaggerUnknown.isTraining)

    POSDataModel.isTraining = false

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

object MikheevClassifierApp {
  def main(args: Array[String]): Unit = {
    val trainDataReader = new PennTreebankPOSReader("train")
    //    trainDataReader.readFile("../data/POS/00-18.br")
    trainDataReader.readFile("../data/POS/00-18_small.br")
    val trainData = trainDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    val testDataReader = new PennTreebankPOSReader("test")
    testDataReader.readFile("../data/POS/22-24.br")
    val testData = testDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    POSDataModel.tokens populate trainData

    MikheevClassifier.learn(1)
    MikheevClassifier.test(testData)

    val sampleInput = toyDataGenerator.generateToyTextAnnotation(1).head.getView(ViewNames.TOKENS).getConstituents.get(1)
    println(BaselineClassifier.classifier.discreteValue(sampleInput))
  }
}

object POSTaggerBaselineApp {
  def main(args: Array[String]): Unit = {
    val trainDataReader = new PennTreebankPOSReader("train")
    trainDataReader.readFile("../data/POS/00-18.br")
    val trainData = trainDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    val testDataReader = new PennTreebankPOSReader("test")
    testDataReader.readFile("../data/POS/22-24.br")
    val testData = testDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    POSDataModel.tokens populate trainData

    // baseline and mikheev just count, they don't learn -- so one iteration should be enough
    BaselineClassifier.learn(1)
    BaselineClassifier.test(testData)

    val sampleInput = toyDataGenerator.generateToyTextAnnotation(1).head.getView(ViewNames.TOKENS).getConstituents.get(1)
    println(BaselineClassifier.classifier.discreteValue(sampleInput))
  }
}

