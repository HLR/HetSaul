/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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
  val label = property(constituents, "label") {
    x: Constituent => x.getLabel
  }

  val constituentContent = property(constituents, "consContent") {
    x: Constituent => x.getSpan.toString
  }

  val documentContent = property(documents, "docContent") {
    x: TextAnnotation => x.toString
  }

  val sentenceContent = property(sentences, "sentenceContent") {
    x: Sentence => x.getText
  }

  /** Edge Types */
  val docToSen = edge(documents, sentences)

  val senToCons = edge(sentences, constituents)

  val docToCons = edge(documents, constituents)

  val consToCons = edge(constituents, constituents)
}
