package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawSentence, ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.Conll04_ReaderNew
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers.{ locationClassifier, orgClassifier, personClassifier }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationSensors._

import scala.collection.JavaConversions._

object EntityRelationDataModel extends DataModel {

  /** Nodes & Edges */
  val tokens = node[ConllRawToken]((x: ConllRawToken) => x.wordId + ":" + x.sentId)
  val sentences = node[ConllRawSentence]((x: ConllRawSentence) => x.sentId)
  val pairs = node[ConllRelation]((x: ConllRelation) => x.wordId1 + ":" + x.wordId2 + ":" + x.sentId)

  val sentenceToToken = edge(sentences, tokens)
  val sentencesToPairs = edge(sentences, pairs)
  val pairTo1stArg = edge(pairs, tokens)
  val pairTo2ndArg = edge(pairs, tokens)
  val tokenToPair = edge(tokens, pairs)

  sentenceToToken.addSensor(sentenceToTokens_GeneratingS _)
  sentencesToPairs.addSensor(sentenceToRelation_GeneratingS _)
  pairTo1stArg.addSensor(relationToSecondArg_MatchingS _)
  pairTo2ndArg.addSensor(relationToSecondArg_MatchingS _)

  /** Properties */
  val pos = property(tokens) {
    t: ConllRawToken => t.POS
  }

  val word = property(tokens) {
    t: ConllRawToken => t.getWords(false).toList
  }

  val phrase = property(tokens) {
    t: ConllRawToken => t.phrase
  }

  val tokenSurface = property(tokens) {
    t: ConllRawToken => t.getWords(false).mkString(" ")
  }

  val containsSubPhraseMent = property(tokens) {
    t: ConllRawToken => t.getWords(false).exists(_.contains("ment")).toString
  }

  val containsSubPhraseIng = property(tokens) {
    t: ConllRawToken => t.getWords(false).exists(_.contains("ing")).toString
  }

  val containsInCityList = property(tokens) {
    t: ConllRawToken => cityGazetSensor.isContainedIn(t)
  }

  val containsInPersonList = property(tokens) {
    t: ConllRawToken => personGazetSensor.containsAny(t)
  }

  val wordLen = property(tokens) {
    t: ConllRawToken => t.getLength
  }

  val relFeature = property(pairs) {
    token: ConllRelation =>
      {
        "w1-word-" + token.e1.phrase :: "w2-word-" + token.e2.phrase ::
          "w1-pos-" + token.e1.POS :: "w2-pos-" + token.e2.POS ::
          "w1-city-" + cityGazetSensor.isContainedIn(token.e1) :: "w2-city-" + cityGazetSensor.isContainedIn(token.e2) ::
          "w1-per-" + personGazetSensor.containsAny(token.e1) :: "w2-per-" + personGazetSensor.containsAny(token.e2) ::
          "w1-ment-" + token.e1.getWords(false).exists(_.contains("ing")) :: "w2-ment-" + token.e2.getWords(false).exists(_.contains("ing")) ::
          "w1-ing-" + token.e1.getWords(false).exists(_.contains("ing")) :: "w2-ing-" + token.e2.getWords(false).exists(_.contains("ing")) ::
          Nil
      }
  }

  val relPos = property(pairs) {
    rela: ConllRelation =>
      val e1 = rela.e1
      val e2 = rela.e2

      this.tokens.getWithWindow(e1, -2, 2, _.sentId).zipWithIndex.map {
        case (Some(t), idx) => s"left-$idx-pos-${t.POS} "
        case (None, idx) => s"left-$idx-pos-EMPTY "
      } ++
        this.tokens.getWithWindow(e2, -2, 2, _.sentId).zipWithIndex.map {
          case (Some(t), idx) => s"right-$idx-pos-${t.POS} "
          case (None, idx) => s"right-$idx-pos-EMPTY} "
        }
  }

  val ePipe = property[ConllRelation](pairs) {
    rel: ConllRelation =>
      "e1-org: " + orgClassifier(rel.e1) ::
        "e1-per: " + personClassifier(rel.e1) ::
        "e1-loc: " + locationClassifier(rel.e1) ::
        "e2-org: " + orgClassifier(rel.e1) ::
        "e2-per: " + personClassifier(rel.e1) ::
        "e2-loc: " + locationClassifier(rel.e1) ::
        Nil
  }

  /** Labeler Properties  */
  val entityType = property(tokens) {
    t: ConllRawToken => t.entType
  }

  val relationType = property(pairs) {
    r: ConllRelation => r.relType
  }

  def populateWithConll() = {
    val reader = new Conll04_ReaderNew("./data/EntityMentionRelation/conll04.corp", "Token")
    sentences.populate(reader.sentences)
    pairs.populate(reader.relations)
  }
}
