/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.{ IntPair, ViewNames }
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.edison.features.helpers.PathFeatureHelper
import edu.illinois.cs.cogcomp.nlp.utilities.CollinsHeadFinder
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Triplet.{ SpRelation, SpRelationLabels }

import scala.collection.JavaConverters._
import scala.collection.immutable.HashSet
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

/** Created by taher on 7/28/16.
  */
object SpRLSensors extends Logging {
  val dependencyView = ViewNames.DEPENDENCY_STANFORD

  def sentencesToRelations(sentence: SpRLSentence): List[SpRelation] = {

    val relations = ListBuffer[SpRelation]()
    val constituents = sentence.getSentence.getView(ViewNames.TOKENS).asScala.toList
    val args = constituents.filter(x => isArgCandidate(x))
    val indicators: ListBuffer[Constituent] = getIndicatorCandidates(sentence.getSentence, Dictionaries.spLexicon)

    for (i <- indicators) {

      val rels = getIndicatorRelations(sentence.getRelations.asScala.toList, i, sentence.getOffset)
      for (tr <- args) {

        var label = SpRelationLabels.CANDIDATE
        val g = rels.find(r => isGoldTrajector(r, tr, sentence.getOffset) && r.getLandmark == null)
        if (g.isDefined) {
          label = Triplet.SpRelationLabels.GOLD
          relations += new SpRelation(sentence.getSentence, tr.getSpan, i.getSpan, null, label, g.get.getId)
        } else {
          relations += new SpRelation(sentence.getSentence, tr.getSpan, i.getSpan, null, label, "")
        }

        for (lm <- args) {
          if (lm.getSpan != tr.getSpan) {
            val g = rels.find(r => isGoldTrajector(r, tr, sentence.getOffset) && isGoldLandmark(r, lm, sentence.getOffset))
            if (g.isDefined) {
              label = Triplet.SpRelationLabels.GOLD
              relations += new SpRelation(sentence.getSentence, tr.getSpan, i.getSpan, lm.getSpan, label, g.get.getId)
            } else {
              label = Triplet.SpRelationLabels.CANDIDATE
              relations += new SpRelation(sentence.getSentence, tr.getSpan, i.getSpan, lm.getSpan, label, "")
            }
          }
        }
      }
    }

    relations.toList
  }

  // helper methods
  def getDependencyPath(ta: TextAnnotation, t1: Int, t2: Int): String = {

    def getRelationName(relations: List[Relation], c1: Constituent, c2: Constituent, dir: String): String = {
      val r = relations.find(x => (x.getSource == c1 && x.getTarget == c2) || (x.getSource == c2 && x.getTarget == c1))
      r match {
        case Some(r) => dir + r.getRelationName
        case None => ""
      }
    }

    val c1 = ta.getView(dependencyView).getConstituentsCoveringToken(t1).get(0)
    val c2 = ta.getView(dependencyView).getConstituentsCoveringToken(t2).get(0)

    val parse: TreeView = ta.getView(dependencyView).asInstanceOf[TreeView]

    val relations = parse.getRelations.asScala.toList
    val paths = PathFeatureHelper.getPathsToCommonAncestor(c1, c2, 400)

    val up = paths.getFirst.asScala.toList
    val down = paths.getSecond.asScala.toList

    val path: StringBuilder = new StringBuilder
    var i = 0
    while (i < up.size - 1) {
      path.append(getRelationName(relations, up(i), up(i + 1), "↑"))
      i += 1
    }
    i = down.size - 1
    while (i > 0) {
      path.append(getRelationName(relations, down(i), down(i - 1), "↓"))
      i -= 1
    }

    path.toString.toUpperCase
  }

  def getDependencyRelationsWith(c: Constituent, relationName: String): List[Relation] = {
    getDependencyRelations(c.getTextAnnotation)
      .filter(y => y.getRelationName.equalsIgnoreCase(relationName.toLowerCase) &&
        (y.getSource.getSpan == c.getSpan || y.getTarget.getSpan == c.getSpan))
  }

  def getDependencyRelations(ta: TextAnnotation): List[Relation] = {
    ta.getView(dependencyView).asInstanceOf[TreeView].getRelations.asScala.toList
  }

  private def getIndicatorCandidates(sentence: Sentence, lexicon: HashSet[String]): ListBuffer[Constituent] = {

    val beforeSp = "([^a-zA-Z\\d-]|^)"
    val afterSp = "[^a-zA-Z\\d-]"
    def contains(sentence: String, phrase: String): Boolean = {
      val pattern = new Regex(beforeSp + phrase + afterSp)
      pattern.findFirstIn(sentence.toLowerCase).isDefined
    }

    val indicators = ListBuffer[Constituent]()
    val matched = lexicon.filter(x => contains(sentence.getText, x)).toList
    for (m <- matched) {
      val pattern = new Regex(beforeSp + m + afterSp)
      val occurrences = pattern.findAllMatchIn(sentence.getText.toLowerCase).toList
      for (i <- occurrences) {
        if (!indicators.exists(x => x.getStartCharOffset == i.start && i.end == x.getEndCharOffset)) {
          val covering = sentence.getView(ViewNames.TOKENS).asScala
            .filter(x => i.start <= x.getStartCharOffset && x.getEndCharOffset <= i.end)
          val c = new Constituent("", "", sentence.getSentenceConstituent.getTextAnnotation,
            covering.head.getStartSpan, covering.last.getEndSpan)
          indicators += c
        }
      }
    }

    indicators
  }

  private def getIndicatorRelations(goldRelations: List[SpRLRelation], sp: Constituent, offset: IntPair): List[SpRLRelation] = {
    goldRelations.filter(x =>
      x.getSpatialIndicator.getStart.intValue() == sp.getStartCharOffset + offset.getFirst &&
        x.getSpatialIndicator.getText.trim.equalsIgnoreCase(sp.toString))
  }

  private def isArgCandidate(x: Constituent): Boolean = {
    CommonSensors.getPosTag(x).startsWith("NN") ||
      CommonSensors.getPosTag(x).startsWith("CD") ||
      CommonSensors.getPosTag(x).startsWith("PRP")
  }

  private def isGoldTrajector(r: SpRLRelation, tr: Constituent, offset: IntPair): Boolean = {
    tr != null && tr == getHeadword(r.getTrajector, tr.getTextAnnotation, offset)
  }

  private def isGoldLandmark(r: SpRLRelation, lm: Constituent, offset: IntPair): Boolean = {
    r.getLandmark != null && lm == getHeadword(r.getLandmark, lm.getTextAnnotation, offset)
  }

  private def getHeadword(t: SpRLAnnotation, ta: TextAnnotation, offset: IntPair): Constituent = {
    ta.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituentAtToken(getHeadwordId(t, ta, offset))
  }

  private def getHeadwordId(t: SpRLAnnotation, ta: TextAnnotation, offset: IntPair): Int = {

    val start = t.getStart().intValue() - offset.getFirst
    val end = t.getEnd().intValue() - offset.getFirst
    val startTokenId = ta.getTokenIdFromCharacterOffset(start)

    if (ta.getToken(startTokenId).equalsIgnoreCase(t.getText)) // single word
      return startTokenId

    val constituents = ta.getView(ViewNames.TOKENS).getConstituents.asScala.
      filter(x => x.getStartCharOffset >= start && x.getEndCharOffset <= end)

    val phrases = ta.getView(ViewNames.SHALLOW_PARSE).getConstituentsCoveringToken(startTokenId).asScala

    if (phrases.nonEmpty) {
      val phrase = phrases.head
      val tree: TreeView = ta.getView(ViewNames.PARSE_STANFORD).asInstanceOf[TreeView]
      val parsePhrase = tree.getParsePhrase(phrase)
      val headId = CollinsHeadFinder.getInstance.getHeadWordPosition(parsePhrase)
      val head = ta.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituentAtToken(headId)
      if (constituents.exists(_.getSpan == head.getSpan) && isArgCandidate(head))
        return headId

      val candidates = constituents.filter(isArgCandidate)
      if (candidates.nonEmpty) {
        val lastId = candidates.last.getStartSpan
        return lastId
      } else {
        return constituents.last.getStartSpan
      }

    } else {
      logger.warn("cannot find phrase for '" + ta.getToken(startTokenId) + "'")
    }
    startTokenId
  }

}
