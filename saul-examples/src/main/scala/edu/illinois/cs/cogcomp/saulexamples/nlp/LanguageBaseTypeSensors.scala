/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp

import java.util.Properties

import edu.illinois.cs.cogcomp.core.datastructures.{ ViewNames, _ }
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Relation => _, Sentence => _, _ }
import edu.illinois.cs.cogcomp.edison.features.FeatureUtilities
import edu.illinois.cs.cogcomp.edison.features.factory.{ SubcategorizationFrame, WordFeatureExtractorFactory }
import edu.illinois.cs.cogcomp.edison.features.helpers.PathFeatureHelper
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator._
import edu.illinois.cs.cogcomp.nlp.utilities.CollinsHeadFinder
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLSensors.dependencyView

import scala.collection.JavaConverters._
import scala.collection.mutable

/** Created by parisakordjamshidi on 12/25/16.
  */
object LanguageBaseTypeSensors extends Logging {
  private val dependencyView = ViewNames.DEPENDENCY_STANFORD
  private val parserView = ViewNames.PARSE_STANFORD
  private val sentenceById = mutable.HashMap[String, TextAnnotation]()
  private val settings = new Properties()
  TextAnnotationFactory.disableSettings(settings, USE_SRL_NOM, USE_NER_CONLL, USE_NER_ONTONOTES, USE_SRL_VERB)
  private val as = TextAnnotationFactory.createPipelineAnnotatorService(settings)

  def documentToSentenceMatching(d: Document, s: Sentence): Boolean = {
    d.getId == s.getDocument.getId
  }

  def documentToSentenceGenerating(d: Document): Seq[Sentence] = {
    getSentences(d)
  }

  def sentenceToPhraseGenerating(s: Sentence): Seq[Phrase] = {
    getPhrases(s)
  }

  def sentenceToPhraseMatching(s: Sentence, p: Phrase): Boolean = {
    s.getId == p.getSentence.getId
  }

  def phraseToTokenGenerating(p: Phrase): Seq[Token] = {
    getTokens(p)
  }

  def phraseToTokenMatching(p: Phrase, t: Token): Boolean = {
    if (t.getPhrase != null)
      p.getId == t.getPhrase.getId
    else
      p.contains(t)
  }

  def sentenceToTokenGenerating(s: Sentence): Seq[Token] = {
    getTokens(s)
  }

  def sentenceToTokenMatching(s: Sentence, t: Token): Boolean = {
    s.getId == t.getSentence.getId
  }

  def documentToRelationMatching(d: Document, r: Relation): Boolean = {
    r.getParent != null && d.getId == getDocument(r.getParent).getId
  }

  def sentenceToRelationMatching(s: Sentence, r: Relation): Boolean = {
    r.getParent != null && s.getId == getSentence(r.getParent).getId
  }

  def phraseToRelationMatching(p: Phrase, r: Relation): Boolean = {
    r.getParent != null && p.getId == getPhrase(r.getParent).getId
  }

  def relationToTokenMatching(r: Relation, t: Token): Boolean = {
    r.getArgumentIds.contains(t.getId)
  }

  def getPos(e: NlpBaseElement): Seq[String] = {
    val constituents = getElementConstituents(e)
    constituents.map(x => WordFeatureExtractorFactory.pos.getFeatures(x).asScala.mkString)
  }

  def getPhrasePos(p: Phrase): String = {
    val ta = getTextAnnotation(p)
    val v = ta.getView(ViewNames.SHALLOW_PARSE)
    v.getLabelsCoveringSpan(getStartTokenId(p), getEndTokenId(p) + 1).asScala.head
  }

  def getLemma(e: NlpBaseElement): Seq[String] = {
    val constituents = getElementConstituents(e)
    constituents.map(x => WordFeatureExtractorFactory.lemma.getFeatures(x).asScala.mkString)
  }

  def getHeadword(p: Phrase): Token = {
    var ta = getTextAnnotation(p)
    val (startId: Int, endId: Int) = getTextAnnotationSpan(p)
    var phrase = new Constituent("temp", "", ta, startId, endId + 1)
    var headId: Int = getHeadwordId(ta, phrase)
    if (headId < startId || headId > endId) {
      //when out of phrase, create a text annotation using just the phrase text
      ta = TextAnnotationFactory.createTextAnnotation(as, "", "", p.getText)
      phrase = new Constituent("temp", "", ta, 0, ta.getTokens.length)
      headId = getHeadwordId(ta, phrase)
    }
    val head = ta.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituentAtToken(headId)
    new Token(p, p.getId + head.getSpan, head.getStartCharOffset, head.getEndCharOffset, head.toString)
  }

  def getTokens(text: String): List[Token] = {
    val ta = TextAnnotationFactory.createTextAnnotation(as, "", "", text)
    ta.getView(ViewNames.TOKENS).getConstituents.asScala.map(x =>
      new Token(null.asInstanceOf[Sentence], null, x.getStartCharOffset, x.getEndCharOffset, x.toString)).toList
  }

  def getHeadword(text: String): (String, Int, Int) = {
    val ta = TextAnnotationFactory.createTextAnnotation(as, "", "", text)
    val phrase = new Constituent("temp", "", ta, 0, ta.getTokens.length)
    val headId = getHeadwordId(ta, phrase)
    val head = ta.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituentAtToken(headId)
    (head.toString, head.getStartCharOffset, head.getEndCharOffset)
  }

  def getSemanticRole(e: NlpBaseElement): String = {
    val ta = getTextAnnotation(e)
    val view = if (ta.hasView(ViewNames.SRL_VERB)) {
      ta.getView(ViewNames.SRL_VERB)
    } else {
      logger.warn("Cannot find SRL view")
      null
    }
    val (startId: Int, endId: Int) = getTextAnnotationSpan(e)
    view match {
      case null => ""
      case _ => view.getLabelsCoveringSpan(startId, endId + 1).asScala.mkString(",")
    }
  }

  def isBefore(t1: NlpBaseElement, t2: NlpBaseElement): Boolean = {
    getStartTokenId(t1) < getStartTokenId(t2)
  }

  def getTokenDistance(t1: NlpBaseElement, t2: NlpBaseElement): Int = {
    Math.abs(getStartTokenId(t1) - getStartTokenId(t2))
  }

  def getCandidateRelations[T <: NlpBaseElement](argumentInstances: List[T]*): List[Relation] = {
    if (argumentInstances.length < 2) {
      List.empty
    } else {
      crossProduct(argumentInstances.seq.toList)
        // don't consider elements that are from different parents(sentences)
        .filter(args => args.filter(_ != null).groupBy {
          case x: Token => x.getSentence.getId
          case x: Phrase => x.getSentence.getId
          case x: Sentence => x.getDocument.getId
          case _ => null
        }.size <= 1 && args.filter(_ != null)
          .groupBy(_.getId).size == args.count(_ != null) // distinct arguments
          )
        .map(args => {
          val r = new Relation()
          args.zipWithIndex.filter(x => x._1 != null).foreach {
            case (a, i) => {
              r.setArgumentId(i, a.getId)
              r.setArgument(i, a)
              r.setId(r.getId + "[" + i + ", " + a.getId + "]")
            }
          }
          r
        })
    }
  }

  def getSubCategorization(e: NlpBaseElement): String = {
    val (startId: Int, endId: Int) = getTextAnnotationSpan(e)
    val ta = getTextAnnotation(e)
    val v = ta.getView(ViewNames.TOKENS)
    val constituents = v.getConstituentsCoveringSpan(startId, endId + 1).asScala
    constituents
      .map(x => FeatureUtilities.getFeatureSet(new SubcategorizationFrame(ViewNames.PARSE_STANFORD), x)
        .asScala.mkString(",")).mkString(";")
  }

  def getWindow(t: Token, before: Int, after: Int): Seq[String] = {
    val id = getStartTokenId(t)
    val ta = getTextAnnotation(t)
    val start = Math.max(0, id - before)
    val end = Math.min(ta.getTokens.length - 1, id + after)
    ta.getTokens.slice(start, end)
  }

  def getDependencyRelation(t: Token): String = {
    val relations = getDependencyRelations(getTextAnnotation(t))
    val root = getDependencyRoot(relations)
    if (root != null && root.getStartCharOffset == t.getStart)
      "root"
    else
      relations.find(r => r.getTarget.getStartCharOffset == t.getStart) match {
        case Some(r) => r.getRelationName
        case _ => ""
      }
  }

  def getDependencyPath(t1: Token, t2: Token): String = {

    def getRelationName(relations: List[textannotation.Relation], c1: Constituent, c2: Constituent, dir: String): String = {
      val r = relations.find(x => (x.getSource == c1 && x.getTarget == c2) || (x.getSource == c2 && x.getTarget == c1))
      r match {
        case Some(r) => dir + r.getRelationName
        case None => ""
      }
    }

    val ta = getTextAnnotation(t1)
    val c1 = ta.getView(dependencyView).getConstituentsCoveringToken(getStartTokenId(t1)).get(0)
    val c2 = ta.getView(dependencyView).getConstituentsCoveringToken(getStartTokenId(t2)).get(0)

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

  ////////////////////////////////////////////////////////////////////////////
  /// private methods
  ////////////////////////////////////////////////////////////////////////////
  private def crossProduct[T](input: List[List[T]]): List[List[T]] = input match {
    case Nil => Nil
    case head :: Nil => head.map(_ :: Nil)
    case head :: tail => for (elem <- head; sub <- crossProduct(tail)) yield elem :: sub
  }

  private def getHeadwordId(ta: TextAnnotation, phrase: Constituent) = {
    val tree: TreeView = ta.getView(ViewNames.PARSE_STANFORD).asInstanceOf[TreeView]
    val parsePhrase = tree.getParsePhrase(phrase)
    val headId = CollinsHeadFinder.getInstance.getHeadWordPosition(parsePhrase)
    headId
  }

  private def getDependencyRoot(relations: Seq[textannotation.Relation]): Constituent = {
    relations.find(x => relations.count(r => r.getTarget == x.getSource) == 0) match {
      case Some(x) => x.getSource
      case _ => null
    }
  }

  private def getDependencyRelations(ta: TextAnnotation): Seq[textannotation.Relation] = {
    ta.getView(dependencyView).asInstanceOf[TreeView].getRelations.asScala
  }

  private def getPhrase(e: NlpBaseElement) = e match {
    case p: Phrase => p
    case t: Token => t.getPhrase
    case _ =>
      logger.warn("cannot use 'getPhrase' for document or Sentence type.")
      null
  }

  private def getSentence(e: NlpBaseElement) = e match {
    case s: Sentence => s
    case p: Phrase => p.getSentence
    case t: Token => t.getSentence
    case _ =>
      logger.warn("cannot use 'getSentence' for document type.")
      null
  }

  private def getDocument(e: NlpBaseElement) = e match {
    case x: Document => x
    case _ => getSentence(e).getDocument
  }

  private def getSentences(document: Document): Seq[Sentence] = {
    val ta = as.createBasicTextAnnotation("", document.getId, document.getText)
    ta.sentences().asScala.map(x =>
      new Sentence(document, document.getId + "_" + x.getSentenceId, x.getSentenceConstituent.getStartCharOffset,
        x.getSentenceConstituent.getEndCharOffset, x.getText))
  }

  private def getPhrases(sentence: Sentence): Seq[Phrase] = {
    val ta = getTextAnnotation(sentence)
    val v = ta.getView(ViewNames.SHALLOW_PARSE)
    v.getConstituents.asScala.map(x =>
      new Phrase(sentence, generateId(sentence, x), x.getStartCharOffset, x.getEndCharOffset, x.toString))
  }

  def getTokens(e: NlpBaseElement): Seq[Token] = {
    val ta = getTextAnnotation(e)
    if (ta == null)
      return Seq()
    val v = ta.getView(ViewNames.TOKENS)
    val (startId: Int, endId: Int) = getTextAnnotationSpan(e)
    v.getConstituentsCoveringSpan(startId, endId + 1).asScala.map(x =>
      e match {
        case p: Phrase => new Token(p, generateId(e, x), x.getStartCharOffset, x.getEndCharOffset, x.toString)
        case s: Sentence => new Token(s, generateId(e, x), x.getStartCharOffset, x.getEndCharOffset, x.toString)
        case _ =>
          logger.warn("cannot find tokens for base types other than phrase and sentence.")
          null
      })
  }

  private def generateId(e: NlpBaseElement, x: Constituent): String = {
    e.getId + x.getSpan
  }

  private def getElementConstituents(e: NlpBaseElement): Seq[Constituent] = {
    val s = getSentence(e)
    if (s == null)
      return Seq()
    val ta = getTextAnnotation(s)
    val v = ta.getView(ViewNames.TOKENS)
    val startId = ta.getTokenIdFromCharacterOffset(e.getStart)
    val endId = ta.getTokenIdFromCharacterOffset(e.getEnd - 1)
    v.getConstituentsCoveringSpan(startId, endId + 1).asScala
  }

  private def getTextAnnotation(e: NlpBaseElement): TextAnnotation = {
    val sentence = getSentence(e)
    if (sentence == null)
      return null
    if (!sentenceById.contains(sentence.getId)) {
      val ta = TextAnnotationFactory.createTextAnnotation(as, sentence.getDocument.getId, sentence.getId, sentence.getText)
      sentenceById.put(sentence.getId, ta)
    }
    sentenceById(sentence.getId)
  }

  private def getTextAnnotationSpan(e: NlpBaseElement): (Int, Int) = {
    (getStartTokenId(e), getEndTokenId(e))
  }

  private def getStartTokenId(e: NlpBaseElement): Int = {
    val ta = getTextAnnotation(e)
    val start = e match {
      case _: Document | _: Sentence => 0
      case _ => e.getStart
    }
    ta.getTokenIdFromCharacterOffset(start)
  }

  private def getEndTokenId(e: NlpBaseElement): Int = {
    val ta = getTextAnnotation(e)
    val end = e match {
      case _: Document | _: Sentence => ta.getView(ViewNames.TOKENS).getConstituents.asScala.last.getEndCharOffset
      case _ => e.getEnd
    }
    ta.getTokenIdFromCharacterOffset(end - 1)
  }
}