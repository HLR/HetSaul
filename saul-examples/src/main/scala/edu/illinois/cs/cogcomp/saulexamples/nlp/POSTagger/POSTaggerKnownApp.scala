package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator
import edu.illinois.cs.cogcomp.nlp.corpusreaders.PennTreebankPOSReader
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures.toyDataGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors

import scala.collection.JavaConversions._

object POSTaggerKnownApp {
  def main(args: Array[String]): Unit = {
    val trainDataReader = new PennTreebankPOSReader("train")
    trainDataReader.readFile("../data/POS/00-18.br")
    //    val trainData = trainDataReader.getTextAnnotations.subList(0, 5).flatMap(commonSensors.textAnnotationToTokens(_).subList(0, 5))
    val trainData = trainDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    val testDataReader = new PennTreebankPOSReader("test")
    testDataReader.readFile("../data/POS/22-24.br")
    val testData = testDataReader.getTextAnnotations.flatMap(commonSensors.textAnnotationToTokens)

    POSDataModel.tokens populate trainData

    /** preprocess the baseline */
    BaselineClassifier.learn(1)

    POSTaggerKnown.learn(1000)
    POSTaggerKnown.test(trainData)
    //    POSTaggerKnown.test(testData)
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

object POSTaggerTestPropertiesApp {
  def main(args: Array[String]): Unit = {
    val dummyData = DummyTextAnnotationGenerator.generateAnnotatedTextAnnotation(Array(ViewNames.POS), false)
    val cons = dummyData.getView(ViewNames.TOKENS).getConstituents
    POSDataModel.tokens populate cons

    println(cons)

    val consThe = cons(0)
    val consConstruction = cons(1)
    val consOf = cons(2)

    // wordForm
    println(POSDataModel.wordForm(consThe) == "The")
    println(POSDataModel.wordForm(consConstruction) == "construction")
    println(POSDataModel.wordForm(consOf) == "of")

    // label
    println(POSDataModel.POSLabel(consThe) == "DT")
    println(POSDataModel.POSLabel(consConstruction) == "NN")
    println(POSDataModel.POSLabel(consOf) == "IN")

    // label or baseline
    BaselineClassifier.learn(1)
    println(POSDataModel.labelOrBaseline(consThe) == "DT")
    println(POSDataModel.labelOrBaseline(consConstruction) == "NN")
    println(POSDataModel.labelOrBaseline(consOf) == "IN")

    POSTaggerKnown.isTraining = true
    POSTaggerKnown.learn(10)

    // labelOneBefore
    // gold labels
    POSTaggerKnown.isTraining = true
    println(POSDataModel.labelOneBefore(consThe) == "")
    println(POSDataModel.labelOneBefore(consConstruction) == "DT")
    println(POSDataModel.labelOneBefore(consOf) == "NN")
    // prediction labels
    POSTaggerKnown.isTraining = false
    println(POSDataModel.labelOneBefore(consThe) == "")
    println(POSDataModel.labelOneBefore(consConstruction) == "DT")
    println(POSDataModel.labelOneBefore(consOf) == "NN")

    // labelTwoBefore
    // gold labels
    POSTaggerKnown.isTraining = true
    println(POSDataModel.labelTwoBefore(consThe) == "")
    println(POSDataModel.labelTwoBefore(consConstruction) == "")
    println(POSDataModel.labelTwoBefore(consOf) == "DT")
    // prediction labels
    POSTaggerKnown.isTraining = false
    println(POSDataModel.labelTwoAfter(consThe) == "IN")
    println(POSDataModel.labelTwoAfter(consConstruction) == "DT")
    println(POSDataModel.labelTwoAfter(consOf) == "NN")

    // labelOneAfter
    // gold labels
    POSTaggerKnown.isTraining = true
    println(POSDataModel.labelOneAfter(consThe) == "NN")
    println(POSDataModel.labelOneAfter(consConstruction) == "IN")
    println(POSDataModel.labelOneAfter(consOf) == "DT")
    // prediction labels
    POSTaggerKnown.isTraining = false
    println(POSDataModel.labelOneAfter(consThe) == "NN")
    println(POSDataModel.labelOneAfter(consConstruction) == "IN")
    println(POSDataModel.labelOneAfter(consOf) == "DT")

    // labelTwoAfter
    // gold labels
    POSTaggerKnown.isTraining = true
    println(POSDataModel.labelTwoAfter(consThe) == "IN")
    println(POSDataModel.labelTwoAfter(consConstruction) == "DT")
    println(POSDataModel.labelTwoAfter(consOf) == "NN")
    // prediction labels
    POSTaggerKnown.isTraining = false
    println(POSDataModel.labelTwoAfter(consThe) == "IN")
    println(POSDataModel.labelTwoAfter(consConstruction) == "DT")
    println(POSDataModel.labelTwoAfter(consOf) == "NN")
  }
}
