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
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors

import scala.collection.JavaConversions._

object Constants{
  private val prefix = "../data/POS/"
  val trainData = prefix + "00-18.br"
  val trainAndDevData = prefix + "00-21.br"
  val testData = prefix + "22-24.br"
}

object POSTaggerApp {
  def main(args: Array[String]): Unit = {
    val trainDataReader = new PennTreebankPOSReader("train")
    trainDataReader.readFile(Constants.trainAndDevData)
    //    val trainData = trainDataReader.getTextAnnotations.subList(0, 5).flatMap(commonSensors.textAnnotationToTokens(_).subList(0, 5))
    val trainData = trainDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    val testDataReader = new PennTreebankPOSReader("test")
    testDataReader.readFile(Constants.testData)
    val testData = testDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    POSDataModel.tokens populate trainData
    POSDataModel.tokens.populate(testData, train = false)

    /** preprocess the baseline systems */
    BaselineClassifier.learn(1)
    MikheevClassifier.learn(1)

    val unknownTrainData = trainData.filter(x => BaselineClassifier.classifier.observedCount(x.toString) <= POSLabeledUnknownWordParser.threshold)

    POSTaggerKnown.learn(50)
    POSTaggerUnknown.learn(50, unknownTrainData)

    /*
        (0 until 50).foreach(_ => {
          POSTaggerKnown.learn(1)
          POSTaggerUnknown.learn(1, unknownTrainData)
          POSDataModel.featureCacheMap.clear()
        })
    */

    POSTaggerKnown.test(testData)
//    POSTaggerUnknown.test(testData)

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

