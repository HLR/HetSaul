package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawSentence, ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationSensors

object entityRelationBasicDataModel extends DataModel {

  /** Nodes */
  val tokens = node[ConllRawToken]

  val sentences = node[ConllRawSentence]

  val pairs = node[ConllRelation]

  val RelationToPer = edge(tokens, pairs)

  val RelationToOrg = edge(tokens, pairs)

  val tokenContainsInSentence = edge(tokens, pairs)

  /** Properties */
  val pos = property(tokens, "pos") {
    t: ConllRawToken => t.POS :: Nil
  }

  val word = property(tokens, "word") {
    t: ConllRawToken => t.getWords(false).toList
  }
  val phrase = property(tokens, "phrase") {
    t: ConllRawToken => t.phrase :: Nil
  }

  val tokenSurface = property(tokens, "tokenSurface") {
    t: ConllRawToken => t.getWords(false).toList.mkString(" ")
  }

  val containsSubPhraseMent = property(tokens, "containsSubPhraseMent") {
    t: ConllRawToken => t.getWords(false).exists(_.contains("ment")).toString
  }

  val containsSubPhraseIng = property(tokens, "containsSubPhraseIng") {
    t: ConllRawToken => t.getWords(false).exists(_.contains("ing")).toString
  }

  import entityRelationSensors._

  val containsInCityList = property(tokens, "containsInCityList") {
    t: ConllRawToken => cityGazetSensor.isContainedIn(t).toString
  }

  val containsInPersonList = property(tokens, "containsInCityList") {
    t: ConllRawToken => personGazetSensor.containsAny(t).toString
  }

  val wordLen = property(tokens, "wordLen") {
    t: ConllRawToken => t.getLength
  }

  val relFeature = property(pairs, "reltokenSurface") {
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

  val relPos = property(pairs, "reltokenSurface") {
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

  /** Labeler Properties  */
  val entityType = property(tokens, "entityType") {
    t: ConllRawToken => t.entType
  }

  val relationType = property(pairs, "relationType") {
    r: ConllRelation => r.relType
  }
}
