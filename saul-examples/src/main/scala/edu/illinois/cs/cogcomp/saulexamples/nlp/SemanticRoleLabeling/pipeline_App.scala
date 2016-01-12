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

object pipeline_App extends App {

  srlDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  populateGraphwithGoldSRL(srlDataModel, srlDataModel.sentences)

  // Here first train and test the argClassifier Given the ground truth Boundaries (i.e. no negative class).

  argumentTypeLearner.learn(10)
  argumentTypeLearner.test()

  val predicateTrainCandidates = tokens.getTrainingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
  val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

  val negativePredicateTrain = predicates(predicateTrainCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))
  val negativePredicateTest = predicates(predicateTestCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))

  predicates.populate(negativePredicateTrain)
  predicates.populate(negativePredicateTest, false)

  val XuPalmerCandidateArgsTraining = predicates.getTrainingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head))

  val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head))

  val a = relations() ~> relationsToArguments prop address
  val b = relations() ~> relationsToPredicates prop address

  val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))
  val negativePalmerTrainCandidates = XuPalmerCandidateArgsTraining.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))

  relations.populate(negativePalmerTrainCandidates)
  relations.populate(negativePalmerTestCandidates, false)

  println("all relations number after population:" + srlDataModel.relations().size)
  print("isPredicate test results:")
  predicateClassifier.learn(100)
  predicateClassifier.save()
  predicateClassifier.test()

  println("directly argIdentification")
  argumentXuIdentifierGivenApredicate.learn(100)
  argumentXuIdentifierGivenApredicate.test()
  argumentXuIdentifierGivenApredicate.save()

  print("argument classifier test results:")
  argumentTypeLearner.learn(100)
  argumentTypeLearner.test()
  argumentTypeLearner.save()

  println("pipeline argIdentification")
  evaluation.Test(isArgumentXuGold, isArgumentPipePrediction, relations)
  println("directly argIdentification")
  evaluation.Test(isArgumentXuGold, isArgumentPrediction, relations)
  println("type prediction pipline:")
  evaluation.Test(argumentLabelGold, typeArgumentPipePrediction, relations)
  println("type prediction directly from candidates:")
  evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations)
  print("finish!")
}
