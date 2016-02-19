package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationClassifiers._

object PipelineTraining extends App {

  val it = 20
  entityRelationBasicDataModel.populateWithConll()
  def independentPipe(it: Int): Unit = {

    println("Running CV " + it)

    personClassifier.crossValidation(it)
    orgClassifier.crossValidation(it)
    locationClassifier.crossValidation(it)

    workForClassifierPipe.crossValidation(it)
    LivesInClassifierPipe.crossValidation(it)

  }

}
