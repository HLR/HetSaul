package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saulexamples.data.DocumentReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamClassifiers.{ deserializedSpamClassifier, spamClassifier }

import scala.collection.JavaConversions._

object SpamApp {
  def main2(args: Array[String]): Unit = {
    /** Defining the data and specifying it's location  */
    val trainData = new DocumentReader("./data/EmailSpam/train").docs.toList
    val testData = new DocumentReader("./data/EmailSpam/test").docs.toList
    spamDataModel.docs populate trainData
    spamClassifier.learn(30)
    spamDataModel.testWith(testData)
    spamClassifier.test(testData)
  }
}

object SpamClassifierSerialization {
  def main(args: Array[String]): Unit = {
    val trainData = new DocumentReader("./data/EmailSpam/train").docs.toList
    val testData = new DocumentReader("./data/EmailSpam/test").docs.toList
    spamDataModel.docs populate trainData
    spamClassifier.learn(30)

    spamClassifier.save()

    println(deserializedSpamClassifier.classifier.getPrunedLexiconSize)
    deserializedSpamClassifier.load(spamClassifier.lcFilePath, spamClassifier.lexFilePath)

    val predictionsBeforeSerialization = testData.map(spamClassifier.classifier.discreteValue(_))
    val predictionsAfterSerialization = testData.map(deserializedSpamClassifier.classifier.discreteValue(_))
    println(predictionsBeforeSerialization.mkString("/"))
    println(predictionsAfterSerialization.mkString("/"))
  }
}