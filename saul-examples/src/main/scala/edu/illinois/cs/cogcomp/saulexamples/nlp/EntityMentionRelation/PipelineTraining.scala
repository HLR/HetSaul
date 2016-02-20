package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers._

object PipelineTraining extends App {
  val it = 20
  EntityRelationDataModel.populateWithConll()

  println("Running CV " + it)

  personClassifier.crossValidation(it)
  orgClassifier.crossValidation(it)
  locationClassifier.crossValidation(it)

  workForClassifierPipe.crossValidation(it)
  livesInClassifierPipe.crossValidation(it)

}
