package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationBasicDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationConstraintClassifiers._
/** Created by Parisa on 5/20/15.
  */
object LplusIModel extends App {
  val iter = 5
  entityRelationBasicDataModel.populateWithConll()
  //Independent Learners
  personClassifier.learn(iter)
  orgClassifier.learn(iter)
  locationClassifier.learn(iter)
  worksForClassifier.learn(iter)
  livesInClassifier.learn(iter)

  //Test use the constraints
  println("Person Classifier Evaluation with training")
  println("=================================")
  PerConstraintClassifier.test(tokens())
  println("=================================")
  println("Organization Classifier Evaluation")
  println("=================================")
  orgConstraintClassifier.test(tokens())
  println("=================================")
  println("Location Classifier Evaluation")
  println("=================================")
  LocConstraintClassifier.test(tokens())
  println("=================================")
  println("WorkFor Classifier Evaluation")
  println("=================================")
  P_O_relationClassifier.test(pairs())
  println("=================================")
  println("LivesIn Classifier Evaluation")
  println("=================================")
  LiveIn_P_O_relationClassifier.test(pairs())
  println("=================================")

}

