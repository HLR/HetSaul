package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation
import scala.collection.JavaConversions._
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation, ConllRawSentence }
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.GazeteerReader

object EntityRelationSensors {
  def cityGazetSensor: GazeteerReader = {
    new GazeteerReader("./data/EntityMentionRelation/known_city.lst", "Gaz:City", true)
  }

  def personGazetSensor: GazeteerReader = {
    val persongazet = new GazeteerReader("./data/EntityMentionRelation/known_maleFirst.lst", "Gaz:Person", true)
    persongazet.addFile("./data/EntityMentionRelation/known_femaleFirst.lst", true)
    persongazet
  }
  def sentenceToRelation_GS(s: ConllRawSentence): List[ConllRelation] = {
    s.relations.toList
  }
  def sentenceToRelations_MS(s: ConllRawSentence, t: ConllRelation): Boolean = {
    s.sentId == t.sentId
  }
  def sentenceToTokens_GS(s: ConllRawSentence): List[ConllRawToken] = {
    s.sentTokens.toList
  }
  def relationTofirstArg_MS(r: ConllRelation, t: ConllRawToken): Boolean = {
    r.sentId.equals(t.sentId) && r.e1.wordId == t.wordId
  }
  def relationTosecondArg_MS(r: ConllRelation, t: ConllRawToken): Boolean = {
    r.sentId.equals(t.sentId) && r.e2.wordId == t.wordId
  }
}
