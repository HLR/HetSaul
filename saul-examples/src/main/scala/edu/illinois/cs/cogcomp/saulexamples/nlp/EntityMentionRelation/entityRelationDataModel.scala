package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawSentence, ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.{ GazeteerReader, Conll04_ReaderNew }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.classifiers.PersonClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import scala.collection.mutable.{ Map => MutableMap }
import scala.collection.JavaConversions._
import scala.util.Random

object entityRelationDataModel extends DataModel {

  /** Entity Definitions
    */
  val citygazet: GazeteerReader = new GazeteerReader("./data/EntityMentionRelation/known_city.lst", "Gaz:City", true)
  val persongazet: GazeteerReader = new GazeteerReader("./data/EntityMentionRelation/known_maleFirst.lst", "Gaz:Person", true)
  persongazet.addFile("./data/EntityMentionRelation/known_femaleFirst.lst", true)
  val tokens = node[ConllRawToken]

  val sentences = node[ConllRawSentence]

  val pairedRelations = node[ConllRelation]
  //'e1id -> ((t: ConllRelation) => String.valueOf(t.sentId) + ":" + String.valueOf(t.wordId1)), // String.valueOf(t.sentId)+ ":" + String.valueOf(t.wordId1)),
  //'e2id -> ((t: ConllRelation) => String.valueOf(t.sentId) + ":" + String.valueOf(t.wordId2))

  // val RelationToToken = oneToManyRelationOf[ConllRelation,ConllRawToken]('contain)(('sid,'sid))

  /** Access method definitions
    */

  val RelationToPer = edge(tokens, pairedRelations, 'e1id)
  //('sid === 'sid, 'e1id === 'wordid) //TODO check the runtime problem with the new edge implementation
  val RelationToOrg = edge(tokens, pairedRelations, 'e2id)
  //('sid === 'sid, 'e2id === 'wordid)//TODO check the runtime problem with the new edge implementation
  val tokenContainsInSentence = edge(tokens, pairedRelations, 'sid) //('sid === 'sid)//TODO check the runtime problem with the new edge implementation

  /** Attributes
    */

  val pos = discreteAttributesGeneratorOf[ConllRawToken]('pos) {
    t: ConllRawToken =>
      {
        //      println(t.POS)
        t.POS :: Nil
      }
  }
  val word = discreteAttributesGeneratorOf[ConllRawToken]('word) {
    t: ConllRawToken => //t.getWords(false).zipWithIndex.map { case (w, i) => s"word${i}_$w"}.toList {
      t.getWords(false).toList
  }
  val phrase = discreteAttributesGeneratorOf[ConllRawToken]('phrase) {
    t: ConllRawToken => t.phrase :: Nil
  }

  val tokenSurface = discreteAttributeOf[ConllRawToken]('tokenSurface) {
    _.getWords(false).toList.mkString(" ")
  }
  val containsSubPhraseMent = discreteAttributeOf[ConllRawToken]('containsSubPhraseMent) {
    _.getWords(false).exists(_.contains("ment")).toString
  }

  val containsSubPhraseIng = discreteAttributeOf[ConllRawToken]('containsSubPhraseIng) {
    _.getWords(false).exists(_.contains("ing")).toString
  }

  val containsInCityList = discreteAttributeOf[ConllRawToken]('containsInCityList) {
    citygazet.isContainedIn(_).toString
  }

  val containsInPersonList = discreteAttributeOf[ConllRawToken]('containsInCityList) {
    persongazet.containsAny(_).toString
  }

  val wordLen = realAttributeOf[ConllRawToken]('wordLen) {
    t => t.getLength
  }

  val relFeature = discreteAttributesGeneratorOf[ConllRelation]('reltokenSurface) {
    token: ConllRelation =>
      {
        "w1-word-" + token.e1.phrase :: "w2-word-" + token.e2.phrase ::
          "w1-pos-" + token.e1.POS :: "w2-pos-" + token.e2.POS ::
          "w1-city-" + citygazet.isContainedIn(token.e1) :: "w2-city-" + citygazet.isContainedIn(token.e2) ::
          "w1-per-" + persongazet.containsAny(token.e1) :: "w2-per-" + persongazet.containsAny(token.e2) ::
          "w1-ment-" + token.e1.getWords(false).exists(_.contains("ing")) :: "w2-ment-" + token.e2.getWords(false).exists(_.contains("ing")) ::
          "w1-ing-" + token.e1.getWords(false).exists(_.contains("ing")) :: "w2-ing-" + token.e2.getWords(false).exists(_.contains("ing")) ::
          //        "w1Typ"+token.e1.entType :: "w2Typ"+token.e2.entType ::
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

  /** Labelers
    */

  val entityType = discreteAttributeOf[ConllRawToken]('entityType) {
    t: ConllRawToken => t.entType
  }

  val relationType = discreteAttributeOf[ConllRelation]('relationType) {
    r: ConllRelation =>
      //      println(r.relType)
      r.relType
  }

  def readAll() = {
    val reader = new Conll04_ReaderNew("./data/EntityMentionRelation/conll04.corp", "Token")
    val trainSentences = reader.sentences.toList
    val trainTokens = trainSentences.flatMap(_.sentTokens)
    val trainRelations = reader.relations.toList

    sentences populate trainSentences
    tokens populate trainTokens
    pairedRelations populate trainRelations
    this.testWith(trainTokens)
    this.testWith(trainRelations)
    this.testWith(trainSentences)
  }

  def read(fold: Int) = {

    println(s"Running fold $fold")
    //These numbers are the size of the folds and this piece of code is customized for the experiments.
    val lower: Int = 1103 * fold
    val upper: Int = 1103 * (fold + 1)

    println(s"testing with [$lower $upper]")
    val reader = new Conll04_ReaderNew("./data/EntityMentionRelation/conll04.corp", "Token")
    val trainSentences = reader.sentences.toList.filter(s => s.sentId < lower || s.sentId > upper)
    val trainTokens = trainSentences.flatMap(_.sentTokens)
    val trainRelations = reader.relations.toList.filter(s => s.sentId < lower || s.sentId > upper)

    val blankRelation = trainSentences.flatMap {
      sent =>
        {
          val zipped = Random.shuffle(sent.sentTokens.toList).zip(Random.shuffle(sent.sentTokens.toList))
          val relationInSent = sent.relations
          val filteredZip = zipped.filterNot({ case (e1, e2) => relationInSent.exists({ r => (r.e1 == e1) && (r.e2 == e2) }) })
          filteredZip.map({
            case (e1, e2) =>
              val ret = new ConllRelation
              ret.e1 = e1
              ret.e2 = e2
              ret.relType = "?"
              ret.wordId1 = e1.wordId
              ret.wordId2 = e2.wordId
              ret.sentId = sent.sentId
              ret
          })
        }
    }

    //    val trainSentences = reader.sentences.toList.filter(_.sentId < 4750)
    //    println(trainSentences.map(s => s.relations).flatten.size)
    //    println(trainRelations.size)

    val testSentences = reader.sentences.toList.filter(s => s.sentId >= lower && s.sentId <= upper)
    val testTokens = testSentences.flatMap(_.sentTokens)
    val testRelations = reader.relations.toList.filter(s => s.sentId >= lower && s.sentId <= upper)

    println(testSentences.size)

    val blankRelationTest: List[ConllRelation] = testSentences.flatMap {

      sent =>
        {
          val zipped = Random.shuffle(sent.sentTokens.toList).zip(Random.shuffle(sent.sentTokens.toList))
          val relationInSent = sent.relations
          val filteredZip = zipped.filterNot({ case (e1, e2) => relationInSent.exists({ r => (r.e1 == e1) && (r.e2 == e2) }) })
          filteredZip.map({
            case (e1, e2) =>
              val ret = new ConllRelation
              ret.e1 = e1
              ret.e2 = e2
              ret.relType = "?"
              ret.wordId1 = e1.wordId
              ret.wordId2 = e2.wordId
              ret.sentId = sent.sentId
              ret
          })
        }

    }

    def searchForSid(sid: Int): Unit = {
      println(Console.MAGENTA)
      trainSentences.toList.filter(_.sentId == sid).foreach(println)
      trainTokens.toList.filter(_.sentId == sid).foreach(println)
      trainTokens.filter(_.sentId == sid).foreach(println)
      println(Console.CYAN)
      testSentences.toList.filter(_.sentId == sid).foreach(println)
      testTokens.toList.filter(_.sentId == sid).foreach(println)
      testRelations.filter(_.sentId == sid).foreach(println)
      println(Console.RESET)
    }

    //    searchForSid(5010)

    //  testReader.relations.toList.filter(_.sentId == sid).foreach(println)
    //  testReader.sentences.toList.filter(_.sentId == 5144).foreach(println)
    //  testReader.sentences.map(_.sentTokens).flatten.toList.filter(_.sentId == 5144).foreach(println)

    //  this ++ reader.instances.toList
    sentences populate trainSentences
    tokens populate trainTokens
    pairedRelations populate trainRelations
    //    this ++ blankRelation
    println("Done adding training")
    //  this.test  ++ testReader.instances.toList
    this testWith testSentences
    this testWith testTokens
    this testWith testRelations
    //    this testWith blankRelationTest

  }
}