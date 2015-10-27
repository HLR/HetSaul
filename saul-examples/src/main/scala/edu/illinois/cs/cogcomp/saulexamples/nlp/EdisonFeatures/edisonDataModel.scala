package edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures

import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

object edisonDataModel extends DataModel {

  /** Node Types */
  val documents = node[TextAnnotation]

  val sentences = node[Sentence]

  val relations = node[Relation]

  val constituents = node[Constituent]

  /** Property Types */
  val label = property[Constituent]("label") {
    x: Constituent => x.getLabel
  }

  val constituentContent = property[Constituent]("consContent") {
    x: Constituent => x.getSpan.toString
  }

  val documentContent = property[TextAnnotation]("docContent") {
    x: TextAnnotation => x.toString
  }

  val sentenceContent = property[Sentence]("sentenceContent") {
    x: Sentence => x.getText
  }

  /** Edge Types */
  val docToSen = edge(documents, sentences)

  val senToCons = edge(sentences, constituents)

  val docToCons = edge(documents, constituents)

  val consToCons = edge(constituents, constituents)
}
