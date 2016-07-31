/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation, Sentence, TextAnnotation }
import edu.illinois.cs.cogcomp.edison.features.{ FeatureExtractor, FeatureUtilities }
import edu.illinois.cs.cogcomp.edison.features.factory.{ ParseHeadWordPOS, ParsePath, SubcategorizationFrame }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLSensors._

/** Created by taher on 7/28/16.
  */
object SpRLDataModel extends DataModel {
  val sentences = node[Sentence]
  val tokens = node[Constituent]

  val sentencesToTokens = edge(sentences, tokens)

  val parseView = ViewNames.PARSE_STANFORD

  sentencesToTokens.addSensor(sentenceToTokens _)

  // Classification labels
  val isSpatialIndicator = property(tokens, "sp") {
    x: Constituent => x.getTextAnnotation.getView("sprl-SpatialIndicator").getLabelsCovering(x).contains("SpatialIndicator")
  }
  val isLandmark = property(tokens, "lm") {
    x: Constituent => x.getTextAnnotation.getView("sprl-Landmark").getLabelsCovering(x).contains("Landmark")
  }
  val isTrajector = property(tokens, "tr") {
    x: Constituent => x.getTextAnnotation.getView("sprl-Trajector").getLabelsCovering(x).contains("Trajector")
  }

  // features
  val posTag = property(tokens, "pos") {
    x: Constituent => getPOS(x)
  }
  val lemma = property(tokens, "pLem") {
    x: Constituent => getLemma(x)
  }
  val subcategorization = property(tokens, "subcatC") {
    x: Constituent => fexFeatureExtractor(x, new SubcategorizationFrame(parseView))
  }
  val headword = property(tokens, "headC") {
    x: Constituent => fexFeatureExtractor(x, new ParseHeadWordPOS(parseView))
  }
  //  val path = property(tokens, "pathC") {
  //    x: Constituent => fexFeatureExtractor(x, new ParsePath(parseView))
  //  }
}
