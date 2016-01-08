package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saul.classifier.JointTrainSparseNetwork
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment.{ argumentTypeLearner1, argumentXuIdentifierGivenApredicate1 }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLConstraintClassifiersForExperiments.{ arg_IdentifyConstraintClassifier1, arg_TypeConstraintClassifier1 }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._

import scala.collection.JavaConversions._

object joinIsArg_TypeArg extends App {

  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)

  val XuPalmerCandidateArgsTraining = predicates.getTrainingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head))

  val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head))

  val a = relations() ~> relationsToArguments prop address
  val b = relations() ~> relationsToPredicates prop address

  val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))
  val negativePalmerTrainCandidates = XuPalmerCandidateArgsTraining.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))

  relations.populate(negativePalmerTrainCandidates)
  relations.populate(negativePalmerTestCandidates, false)

  println("all relations number after population:" + SRLDataModel.relations().size)

  argumentXuIdentifierGivenApredicate1.learn(5)

  print("argument identifier test results after 5 rounds:")

  argumentXuIdentifierGivenApredicate1.test()

  argumentTypeLearner1.learn(5)

  print("argument classifier test results after 5 rounds:")

  argumentTypeLearner1.test()
  // argumentTypeLearner1.classifier.classify(relations().head)
  //
  //  print("argument classifier L+I model (join with identification) test results:")
  //
  //arg_TypeConstraintClassifier1.test()

  //
  //  print("argument identifier L+I model (join with classifciation) test results:")
  //
  //  arg_IdentifyConstraintClassifier1.test()
  //
  JointTrainSparseNetwork.train(SRLDataModel, arg_TypeConstraintClassifier1 :: arg_IdentifyConstraintClassifier1 :: Nil, 40)

  print("argument classifier IBT model (join with identification) test results:")
  arg_TypeConstraintClassifier1.test()

  print("argument identifier IBT model (join with classification) test results:")

  arg_IdentifyConstraintClassifier1.test()

  //  argumentTypeLearner.learn(3)
  //  println("Training finished")
  //  println("Test multi class with 3 iterations:")
  //  argumentTypeLearner.test()
  //  println("Test constrained multi class:")
  //
  //  argTypeConstraintClassifier.test()
  //
  //  // argumentTypeLearner.forget()
  //
  //  JoinTrainSparseNetwork.train[TextAnnotation](SRLDataModel, argTypeConstraintClassifier:: Nil,50)
  //
  //  println("Test joint learner joint prediction:")
  //
  //  argTypeConstraintClassifier.test()
  //
  //  println("Test joint learner independent prediction:")
  //
  //  argumentTypeLearner.test()
}
