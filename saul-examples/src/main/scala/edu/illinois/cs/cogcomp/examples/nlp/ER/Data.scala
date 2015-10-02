package edu.illinois.cs.cogcomp.examples.nlp.ER

import edu.illinois.cs.cogcomp.example.Conll04_RelationReaderNew
import edu.illinois.cs.cogcomp.example.er_task.datastruct.{ConllRawSentence, ConllRawToken, ConllRelation}
import edu.illinois.cs.cogcomp.example.er_task.reader.GazeteerReader
import edu.illinois.cs.cogcomp.examples.firstexamples.Classifiers.PersonClassifier
import edu.illinois.cs.cogcomp.lfs.data_model.DataModel

import scala.collection.mutable.{Map => MutableMap}
//import example.er_task.datastruct._




//import example.er_task.reader.GazeteerReader

//import example.Conll04_RelationReaderNew
import scala.collection.JavaConversions._
import scala.util.Random

/**
 * Created by haowu on 1/27/15.
 */

object ErDataModelExample extends DataModel{



  import edu.illinois.cs.cogcomp.lfs.data_model.DataModel._


  /**
   * Entity Definitions
   */



  val citygazet: GazeteerReader =  new GazeteerReader("./data/known_city.lst", "Gaz:City", true)
  val persongazet: GazeteerReader= new GazeteerReader("./data/known_maleFirst.lst", "Gaz:Person", true)
  persongazet.addFile("./data/known_femaleFirst.lst", true)


  val tokens = node[ConllRawToken](
    PrimaryKey = {
      t : ConllRawToken =>  String.valueOf(t.sentId) + ":" +String.valueOf(t.wordId)
    },


    SecondaryKeyMap = MutableMap(
      'sid -> ((t : ConllRawToken) => String.valueOf(t.sentId)),
      'wordid ->((t : ConllRawToken) => String.valueOf(t.wordId))
    ))

  val sentences = node[ConllRawSentence](
    PrimaryKey = {s : ConllRawSentence => String.valueOf(s.sentId)},
    SecondaryKeyMap = MutableMap('sid -> ((snet : ConllRawSentence) =>(String.valueOf(snet.sentId))))
  )

  val pairedRelations = node[ConllRelation](
    PrimaryKey = {s : ConllRelation => String.valueOf(s.hashCode())},

    SecondaryKeyMap = MutableMap(
      'sid -> ((t : ConllRelation) => String.valueOf(t.sentId)),
      'e1id ->((t : ConllRelation) => String.valueOf(t.wordId1)),
      'e2id ->((t : ConllRelation) => String.valueOf(t.wordId2))
    ))

  val NODES = ~~(tokens,sentences,pairedRelations)


// val RelationToToken = oneToManyRelationOf[ConllRelation,ConllRawToken]('contain)(('sid,'sid))


  /**
   * Access method definitions
   */


val RelationToPer = edge[ConllRelation,ConllRawToken]('containE1)//('sid === 'sid, 'e1id === 'wordid) //TODO check the runtime problem with the new edge implementation
val RelationToOrg = edge[ConllRelation,ConllRawToken]('containE2)//('sid === 'sid, 'e2id === 'wordid)//TODO check the runtime problem with the new edge implementation
val tokenContainsInSentence = edge[ConllRawSentence,ConllRawToken]('tokenInSentence)//('sid === 'sid)//TODO check the runtime problem with the new edge implementation

val EDGES = List(RelationToPer,RelationToOrg,tokenContainsInSentence) flatten


  /**
   * Attributes
   */


  val pos  = discreteAttributesGeneratorOf[ConllRawToken]('pos) {
    t : ConllRawToken => {
//      println(t.POS)
      t.POS :: Nil
    }
  }
 val word =discreteAttributesGeneratorOf[ConllRawToken]('word) {
   t: ConllRawToken => //t.getWords(false).zipWithIndex.map { case (w, i) => s"word${i}_$w"}.toList {
     t.getWords(false).toList
 }
  val phrase=discreteAttributesGeneratorOf[ConllRawToken]('phrase){
    t:ConllRawToken=> t.phrase :: Nil
  }

//  val phrase=discreteAttributesGeneratorOf[ConllRawToken]('phrase){
//    t:ConllRawToken=> t.phrase :: Nil
//  }

  val tokenSurface = discreteAttributeOf[ConllRawToken]('tokenSurface){
    _.getWords(false).toList.mkString(" ")
  }
  val containsSubPhraseMent=discreteAttributeOf[ConllRawToken]('containsSubPhraseMent){
    _.getWords(false).exists(_.contains("ment")).toString()
  }

  val containsSubPhraseIng=discreteAttributeOf[ConllRawToken]('containsSubPhraseIng){
    _.getWords(false).exists(_.contains("ing")).toString()
  }




  val containsInCityList = discreteAttributeOf[ConllRawToken]('containsInCityList){
    citygazet.isContainedIn(_).toString()
  }

  val containsInPersonList = discreteAttributeOf[ConllRawToken]('containsInCityList){
    persongazet.containsAny(_).toString()
  }


  val wordLen = realAttributeOf[ConllRawToken]('wordLen){
    t=>t.getLength
  }



  val relFeature =  discreteAttributesGeneratorOf[ConllRelation]('reltokenSurface){
    token : ConllRelation => {
      "w1-word-"+  token.e1.phrase ::"w2-word-"+ token.e2.phrase ::
      "w1-pos-"+token.e1.POS ::"w2-pos-"+ token.e2.POS ::
      "w1-city-" + citygazet.isContainedIn(token.e1) :: "w2-city-"+citygazet.isContainedIn(token.e2) ::
      "w1-per-" + persongazet.containsAny(token.e1) :: "w2-per-"+persongazet.containsAny(token.e2) ::
      "w1-ment-"+token.e1.getWords(false).exists(_.contains("ing")) :: "w2-ment-"+token.e2.getWords(false).exists(_.contains("ing")) ::
      "w1-ing-"+token.e1.getWords(false).exists(_.contains("ing")) :: "w2-ing-"+token.e2.getWords(false).exists(_.contains("ing")) ::
//        "w1Typ"+token.e1.entType :: "w2Typ"+token.e2.entType ::
      Nil
    }
  }


  val relPos =  discreteAttributesGeneratorOf[ConllRelation]('reltokenSurface){
    rela : ConllRelation => {
      val e1 = rela.e1
      val e2 = rela.e2

        this.getNodeWithType[ConllRawToken].getWithWindow(e1,-2,2,'sid).zipWithIndex.map({
          case (Some(t),idx) => s"left-$idx-pos-${t.POS} "
          case (None,idx) => s"left-$idx-pos-EMPTY "
        }) ++
     this.getNodeWithType[ConllRawToken].getWithWindow(e2,-2,2,'sid).zipWithIndex.map({
       case (Some(t),idx) => s"right-$idx-pos-${t.POS} "
       case (None,idx) => s"right-$idx-pos-EMPTY} "
      })

    }
  }

//
//  val relativePosition = realAttributesGeneratorOf[ConllRelation]('relPositionShift){
//    r => Math.abs(r.wordId1 - r.wordId2).toDouble :: Nil
//  }

    //
  /**
   * Labelers
   */

  val entityType = discreteAttributeOf[ConllRawToken]('entityType){
    t : ConllRawToken => t.entType
  }

  val relationType = discreteAttributeOf[ConllRelation]('relationType){
    r : ConllRelation=>
//      println(r.relType)
      r.relType
  }

  val PROPERTIES = List(word,pos,containsInPersonList,relFeature,relPos)


//  this ++



  def readAll() = {
    val reader = new Conll04_RelationReaderNew("./data/conll04.corp", "Token")
    //  val testReader = new Conll04_RelationReaderNew("./data/conll04_test.corp", "Token",reader.sentences.size()+1)


    val trainSentences = reader.sentences.toList
    val trainTokens = trainSentences.map(_.sentTokens).flatten.toList
    val trainRelations = reader.relations.toList


    this ++ trainSentences
    this ++ trainTokens
    this ++ trainRelations
    this.testWith(trainTokens)
    this.testWith(trainRelations)
    this.testWith(trainSentences)

  }




  def read(fold : Int) = {

    println(s"Running fold ${fold}")


    val lower : Int = 1103 * fold
    val upper : Int = 1103 * (fold + 1)


    println( s"testing with [$lower $upper]")

    val reader = new Conll04_RelationReaderNew("./data/conll04.corp", "Token")
    //  val testReader = new Conll04_RelationReaderNew("./data/conll04_test.corp", "Token",reader.sentences.size()+1)

    val trainSentences = reader.sentences.toList.filter(s => s.sentId < lower || s.sentId > upper)
    val trainTokens = trainSentences.map(_.sentTokens).flatten.toList
    val trainRelations = reader.relations.toList.filter(s => s.sentId < lower || s.sentId > upper)


    println(trainSentences.size)

    val blankRelation = trainSentences.map({

      sent => {
        val zipped = Random.shuffle(sent.sentTokens.toList).zip(Random.shuffle(sent.sentTokens.toList))
        val relationInSent = sent.relations
        val filteredZip = zipped.filterNot({   case(e1,e2) => relationInSent.exists({r => (r.e1 == e1) && (r.e2 == e2)})   })
        filteredZip.map({case(e1,e2) =>  val ret = new ConllRelation
          ret.e1 = e1
          ret.e2 = e2
          ret.relType = "?"
          ret.wordId1 = e1.wordId
          ret.wordId2 = e2.wordId
          ret.sentId = sent.sentId
          ret
        })
      }

    }).flatten



//    val trainSentences = reader.sentences.toList.filter(_.sentId < 4750)
//    println(trainSentences.map(s => s.relations).flatten.size)
//    println(trainRelations.size)


    val testSentences = reader.sentences.toList.filter(s => s.sentId >= lower && s.sentId <= upper)
    val testTokens = testSentences.map(_.sentTokens).flatten.toList
    val testRelations = reader.relations.toList.filter(s => s.sentId >= lower && s.sentId <= upper)

    println(testSentences.size)


    val blankRelationTest : List[ConllRelation]= testSentences.map({

      sent => {
        val zipped = Random.shuffle(sent.sentTokens.toList).zip(Random.shuffle(sent.sentTokens.toList))
        val relationInSent = sent.relations
        val filteredZip = zipped.filterNot({   case(e1,e2) => relationInSent.exists({r => (r.e1 == e1) && (r.e2 == e2)})   })
        filteredZip.map({case(e1,e2) =>  val ret = new ConllRelation
          ret.e1 = e1
          ret.e2 = e2
          ret.relType = "?"
          ret.wordId1 = e1.wordId
          ret.wordId2 = e2.wordId
          ret.sentId = sent.sentId
          ret
        })
      }

    }).flatten



    def searchForSid(sid : Int) : Unit = {
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
    this ++ trainSentences
    this ++ trainTokens
    this ++ trainRelations
//    this ++ blankRelation
    println("Done adding training")

    //  this.test  ++ testReader.instances.toList


    this testWith testSentences
    this testWith testTokens
    this testWith testRelations
//    this testWith blankRelationTest

  }




}


object Data {


  def main(args: Array[String]){


    val reader = new Conll04_RelationReaderNew("./data/conll04.corp", "Token")
    //  val testReader = new Conll04_RelationReaderNew("./data/conll04_test.corp", "Token",reader.sentences.size()+1)

    val trainSentences = reader.sentences.toList
    val trainTokens = trainSentences.map(_.sentTokens).flatten.toList
    val trainRelations = reader.relations.toList


    //    val sentence = ErDataModelExample.reader.sentences

//    sentence.foreach(s => println(s.sentId))
    //    sentence.sentTokens.foreach(println)

//    val t = sentence.sentTokens.get(2)

//    println(t)
//    println("====")
//    ErDataModelExample.tokens.getWithWindow(t,-4,3,'sid) foreach println
//    println("====")
//    ErDataModelExample.tokens.getWithWindow(t,-3,3) foreach println
//    println("====")
//    ErDataModelExample.tokens.nextOf(t,List('sid)) foreach println
//    println("====")
//    ErDataModelExample.tokens.pervOf(t,List('sid)) foreach println
//
//    println("====")
//    println(ErDataModelExample.tokens.getWithRelativePosition(t,-3,List('sid)))
//    println(ErDataModelExample.tokens.getWithRelativePosition(t,-2,List('sid)))
//    println(ErDataModelExample.tokens.getWithRelativePosition(t,-1,List('sid)))
//    println(ErDataModelExample.tokens.getWithRelativePosition(t,0,List('sid)))
//    println(ErDataModelExample.tokens.getWithRelativePosition(t,1,List('sid)))
//    println(ErDataModelExample.tokens.getWithRelativePosition(t,2,List('sid)))
//    println(ErDataModelExample.tokens.getWithRelativePosition(t,3,List('sid)))
//    println(ErDataModelExample.tokens.getWithRelativePosition(t,4,List('sid)))

//    val arr = ErDataModelExample.reader.relations
//
//    arr.foreach(
//    {
//      x => println(x.e1.sentId + "===" +x.sentId)
//    }
//    )
//
//    println("-=-=-")

//    val rarr = ErDataModelExample.testReader.relations
//
//    rarr.foreach(
//    {
//      x => println(x.e1.sentId + "===" +x.sentId)
//    }
//    )

//    (ErDataModelExample.pairedRelations.getPIWittSI('sid,"0") map {
//        p => ErDataModelExample.pairedRelations.getWithPI(p)
//      }).foreach(println)


    //    sentence.printSentence()

    PersonClassifier.learn(10)
//    PersonClassifier.test(testData)
//    orgClassifier.learn(10)
//    orgClassifier.test(testData)
//    LocClassifier.learn(10)
//    LocClassifier.test(testData)
//    workForClassifier.learn(10)
//    workForClassifier.test(testData)
  }
}
