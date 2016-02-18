package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saul.classifier.JointTrainSparseNetwork
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlConstraintClassifiers._

object ibtApp extends App {

  val useGoldPredicate = true
  val useGoldBoundaries = false

  val srlGraphs = populatemultiGraphwithSRLData(false, useGoldPredicate, useGoldBoundaries)

  import srlGraphs._

  argumentTypeLearner.setModelDir("models_aTrJoin")

  argumentTypeLearner.learn(1, relations.getTrainingInstances)

  argumentTypeLearner.test(exclude = "candidate")

  JointTrainSparseNetwork(srlGraphs, argTypeConstraintClassifier::Nil, 1)

  argumentTypeLearner.save()

  argTypeConstraintClassifier.test(exclude ="candidate", outputGranularity = 100)


//  println("all relations number after population:" + SRLDataModel.relations().size)
//
//  argumentXuIdentifierGivenApredicate.learn(5)
//
//  print("argument identifier test results after 5 rounds:")
//
//  argumentXuIdentifierGivenApredicate.test()
//
//  argumentTypeLearner.learn(5)
//
//  print("argument classifier test results after 5 rounds:")
//
//  argumentTypeLearner.test()

//  JointTrainSparseNetwork(SRLDataModel, argTypeConstraintClassifier::Nil, 50)
//
//  println("**************************************************** go 50 iterations:")
//  JointTrainSparseNetwork.train(SRLDataModel, arg_Is_TypeConstraintClassifier :: arg_IdentifyConstraintClassifier :: Nil, 50)
//
//  println("argument classifier IBT model (join with identification) test results:")
//  arg_Is_TypeConstraintClassifier.test()
//  println("argument identifier IBT model (join with classification) test results:")
//  arg_IdentifyConstraintClassifier.test()
//
//  JointTrainSparseNetwork.train(SRLDataModel, arg_Is_TypeConstraintClassifier :: arg_IdentifyConstraintClassifier :: Nil, 50)
//
//  println("******************************************** additional 50 iterations:")
//
//  println("argument classifier IBT model (join with identification) test results:")
//  arg_Is_TypeConstraintClassifier.test()
//  println("argument identifier IBT model (join with classification) test results:")
//
//  arg_IdentifyConstraintClassifier.test()
//  argumentTypeLearner.save()
//  argumentXuIdentifierGivenApredicate.save()

  //TODO add more variations with combination of constraints
}
