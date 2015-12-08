package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saul.classifier.trainingParadigms._
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.Conll04_ReaderNew
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationBasicDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationClassifiers._

import scala.collection.JavaConversions._

object IndependentERTraining extends App {

  def populate_ER_graph = {
    val reader = new Conll04_ReaderNew("./data/EntityMentionRelation/conll04.corp", "Token")
    val trainSentences = reader.sentences.toList
    val trainTokens = trainSentences.flatMap(_.sentTokens).slice(0, 20)
    val trainRelations = reader.relations.toList

    sentences populate trainSentences
    tokens populate trainTokens
    pairs populate trainRelations
  }

  populate_ER_graph
  forgetAll(personClassifier, orgClassifier, locationClassifier, worksForClassifier, livesInClassifier)
  // independent_train(personClassifier, orgClassifier, locationClassifier, worksForClassifier, livesInClassifier)
  independent_test(entityRelationBasicDataModel, personClassifier, orgClassifier, locationClassifier)
}
