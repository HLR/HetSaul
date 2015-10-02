package edu.illinois.cs.cogcomp.examples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.examples.nlp.EmailSpam.Classifers.spamClassifier
import edu.illinois.cs.cogcomp.tutorial_related.{ Document, DocumentReader }

import scala.collection.JavaConversions._
/** Created by Parisa on 6/9/15.
  */
object Spamapp {

  def main(args: Array[String]): Unit = {
    val dat: List[Document] = new DocumentReader("./data/EmailSpam/train").docs.toList
    val dat2: List[Document] = new DocumentReader("./data/EmailSpam/test").docs.toList
    spamDataModel ++ dat
    spamClassifier.learn(1)
    spamDataModel.testWith(dat2)
    spamClassifier.test()
  }
}
