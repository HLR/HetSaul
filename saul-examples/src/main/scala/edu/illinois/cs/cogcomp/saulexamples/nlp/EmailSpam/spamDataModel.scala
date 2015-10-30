package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.Document

import scala.collection.JavaConversions._

object spamDataModel extends DataModel {

  val docs = node[Document]

  val wordFeature = property[Document]("wordF") {
    x: Document => x.getWords.toList
  }

  val bigramFeature = property[Document]("bigram") {
    x: Document =>
      val words = x.getWords.toList

      /** bigram features */
      words.sliding(2).map(_.mkString("-")).toList
  }

  val spamLabel = property[Document]("label") {
    x: Document => x.getLabel
  }
}
