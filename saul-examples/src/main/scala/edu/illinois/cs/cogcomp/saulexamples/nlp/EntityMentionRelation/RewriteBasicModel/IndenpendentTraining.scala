package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.Conll04_ReaderNew
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationBasicDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.classifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.classifiers.LivesInClassifier
import scala.collection.JavaConversions._

object IndenpendentTraining extends App {

  def populate_ER_graph = {
    val reader = new Conll04_ReaderNew("./data/EntityMentionRelation/conll04.corp", "Token")
    val trainSentences = reader.sentences.toList
    val trainTokens = trainSentences.flatMap(_.sentTokens).slice(1, 10)
    val trainRelations = reader.relations.toList

    sentences populate trainSentences
    tokens populate trainTokens
    pairs populate trainRelations
    testWith(trainTokens)
    testWith(trainRelations)
    testWith(trainSentences)
  }

  populate_ER_graph
  val it = 2
  println("Indepent Training with iteration " + it)
  PersonClassifier.learn(it)
  PersonClassifier.test()
  orgClassifier.learn(it)
  orgClassifier.test()
  LocClassifier.learn(it)
  LocClassifier.test()
  workForClassifier.learn(it)
  workForClassifier.test()
  LivesInClassifier.learn(it)
  LivesInClassifier.test()

}
