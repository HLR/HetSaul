/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.io.{ File, PrintWriter }
import java.lang.Boolean

import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.core.datastructures.{ IntPair, ViewNames }
import edu.illinois.cs.cogcomp.nlp.utilities.CollinsHeadFinder
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.{ RELATION, SpRL2013Document }

import scala.collection.JavaConverters._
import scala.collection.immutable.HashSet
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

/** Created by taher on 7/28/16.
  */
object PopulateSpRLDataModel extends Logging {
  def apply(path: String, isTraining: Boolean, dataVersion: String, modelName: String, savedLexicon: HashSet[String]) = {

    modelName match {
      case "Roberts" =>
        val getLex: (List[SpRL2013Document]) => HashSet[String] = if (isTraining) getLexicon else (x) => savedLexicon

        val (sentences, relations, lex) =
          SpRLDataModelReader.read(path, isTraining, dataVersion, getRobertsRelations, getLex)

        if (!isTraining) {
          val reader = new SpRLDataReader(path, classOf[SpRL2013Document])
          reader.readData()
          val doc = reader.documents.get(0)
          val originalRelations = doc.getTAGS.getRELATION.asScala.toList
          val missed = originalRelations.filter(x => !relations.exists(r => r.getRelationId == x.getId))
          val writer = new PrintWriter(new File("missed-relations.txt"))
          for (r <- missed) {
            val sp = doc.getSpatialIndicatorMap.get(r.getSpatialIndicatorId)
            val tr = doc.getTrajectorHashMap.get(r.getTrajectorId)
            val lm = doc.getLandmarkHashMap.get(r.getLandmarkId)
            writer.println("relationId: " + r.getId)
            if (tr != null)
              writer.println("tr: " + tr.getText + "(" + tr.getId + ")")
            if (sp != null)
              writer.println("sp: " + sp.getText + "(" + sp.getId + ") , contains in lex: " + lex.contains(sp.getText.toLowerCase()))
            if (lm != null)
              writer.println("lm: " + lm.getText + "(" + lm.getId + ")")
            writer.println()
            writer.println()
          }
          writer.close()
        }

        RobertsDataModel.spLexicon = lex
        RobertsDataModel.sentences.populate(sentences, train = isTraining)
        RobertsDataModel.relations.populate(relations, train = isTraining)
    }
  }

  def getLexicon(docs: List[SpRL2013Document]): HashSet[String] = {
    val dic: Seq[String] = Dictionaries.prepositions.toSeq
    val indicators: Seq[String] = docs.flatMap(d => d.getTAGS.getSPATIALINDICATOR.
      asScala.map(s => s.getText.toLowerCase.trim))

    HashSet[String](dic ++ indicators: _*)
  }

  def getRobertsRelations(sentence: Sentence, doc: SpRL2013Document, lexicon: HashSet[String], offset: IntPair): List[RobertsRelation] = {

    def getIndicatorCandidates(sentence: Sentence, constituents: List[Constituent], lexicon: HashSet[String]): ListBuffer[Constituent] = {

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

    def isGoldTrajector(r: RELATION, tr: Constituent): Boolean = {
      tr == getHeadword(doc.getTrajectorHashMap.get(r.getTrajectorId), tr.getTextAnnotation, offset)
    }

    def isGoldLandmark(r: RELATION, lm: Constituent): Boolean = {
      lm == getHeadword(doc.getLandmarkHashMap.get(r.getLandmarkId), lm.getTextAnnotation, offset)
    }

    def getGoldRelations(goldRelations: List[RELATION], sp: Constituent): List[RELATION] = {
      goldRelations.filter(x =>
        doc.getSpatialIndicatorMap.get(x.getSpatialIndicatorId).getStart.intValue() == sp.getStartCharOffset + offset.getFirst &&
          doc.getSpatialIndicatorMap.get(x.getSpatialIndicatorId).getText.trim.equalsIgnoreCase(sp.toString))
    }

    val relations = ListBuffer[RobertsRelation]()
    val constituents = sentence.getView(ViewNames.TOKENS).asScala.toList

    val args = constituents.filter(x => isArgCandidate(x))
    val indicators: ListBuffer[Constituent] = getIndicatorCandidates(sentence, constituents, lexicon)

    val goldRelations = doc.getTAGS.getRELATION.asScala
      .filter(x => !tagIsNullOrOutOfSentence(doc.getSpatialIndicatorMap.get(x.getSpatialIndicatorId), offset)).toList

    for (i <- indicators) {

      val rels = getGoldRelations(goldRelations, i)

      for (tr <- args) {

        var label = RobertsRelation.RobertsRelationLabels.CANDIDATE

        val g = rels.find(r => isGoldTrajector(r, tr) && tagIsNullOrOutOfSentence(doc.getLandmarkHashMap.get(r.getLandmarkId), offset))
        if (g.isDefined) {
          label = RobertsRelation.RobertsRelationLabels.GOLD
          relations += new RobertsRelation(sentence, tr.getSpan, i.getSpan, null, label, g.get.getId)
        } else {
          relations += new RobertsRelation(sentence, tr.getSpan, i.getSpan, null, label, "")
        }

        for (lm <- args) {
          if (lm.getSpan != tr.getSpan) {

            val g = rels.find(r => isGoldTrajector(r, tr) && isGoldLandmark(r, lm))
            if (g.isDefined) {
              label = RobertsRelation.RobertsRelationLabels.GOLD
              relations += new RobertsRelation(sentence, tr.getSpan, i.getSpan, lm.getSpan, label, g.get.getId)
            } else {
              label = RobertsRelation.RobertsRelationLabels.CANDIDATE
              relations += new RobertsRelation(sentence, tr.getSpan, i.getSpan, lm.getSpan, label, "")
            }
          }
        }

      }
    }

    relations.toList
  }

  def tagIsNullOrOutOfSentence(t: HasSpan, offset: IntPair): Boolean = {
    t == null || t.getStart.intValue() < 0 || t.getEnd.intValue() < 0 ||
      !(offset.getFirst <= t.getStart.intValue() && t.getEnd.intValue() <= offset.getSecond)
  }

  def isArgCandidate(x: Constituent): Boolean = {
    CommonSensors.getPosTag(x).startsWith("NN") ||
      CommonSensors.getPosTag(x).startsWith("CD") ||
      CommonSensors.getPosTag(x).startsWith("PRP")
  }

  def getHeadword(t: HasSpan, ta: TextAnnotation, offset: IntPair): Constituent = {
    ta.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituentAtToken(getHeadwordId(t, ta, offset))
  }

  def getHeadwordId(t: HasSpan, ta: TextAnnotation, offset: IntPair): Int = {

    if (tagIsNullOrOutOfSentence(t, offset))
      return -1

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
      val tree: TreeView = ta.getView(RobertsDataModel.parseView).asInstanceOf[TreeView]
      val parsePhrase = tree.getParsePhrase(phrase)
      val headId = CollinsHeadFinder.getInstance.getHeadWordPosition(parsePhrase)
      val head = ta.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituentAtToken(headId)
      if (constituents.exists(x => x.getSpan == head.getSpan) && isArgCandidate(head))
        return headId

      val candidates = constituents.filter(c => isArgCandidate(c))
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
