package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.Conll04_ReaderNew
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.classifiers.PersonClassifier
import scala.collection.JavaConversions._

object entityRelationApp_someFeatureTests {

  def main(args: Array[String]) {

    val reader = new Conll04_ReaderNew("./data/EntityMentionRelation/conll04.corp", "Token")
    //  val testReader = new Conll04_RelationReaderNew("./data/conll04_test.corp", "Token",reader.sentences.size()+1)

    val trainSentences = reader.sentences.toList
    val trainTokens = trainSentences.flatMap(_.sentTokens)
    val trainRelations = reader.relations.toList

    //    val sentence = entityRelationDataModel.reader.sentences

    //    sentence.foreach(s => println(s.sentId))
    //    sentence.sentTokens.foreach(println)

    //    val t = sentence.sentTokens.get(2)

    //    println(t)
    //    println("====")
    //    entityRelationDataModel.tokens.getWithWindow(t,-4,3,'sid) foreach println
    //    println("====")
    //    entityRelationDataModel.tokens.getWithWindow(t,-3,3) foreach println
    //    println("====")
    //    entityRelationDataModel.tokens.nextOf(t,List('sid)) foreach println
    //    println("====")
    //    entityRelationDataModel.tokens.pervOf(t,List('sid)) foreach println
    //
    //    println("====")
    //    println(entityRelationDataModel.tokens.getWithRelativePosition(t,-3,List('sid)))
    //    println(entityRelationDataModel.tokens.getWithRelativePosition(t,-2,List('sid)))
    //    println(entityRelationDataModel.tokens.getWithRelativePosition(t,-1,List('sid)))
    //    println(entityRelationDataModel.tokens.getWithRelativePosition(t,0,List('sid)))
    //    println(entityRelationDataModel.tokens.getWithRelativePosition(t,1,List('sid)))
    //    println(entityRelationDataModel.tokens.getWithRelativePosition(t,2,List('sid)))
    //    println(entityRelationDataModel.tokens.getWithRelativePosition(t,3,List('sid)))
    //    println(entityRelationDataModel.tokens.getWithRelativePosition(t,4,List('sid)))

    //    val arr = entityRelationDataModel.reader.relations
    //
    //    arr.foreach(
    //    {
    //      x => println(x.e1.sentId + "===" +x.sentId)
    //    }
    //    )
    //
    //    println("-=-=-")

    //    val rarr = entityRelationDataModel.testReader.relations
    //
    //    rarr.foreach(
    //    {
    //      x => println(x.e1.sentId + "===" +x.sentId)
    //    }
    //    )

    //    (entityRelationDataModel.pairedRelations.getPIWittSI('sid,"0") map {
    //        p => entityRelationDataModel.pairedRelations.getWithPI(p)
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
