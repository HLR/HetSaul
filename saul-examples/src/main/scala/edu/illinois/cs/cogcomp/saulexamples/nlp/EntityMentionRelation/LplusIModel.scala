package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationConstrainedClassifiers._
import EntityRelationDataModel._
/** Created by Parisa on 5/20/15.
  */
object LplusIModel extends App {
  val iter = 5
  EntityRelationDataModel.populateWithConll()
  //Independent Learners
  personClassifier.learn(iter)
  orgClassifier.learn(iter)
  locationClassifier.learn(iter)
  worksForClassifier.learn(iter)
  livesInClassifier.learn(iter)

  //Test use the constraints
  println("Person Classifier Evaluation with training")
  println("=================================")
  perConstraintClassifier.test(tokens())
  println("=================================")
  println("Organization Classifier Evaluation")
  println("=================================")
  orgConstraintClassifier.test(tokens())
  println("=================================")
  println("Location Classifier Evaluation")
  println("=================================")
  locConstraintClassifier.test(tokens())
  println("=================================")
  println("WorkFor Classifier Evaluation")
  println("=================================")
  work_P_O_relationClassifier.test(pairs())
  println("=================================")
  println("LivesIn Classifier Evaluation")
  println("=================================")
  liveIn_P_O_relationClassifier.test(pairs())
  println("=================================")

}

