package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.nlp.corpusreaders.PennTreebankPOSReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures.toyDataGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors

import scala.collection.JavaConversions._

object POSTaggerApp {

  def main(args: Array[String]): Unit = {
    val trainDataReader = new PennTreebankPOSReader("train")
    trainDataReader.readFile("./data/POS/00-18.br")
    val trainData = trainDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    val testDataReader = new PennTreebankPOSReader("test")
    testDataReader.readFile("./data/POS/22-24.br")
    val testData = testDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    POSDataModel.tokens populate trainData

    POSClassifier.learn(30)
    POSDataModel.testWith(testData)

    POSClassifier.test(testData)
  }
}

object POSTaggerBaselineApp {
  def main(args: Array[String]): Unit = {
    val trainDataReader = new PennTreebankPOSReader("train")
    trainDataReader.readFile("./data/POS/00-18_small.br")
    val trainData = trainDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    val testDataReader = new PennTreebankPOSReader("test")
    testDataReader.readFile("./data/POS/22-24.br")
    val testData = testDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    POSDataModel.tokens populate trainData

    //val a = BaselineLabel.classifier.discreteValue("The")
    //println(a)

    //    val sampleInput = toyDataGenerator.generateToyTextAnnotation(1).head.getView(ViewNames.TOKENS).getConstituents.get(1)
    /*val sampleInput = trainData.toList.head
    println("Sample input = " + sampleInput)

    val combinedProperty = new CombinedDiscreteProperty[Constituent](List(POSDataModel.wordForm))
    val lbpFeatures = combinedProperty.makeClassifierWithName("")
    println(s"extractor = $lbpFeatures")

    println(lbpFeatures.classify(sampleInput))
    println(lbpFeatures.discreteValue(sampleInput))

    val prop: Property[Constituent] = POSDataModel.wordForm
    println(prop(sampleInput))*/

    //    val extractor = BaselineLabel.classifier.getExtractor
    //    println(s"extractor = $extractor")
    //    println(extractor.discreteValue(sampleInput))

    BaselineClassifier.learn(1)
    BaselineClassifier.test(testData)

    val sampleInput = toyDataGenerator.generateToyTextAnnotation(1).head.getView(ViewNames.TOKENS).getConstituents.get(1)
    println(BaselineClassifier.classifier.discreteValue(sampleInput))
  }
}
