package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.Document

import scala.collection.JavaConversions._

object spamDataModel extends DataModel {

  val docs = node[Document]

  val wordFeature = discreteAttributesGeneratorOf[Document]('wordF) {
    x: Document => x.getWords.toList
  }

  val bigramFeature = discreteAttributesGeneratorOf[Document]('bigram) {
    x: Document =>
      {
        val words = x.getWords.toList
        var big: List[String] = List()
        for (i <- 0 until words.size - 1)
          big = (words.get(i) + "-" + words.get(i + 1)) :: big
        big
      }
  }

  val spamLable = discreteAttributeOf[Document]('label) {
    x: Document => x.getLabel
  }
}
