package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.nlp.corpusreaders.PennTreebankPOSReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers._

import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import scala.collection.JavaConversions._

object POSTaggerApp {

  def main(args: Array[String]) {
    val trainDataReader = new PennTreebankPOSReader("train")

    trainDataReader.readFile("./data/POSTagger/POS/00-18.br")
    val trainData = trainDataReader.getTextAnnotations.flatMap(SemanticRoleLabeling.SRLSensors.textAnnotationToTokens)

    val testDataReader = new PennTreebankPOSReader("test")
    testDataReader.readFile("./data/POSTagger/POS/22-24.br")
    val testData = testDataReader.getTextAnnotations.flatMap(SemanticRoleLabeling.SRLSensors.textAnnotationToTokens)

    POSDataModel.tokens populate testData

    POSClassifier.learn(30)
    POSDataModel.testWith(testData)

    POSClassifier.test(testData)
  }
}
