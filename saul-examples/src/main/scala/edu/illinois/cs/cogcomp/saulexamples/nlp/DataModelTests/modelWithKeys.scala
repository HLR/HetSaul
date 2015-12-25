package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.collection.mutable.{ Map => MutableMap }

object modelWithKeys extends DataModel {

  /** Node Types */
  val document = node[TextAnnotation]
  val sentence = node[Sentence]
  val tokens = node[Constituent]

  /** Property Types */
  val label = property(tokens, "label") {
    x: Constituent => x.getLabel
  }

  val docFeatureExample = property(document, "doc") {
    x: TextAnnotation => x.getNumberOfSentences.toString
  }
  val sentenceFeatureExample = property(sentence, "sentence") {
    x: Sentence => x.getText
  }

  /** Edge Types */
  val docTosen = edge(document, sentence, 'dTos)
  docTosen.populateWith((d, s) => d.getId == s.getSentenceConstituent.getTextAnnotation.getId)
}
