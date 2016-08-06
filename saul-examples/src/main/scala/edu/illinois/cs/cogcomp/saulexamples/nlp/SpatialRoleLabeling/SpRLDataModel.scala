/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.edison.features.factory.{ LinearPosition, ParseHeadWordPOS, ParsePath, SubcategorizationFrame }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._

import scala.collection.JavaConverters._

/** Created by taher on 7/28/16.
  */
object SpRLDataModel extends DataModel {

  val parseView = ViewNames.PARSE_STANFORD

  val sentences = node[Sentence]
  val tokens = node[Constituent]((x: Constituent) => getConstituentId(x))
  val relations = node[Relation]

  val sentencesToTokens = edge(sentences, tokens)
  sentencesToTokens.addSensor(sentenceToTokens _)

  // Classification labels
  val relationType = property(relations) {
    x: Relation => x.getRelationName
  }

  val isSpatialIndicator = property(tokens) {
    x: Constituent => x.getIncomingRelations().asScala.exists(x => x.getRelationName.endsWith("sp"))
  }

  val isLandmark = property(tokens) {
    x: Constituent => x.getOutgoingRelations().asScala.exists(x => x.getRelationName.startsWith("lm"))
  }

  val isTrajector = property(tokens) {
    x: Constituent => x.getOutgoingRelations().asScala.exists(x => x.getRelationName.startsWith("tr"))
  }

  // relation features
  val argPosTag = property(relations) {
    x: Relation => getPosTag(x.getSource)
  }
  val indicatorPosTag = property(relations) {
    x: Relation => getPosTag(x.getTarget)
  }

  val argLemma = property(relations) {
    x: Relation => getLemma(x.getSource)
  }
  val indicatorLemma = property(relations) {
    x: Relation => getLemma(x.getTarget)
  }

  val argSubCategorization = property(relations) {
    x: Relation => getFeature(x.getSource, new SubcategorizationFrame(parseView))
  }
  val indicatorSubCategorization = property(relations) {
    x: Relation => getFeature(x.getTarget, new SubcategorizationFrame(parseView))
  }

  val argHeadword = property(relations) {
    x: Relation => getFeature(x.getSource, new ParseHeadWordPOS(parseView))
  }
  val indicatorHeadword = property(relations) {
    x: Relation => getFeature(x.getTarget, new ParseHeadWordPOS(parseView))
  }

  val indicatorPath = property(relations) {
    x: Relation => getFeature(x.getTarget, new ParsePath(parseView))
  }

  val indicatorPosition = property(relations) {
    x: Relation => getFeature(x.getTarget, new LinearPosition())
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
