package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationClassifiers._

object PipelineTraining extends App {
  val it = 20
  entityRelationDataModel.populateWithConll()
  def independentPipe(it: Int): Unit = {

    println("Running CV " + it)

    personClassifier.crossValidation(it)
    orgClassifier.crossValidation(it)
    locationClassifier.crossValidation(it)

    workForClassifierPipe.crossValidation(it)
    LivesInClassifierPipe.crossValidation(it)

  }
}
