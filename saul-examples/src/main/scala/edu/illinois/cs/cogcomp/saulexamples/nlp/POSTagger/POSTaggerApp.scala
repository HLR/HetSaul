package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.nlp.corpusreaders.PennTreebankPOSReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors

import scala.collection.JavaConversions._

object POSTaggerApp {

  def main(args: Array[String]) {
    val trainDataReader = new PennTreebankPOSReader("train")

    trainDataReader.readFile("./data/POSTagger/POS/00-18.br")
    val trainData = trainDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    val testDataReader = new PennTreebankPOSReader("test")
    testDataReader.readFile("./data/POSTagger/POS/22-24.br")
    val testData = testDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    POSDataModel.tokens populate trainData

    POSClassifier.learn(30)
    POSDataModel.testWith(testData)

    POSClassifier.test(testData)
  }
}
