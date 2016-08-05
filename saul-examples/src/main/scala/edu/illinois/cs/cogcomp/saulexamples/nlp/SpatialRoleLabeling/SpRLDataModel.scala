/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.edison.features.{ FeatureExtractor, FeatureUtilities }
import edu.illinois.cs.cogcomp.edison.features.factory.{ ParseHeadWordPOS, ParsePath, SubcategorizationFrame }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLSensors._

/** Created by taher on 7/28/16.
  */
object SpRLDataModel extends DataModel {

  val parseView = ViewNames.PARSE_STANFORD

  val sentences = node[Sentence]
  val tokens = node[Constituent]((x: Constituent) => getConstituentId(x))
  val pairs = join(tokens, tokens)((a, b) =>
    getUniqueSentenceId(a) == getUniqueSentenceId(b) &&
      posTag(a).startsWith("NN") &&
      isCandidate(b) &&
      getConstituentId(a) != getConstituentId(b))

  val sentencesToTokens = edge(sentences, tokens)
  sentencesToTokens.addSensor(sentenceToTokens _)

  // Classification labels
  //  val pairType = property(nounToCandidate) {
  //    x: (Constituent, Constituent) => x._1.getLabel + "-" + x._2.getLabel
  //  }

  val isSpatialIndicator = property(tokens) {
    x: Constituent => x.getTextAnnotation.getView("sprl-SpatialIndicator").getLabelsCovering(x).contains("SpatialIndicator")
  }

  val isLandmark = property(tokens) {
    x: Constituent => x.getTextAnnotation.getView("sprl-Landmark").getLabelsCovering(x).contains("Landmark")
  }

  val isTrajector = property(tokens) {
    x: Constituent => x.getTextAnnotation.getView("sprl-Trajector").getLabelsCovering(x).contains("Trajector")
  }

  // features
  val posTag = property(tokens) {
    x: Constituent => getPOS(x)
  }

  val lemma = property(tokens) {
    x: Constituent => getLemma(x)
  }

  val subcategorization = property(tokens) {
    x: Constituent => getFeature(x, new SubcategorizationFrame(parseView))
  }

  val headword = property(tokens) {
    x: Constituent => getFeature(x, new ParseHeadWordPOS(parseView))
  }

  val isPivot = property(tokens) {
    x: Constituent => x.getAttribute("pivot") == "true"
  }
  //  val path = property(tokens) {
  //     x: Constituent => getFeature(x, new ParsePath(parseView))
  //  }

  def getConstituentId(x: Constituent): String =
    x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSentenceId + ":" + x.getSpan

  def getUniqueSentenceId(x: Constituent): String =
    x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSentenceId

}
