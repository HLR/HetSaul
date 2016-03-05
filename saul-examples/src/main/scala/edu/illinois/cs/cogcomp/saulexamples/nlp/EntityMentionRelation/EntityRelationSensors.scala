package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation, ConllRawSentence }
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.{ Conll04_Reader, GazeteerReader }

import scala.collection.JavaConverters._

object EntityRelationSensors {
  val path = "./data/"
  val resourcePath = "../saul-examples/src/main/resources/EntityMentionRelation/"

  def readConllData(dir: String): (List[ConllRawSentence], List[ConllRelation]) = {
    val reader = new Conll04_Reader(dir, "Token")
    (reader.sentences.asScala.toList, reader.relations.asScala.toList)
  }

  lazy val (sentencesAll, relationsAll) = readConllData(path + "EntityMentionRelation/conll04.corp")
  lazy val (sentencesTrain, relationsTrain) = readConllData(path + "EntityMentionRelation/conll04_train.corp")
  lazy val (sentencesTest, relationsTest) = readConllData(path + "EntityMentionRelation/conll04_test.corp")
  lazy val (sentencesSmallSet, testRelationsSmallSet) = readConllData(resourcePath + "conll04-smallDocument.txt")

  def cityGazetSensor: GazeteerReader = {
    new GazeteerReader(path + "EntityMentionRelation/known_city.lst", "Gaz:City", true)
  }

  def personGazetSensor: GazeteerReader = {
    val personGazet = new GazeteerReader(path + "EntityMentionRelation/known_maleFirst.lst", "Gaz:Person", true)
    personGazet.addFile(path + "EntityMentionRelation/known_femaleFirst.lst", true)
    personGazet
  }

  def sentenceToRelation_GeneratingSensor(s: ConllRawSentence): List[ConllRelation] = {
    s.relations.asScala.toList
  }

  def sentenceToRelations_MatchingSensor(s: ConllRawSentence, t: ConllRelation): Boolean = {
    s.sentId == t.sentId
  }

  def sentenceToTokens_GeneratingSensor(s: ConllRawSentence): List[ConllRawToken] = {
    s.sentTokens.asScala.toList
  }

  def relationToFirstArg_MatchingSensor(r: ConllRelation, t: ConllRawToken): Boolean = {
    r.sentId.equals(t.sentId) && r.e1.wordId == t.wordId
  }

  def relationToSecondArg_MatchingSensor(r: ConllRelation, t: ConllRawToken): Boolean = {
    r.sentId.equals(t.sentId) && r.e2.wordId == t.wordId
  }
}
