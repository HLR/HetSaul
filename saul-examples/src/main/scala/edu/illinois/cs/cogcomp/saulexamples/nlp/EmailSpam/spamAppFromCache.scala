package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.core.io.IOUtils
import edu.illinois.cs.cogcomp.saulexamples.data.DocumentReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamClassifiers.spamClassifierWithCache

import scala.collection.JavaConversions._

/** Testing the functionality of the cache. [[edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamAppWithCache]]
  * produces the temporary model file need for this App to run.
  *
  * @author Christos Christodoulopoulos
  */
object spamAppFromCache {
  def main(args: Array[String]) {
    val testData = new DocumentReader("./data/EmailSpam/test").docs.toList

    spamDataModel.load("models/temp.model")
    spamClassifierWithCache.learn(30)
    spamClassifierWithCache.learn(30)
    spamDataModel.testWith(testData)
    spamClassifierWithCache.test(testData)
  }
}
