package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saulexamples.data.{ DocumentReader, Document }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamClassifers.spamClassifier

import scala.collection.JavaConversions._

object spamApp {

  def main(args: Array[String]): Unit = {
    val trainData = new DocumentReader("./data/EmailSpam/train").docs.toList
    val testData = new DocumentReader("./data/EmailSpam/test").docs.toList
    spamDataModel populate trainData
    spamClassifier.learn(1)
    spamDataModel.testWith(testData)
    spamClassifier.test()
  }
}
