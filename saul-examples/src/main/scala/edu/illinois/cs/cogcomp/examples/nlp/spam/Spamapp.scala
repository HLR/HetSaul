package edu.illinois.cs.cogcomp.examples.nlp.spam

import edu.illinois.cs.cogcomp.examples.nlp.spam.Classifers.spamClassifier
import edu.illinois.cs.cogcomp.tutorial_related.{Document, DocumentReader}

import scala.collection.JavaConversions._
/**
 * Created by Parisa on 6/9/15.
 */
object Spamapp {

  def main(args: Array[String]): Unit = {
  val dat:List[Document]=new DocumentReader("./saul-examples/src/test/resources/EmailSpam/train").docs.toList
  val dat2:  List[Document]=new DocumentReader("./saul-examples/src/test/resources/EmailSpam/test").docs.toList
  spamDataModel++ dat
  spamClassifier.learn(1)
  spamDataModel.testWith(dat2)
  spamClassifier.test()
  }
}
