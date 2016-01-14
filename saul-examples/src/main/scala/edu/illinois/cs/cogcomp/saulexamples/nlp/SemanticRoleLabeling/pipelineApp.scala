package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

/** Created by Parisa on 1/5/16.
  */

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ argumentTypeLearner, argumentXuIdentifierGivenApredicate, predicateClassifier }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._

import scala.collection.JavaConversions._

object pipelineApp extends App {
  val trainPredicates = true
  val trainArgTypeWithGold = false
  val trainArgIdWithCandidates = false
  val trainArgTypeWithCandidates = false
  val modelsDir = "models_aTr"

  if (!trainArgTypeWithGold) {
    srlDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  }
  populateGraphwithGoldSRL(srlDataModel, srlDataModel.sentences)

  if (trainArgTypeWithGold) {
    // Here first train and test the argClassifier Given the ground truth Boundaries (i.e. no negative class).
    argumentTypeLearner.setModelDir(modelsDir)
    argumentTypeLearner.learn(100)
    argumentTypeLearner.test()
    argumentTypeLearner.save()
  }

  if (trainArgIdWithCandidates || trainArgTypeWithCandidates || trainPredicates) {
    val predicateTrainCandidates = tokens.getTrainingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
    val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

    val negativePredicateTrain = predicates(predicateTrainCandidates)
      .filterNot(cand => (predicates() prop address).contains(address(cand)))
    val negativePredicateTest = predicates(predicateTestCandidates)
      .filterNot(cand => (predicates() prop address).contains(address(cand)))

    predicates.populate(negativePredicateTrain)
    predicates.populate(negativePredicateTest, train = false)

    val XuPalmerCandidateArgsTraining = predicates.getTrainingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))
    val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))

    val a = relations() ~> relationsToArguments prop address

    val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => a.contains(address(cand.getTarget)))
    val negativePalmerTrainCandidates = XuPalmerCandidateArgsTraining.filterNot(cand => a.contains(address(cand.getTarget)))

    relations.populate(negativePalmerTrainCandidates)
    relations.populate(negativePalmerTestCandidates, train = false)
    println("all relations number after population:" + srlDataModel.relations().size)

    if (trainPredicates) {
      predicateClassifier.setModelDir("models_dTr")
      println("Training predicate identifier")
      predicateClassifier.learn(100)
      predicateClassifier.save()
      print("isPredicate test results:")
      predicateClassifier.test()
    }

    if (trainArgIdWithCandidates) {
      argumentXuIdentifierGivenApredicate.setModelDir("models_bTr")
      println("Training argument identifier")
      argumentXuIdentifierGivenApredicate.learn(100)
      print("isArgument test results:")
      argumentXuIdentifierGivenApredicate.test()
      argumentXuIdentifierGivenApredicate.save()
    }

    if (trainArgTypeWithCandidates) {
      argumentTypeLearner.setModelDir("models_cTr")
      println("Training argument classifier")
      argumentTypeLearner.learn(100)
      print("argument classifier test results:")
      argumentTypeLearner.test()
      argumentTypeLearner.save()
    }
  }
}
