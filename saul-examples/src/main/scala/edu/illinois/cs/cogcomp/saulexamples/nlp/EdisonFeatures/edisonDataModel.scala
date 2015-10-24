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

  val constituentAddress = property[Constituent]("address") {
    x: Constituent => x.getSpan.toString
  }

  val constituentViewName = property[Constituent]("constituentViewName") {
    x: Constituent => x.getViewName
  }

  val relationViewName = property[Relation]("relationVeiwName") {
    x: Relation => x.getSource.getViewName
  }
  val docNumSentences = property[TextAnnotation]("doc") {
    x: TextAnnotation => x.getNumberOfSentences.toString
  }
  val sentenceContent = property[Sentence]("sentnce") {
    x: Sentence => x.getText
  }

  /** Edge Types */
  val docToSen = edge(documents, sentences, 'docToSen)

  val senToCons = edge(sentences, constituents, 'senToCons)

  val docToCons = edge(documents, constituents, 'senToCons)

  val consToCons = edge(constituents, constituents, 'consToCons)
}
