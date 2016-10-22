/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.Document

import scala.collection.JavaConversions._

object SpamDataModel extends DataModel {
  val docs = node[Document]

  val wordFeature = property(docs, "wordF") {
    x: Document => x.getWords.toList
  }

  val bigramFeature = property(docs, "bigram") {
    x: Document =>
      val words = x.getWords.toList

      /** bigram features */
      words.sliding(2).map(_.mkString("-")).toList
  }

  val spamLabel = property(docs, "label") {
    x: Document => x.getLabel
  }
}
