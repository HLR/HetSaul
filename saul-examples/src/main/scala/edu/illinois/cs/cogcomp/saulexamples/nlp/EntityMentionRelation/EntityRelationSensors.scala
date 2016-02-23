package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation, ConllRawSentence }
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.{ Conll04_ReaderNew, GazeteerReader }

import scala.collection.JavaConversions._

object EntityRelationSensors {
  val path = "../data/"

  lazy val (sentences, relations) = {
    val reader = new Conll04_ReaderNew(path + "EntityMentionRelation/conll04.corp", "Token")
    (reader.sentences, reader.relations)
  }

  def cityGazetSensor: GazeteerReader = {
    new GazeteerReader(path + "EntityMentionRelation/known_city.lst", "Gaz:City", true)
  }

  def personGazetSensor: GazeteerReader = {
    val personGazet = new GazeteerReader(path + "EntityMentionRelation/known_maleFirst.lst", "Gaz:Person", true)
    personGazet.addFile(path + "EntityMentionRelation/known_femaleFirst.lst", true)
    personGazet
  }

  def sentenceToRelation_GeneratingSensor(s: ConllRawSentence): List[ConllRelation] = {
    s.relations.toList
  }

  def sentenceToRelations_MatchingSensor(s: ConllRawSentence, t: ConllRelation): Boolean = {
    s.sentId == t.sentId
  }

  def sentenceToTokens_GeneratingSensor(s: ConllRawSentence): List[ConllRawToken] = {
    s.sentTokens.toList
  }

  def relationToFirstArg_MatchingSensor(r: ConllRelation, t: ConllRawToken): Boolean = {
    r.sentId.equals(t.sentId) && r.e1.wordId == t.wordId
  }

  def relationToSecondArg_MatchingSensor(r: ConllRelation, t: ConllRawToken): Boolean = {
    r.sentId.equals(t.sentId) && r.e2.wordId == t.wordId
  }
}
