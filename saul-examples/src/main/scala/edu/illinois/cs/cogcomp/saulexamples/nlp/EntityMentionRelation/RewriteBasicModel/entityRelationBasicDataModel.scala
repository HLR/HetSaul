package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawSentence, ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.GazeteerReader

import scala.collection.mutable.{ Map => MutableMap }

object entityRelationBasicDataModel extends DataModel {

  val citygazet: GazeteerReader = new GazeteerReader("./data/EntityMentionRelation/known_city.lst", "Gaz:City", true)
  val persongazet: GazeteerReader = new GazeteerReader("./data/EntityMentionRelation/known_maleFirst.lst", "Gaz:Person", true)
  persongazet.addFile("./data/EntityMentionRelation/known_femaleFirst.lst", true)

  /** Nodes */
  val tokens = node[ConllRawToken]

  val sentences = node[ConllRawSentence]

  val pairs = node[ConllRelation]

  val RelationToPer = edge(tokens, pairs)

  val RelationToOrg = edge(tokens, pairs)

  val tokenContainsInSentence = edge(tokens, pairs)

  /** Properties */

  val pos = property[ConllRawToken]("pos") {
    t: ConllRawToken => t.POS :: Nil
  }

  val word = property[ConllRawToken]("word") {
    t: ConllRawToken => t.getWords(false).toList
  }
  val phrase = property[ConllRawToken]("phrase") {
    t: ConllRawToken => t.phrase :: Nil
  }

  val tokenSurface = property[ConllRawToken]("tokenSurface") {
    t: ConllRawToken => t.getWords(false).toList.mkString(" ")
  }

  val containsSubPhraseMent = property[ConllRawToken]("containsSubPhraseMent") {
    t: ConllRawToken => t.getWords(false).exists(_.contains("ment")).toString
  }

  val containsSubPhraseIng = property[ConllRawToken]("containsSubPhraseIng") {
    t: ConllRawToken => t.getWords(false).exists(_.contains("ing")).toString
  }

  val containsInCityList = property[ConllRawToken]("containsInCityList") {
    t: ConllRawToken => citygazet.isContainedIn(t).toString
  }

  val containsInPersonList = property[ConllRawToken]("containsInCityList") {
    t: ConllRawToken => persongazet.containsAny(t).toString
  }

  val wordLen = property[ConllRawToken]("wordLen") {
    t: ConllRawToken => t.getLength
  }

  val relFeature = property[ConllRelation]("reltokenSurface") {
    token: ConllRelation =>
      {
        "w1-word-" + token.e1.phrase :: "w2-word-" + token.e2.phrase ::
          "w1-pos-" + token.e1.POS :: "w2-pos-" + token.e2.POS ::
          "w1-city-" + citygazet.isContainedIn(token.e1) :: "w2-city-" + citygazet.isContainedIn(token.e2) ::
          "w1-per-" + persongazet.containsAny(token.e1) :: "w2-per-" + persongazet.containsAny(token.e2) ::
          "w1-ment-" + token.e1.getWords(false).exists(_.contains("ing")) :: "w2-ment-" + token.e2.getWords(false).exists(_.contains("ing")) ::
          "w1-ing-" + token.e1.getWords(false).exists(_.contains("ing")) :: "w2-ing-" + token.e2.getWords(false).exists(_.contains("ing")) ::
          Nil
      }
  }

  val relPos = discreteAttributesGeneratorOf[ConllRelation]('reltokenSurface) {
    rela: ConllRelation =>
      {
        val e1 = rela.e1
        val e2 = rela.e2

        this.getNodeWithType[ConllRawToken].getWithWindow(e1, -2, 2, _.sentId).zipWithIndex.map({
          case (Some(t), idx) => s"left-$idx-pos-${t.POS} "
          case (None, idx) => s"left-$idx-pos-EMPTY "
        }) ++
          this.getNodeWithType[ConllRawToken].getWithWindow(e2, -2, 2, _.sentId).zipWithIndex.map({
            case (Some(t), idx) => s"right-$idx-pos-${t.POS} "
            case (None, idx) => s"right-$idx-pos-EMPTY} "
          })
      }
  }

  /** Labeler Properties  */

  val entityType = property[ConllRawToken]("entityType") {
    t: ConllRawToken => t.entType
  }

  val relationType = property[ConllRelation]("relationType") {
    r: ConllRelation => r.relType
  }
}
