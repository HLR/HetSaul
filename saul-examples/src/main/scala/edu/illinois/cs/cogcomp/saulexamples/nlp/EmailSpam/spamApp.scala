package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saulexamples.data.{ DocumentReader, Document }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.classifers.spamClassifier

import scala.collection.JavaConversions._

/** Created by Parisa on 6/9/15.
  */
object spamApp {

  def main(args: Array[String]): Unit = {
    val dat: List[Document] = new DocumentReader("./data/EmailSpam/train").docs.toList
    val dat2: List[Document] = new DocumentReader("./data/EmailSpam/test").docs.toList
    spamDataModel populate dat
    spamClassifier.learn(1)
    spamDataModel.testWith(dat2)
    spamClassifier.test()
  }
}
