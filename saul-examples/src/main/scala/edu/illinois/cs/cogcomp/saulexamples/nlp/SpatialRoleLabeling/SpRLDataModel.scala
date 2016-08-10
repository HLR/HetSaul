/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.edison.features.factory.{LinearPosition, ParseHeadWordPOS, ParsePath, SubcategorizationFrame}
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLClassifiers.spatialIndicatorClassifier

import scala.collection.JavaConverters._

/** Created by taher on 7/28/16.
  */
object SpRLDataModel extends DataModel {

  val parseView = ViewNames.PARSE_STANFORD

  val sentences = node[Sentence]
  val tokens = node[Constituent]((x: Constituent) => getConstituentId(x))
  val pairs = node[Relation]

  val sentencesToTokens = edge(sentences, tokens)
  sentencesToTokens.addSensor(sentenceToTokens _)

  // Classification labels
  val pairType = property(pairs) {
    x: Relation => x.getRelationName
  }

  val isSpatialIndicator = property(pairs) {
    x: Relation => !x.getRelationName.equals("none")
  }

  val isLandmark = property(pairs) {
    x: Relation => x.getRelationName.equals("lm")
  }

  val isTrajector = property(pairs) {
    x: Relation => x.getRelationName.equals("tr")
  }

  // relation features
  val argPosTag = property(pairs) {
    x: Relation => getPosTag(x.getSource)
  }
  val pivotPosTag = property(pairs) {
    x: Relation => getPosTag(x.getTarget)
  }

  val argLemma = property(pairs) {
    x: Relation => getLemma(x.getSource)
  }
  val pivotLemma = property(pairs) {
    x: Relation => getLemma(x.getTarget)
  }

  val argSubCategorization = property(pairs) {
    x: Relation => getFeature(x.getSource, new SubcategorizationFrame(parseView))
  }
  val pivotSubCategorization = property(pairs) {
    x: Relation => getFeature(x.getTarget, new SubcategorizationFrame(parseView))
  }

  val argHeadword = property(pairs) {
    x: Relation => getFeature(x.getSource, new ParseHeadWordPOS(parseView))
  }
  val pivotHeadword = property(pairs) {
    x: Relation => getFeature(x.getTarget, new ParseHeadWordPOS(parseView))
  }

  val pathToPivot = property(pairs) {
    x: Relation => getFeature(x.getTarget, new ParsePath(parseView))
  }

  val positionRelativeToPivot = property(pairs) {
    x: Relation => getFeature(x.getTarget, new LinearPosition())
  }

  val isPivot = property(pairs) {
    x: Relation => x.getTarget.getSpan == x.getSource.getSpan
  }
  val pipeLineIsSp = property(pairs){
    x: Relation => spatialIndicatorClassifier(x)
  }
  // tokens features
  val posTag = property(tokens) {
    x: Constituent => getPosTag(x)
  }

  val lemma = property(tokens) {
    x: Constituent => getLemma(x)
  }

  val subCategorization = property(tokens) {
    x: Constituent => getFeature(x, new SubcategorizationFrame(parseView))
  }

  val headword = property(tokens) {
    x: Constituent => getFeature(x, new ParseHeadWordPOS(parseView))
  }

  def getConstituentId(x: Constituent): String =
    x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSentenceId + ":" + x.getSpan

  def getUniqueSentenceId(x: Constituent): String =
    x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSentenceId

}
