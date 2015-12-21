package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saulexamples.data.DocumentReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamClassifiers.spamClassifierWithCache

import scala.collection.JavaConversions._

object spamAppWithCache {

  def main(args: Array[String]): Unit = {
    /** Defining the data and specifying it's location  */
    val trainData = new DocumentReader("./data/EmailSpam/train").docs.toList
    val testData = new DocumentReader("./data/EmailSpam/test").docs.toList
    spamDataModel.docs populate trainData
    spamDataModel.deriveInstances()
    spamDataModel.write("models/temp.model")

    spamClassifierWithCache.learn(30)
    spamClassifierWithCache.learn(30)
    spamDataModel.testWith(testData)
    spamClassifierWithCache.test(testData)
  }
}
