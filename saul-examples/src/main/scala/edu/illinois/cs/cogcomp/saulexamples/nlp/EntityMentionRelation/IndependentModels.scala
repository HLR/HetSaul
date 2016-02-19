package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers._

object IndependentModels extends App {

  EntityRelationDataModel.populateWithConll()
  val iter = 5
  println("Person Classifier Evaluation")
  println("=================================")
  personClassifier.crossValidation(iter)
  println("=================================")
  println("Organization Classifier Evaluation")
  println("=================================")
  orgClassifier.crossValidation(iter)
  println("=================================")
  println("Location Classifier Evaluation")
  println("=================================")
  locationClassifier.crossValidation(iter)
  println("=================================")
  println("WorkFor Classifier Evaluation")
  println("=================================")
  worksForClassifier.crossValidation(iter)
  println("=================================")
  println("LivesIn Classifier Evaluation")
  println("=================================")
  livesInClassifier.crossValidation(iter)
  println("=================================")
}
