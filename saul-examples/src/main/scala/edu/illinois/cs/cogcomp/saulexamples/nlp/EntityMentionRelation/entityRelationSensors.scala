package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation, ConllRawSentence }
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.GazeteerReader

import scala.collection.JavaConversions._

object EntityRelationSensors {
  def cityGazetSensor: GazeteerReader = {
    new GazeteerReader("./data/EntityMentionRelation/known_city.lst", "Gaz:City", true)
  }

  def personGazetSensor: GazeteerReader = {
    val persongazet = new GazeteerReader("./data/EntityMentionRelation/known_maleFirst.lst", "Gaz:Person", true)
    persongazet.addFile("./data/EntityMentionRelation/known_femaleFirst.lst", true)
    persongazet
  }
  def sentenceToRelation_GeneratingS(s: ConllRawSentence): List[ConllRelation] = {
    s.relations.toList
  }
  def sentenceToRelations_MatchingS(s: ConllRawSentence, t: ConllRelation): Boolean = {
    s.sentId == t.sentId
  }
  def sentenceToTokens_GeneratingS(s: ConllRawSentence): List[ConllRawToken] = {
    s.sentTokens.toList
  }
  def relationToFirstArg_MatchingS(r: ConllRelation, t: ConllRawToken): Boolean = {
    r.sentId.equals(t.sentId) && r.e1.wordId == t.wordId
  }
  def relationToSecondArg_MatchingS(r: ConllRelation, t: ConllRawToken): Boolean = {
    r.sentId.equals(t.sentId) && r.e2.wordId == t.wordId
  }
}
