package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

/** Created by Parisa on 1/5/16.
  */

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.evaluation.evaluation
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ predicateClassifier, argumentXuIdentifierGivenApredicate, argumentTypeLearner }
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._
import srlDataModel._
import scala.collection.JavaConversions._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlSensors._

object pipelineApp extends App {
  var trainPredicates = false
  var trainArgTypeWithGold = false
  var trainArgIdWithCandidates = true
  var trainArgTypeWithCandidates = false

  srlDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  populateGraphwithGoldSRL(srlDataModel, srlDataModel.sentences)

  if (trainArgTypeWithGold) {
    // Here first train and test the argClassifier Given the ground truth Boundaries (i.e. no negative class).
    argumentTypeLearner.learn(10)
    argumentTypeLearner.test()
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
      println("Training predicate identifier")
      predicateClassifier.learn(100)
      predicateClassifier.save()
      print("isPredicate test results:")
      predicateClassifier.test()
    }

    if (trainArgIdWithCandidates || trainArgTypeWithCandidates) {
      println("Training argument identifier")
      argumentXuIdentifierGivenApredicate.learn(100)
      print("isArgument test results:")
      argumentXuIdentifierGivenApredicate.test()
      argumentXuIdentifierGivenApredicate.save()
    }

    if (trainArgTypeWithCandidates) {
      println("Training argument classifier")
      argumentTypeLearner.learn(100)
      print("argument classifier test results:")
      argumentTypeLearner.test()
      argumentTypeLearner.save()
    }

    if (trainArgIdWithCandidates || trainArgTypeWithCandidates) {
      println("Pipeline argument identification")
      evaluation.Test(isArgumentXuGold, isArgumentPipePrediction, relations)
      println("Pipeline argument classification")
      evaluation.Test(argumentLabelGold, typeArgumentPipePrediction, relations)
    }
    if (trainArgTypeWithGold) {
      println("Direct argument identification")
      evaluation.Test(isArgumentXuGold, isArgumentPrediction, relations)
      println("Direct argument classification")
      evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations)
    }
    print("finish!")
  }
}
