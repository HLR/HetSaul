package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saul.classifier.JointTrainSparseNetwork
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ argumentTypeLearner, argumentXuIdentifierGivenApredicate }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlConstraintClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLSensors._

import scala.collection.JavaConversions._

object ibtApp extends App {

  populateGraphwithGoldSRL(SRLDataModel, SRLDataModel.sentences)

  val XuPalmerCandidateArgsTraining = predicates.getTrainingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))

  val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))

  val a = relations() ~> relationsToArguments prop address
  val b = relations() ~> relationsToPredicates prop address

  val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))
  val negativePalmerTrainCandidates = XuPalmerCandidateArgsTraining.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))

  relations.populate(negativePalmerTrainCandidates)
  relations.populate(negativePalmerTestCandidates, false)

  println("all relations number after population:" + SRLDataModel.relations().size)

  argumentXuIdentifierGivenApredicate.learn(5)

  print("argument identifier test results after 5 rounds:")

  argumentXuIdentifierGivenApredicate.test()

  argumentTypeLearner.learn(5)

  print("argument classifier test results after 5 rounds:")

  argumentTypeLearner.test()

  println("**************************************************** go 50 iterations:")
  JointTrainSparseNetwork.train(SRLDataModel, arg_Is_TypeConstraintClassifier :: arg_IdentifyConstraintClassifier :: Nil, 50)

  println("argument classifier IBT model (join with identification) test results:")
  arg_Is_TypeConstraintClassifier.test()
  println("argument identifier IBT model (join with classification) test results:")
  arg_IdentifyConstraintClassifier.test()

  JointTrainSparseNetwork.train(SRLDataModel, arg_Is_TypeConstraintClassifier :: arg_IdentifyConstraintClassifier :: Nil, 50)

  println("******************************************** additional 50 iterations:")

  println("argument classifier IBT model (join with identification) test results:")
  arg_Is_TypeConstraintClassifier.test()
  println("argument identifier IBT model (join with classification) test results:")

  arg_IdentifyConstraintClassifier.test()
  argumentTypeLearner.save()
  argumentXuIdentifierGivenApredicate.save()

  //TODO add more variations with combination of constraints
}
