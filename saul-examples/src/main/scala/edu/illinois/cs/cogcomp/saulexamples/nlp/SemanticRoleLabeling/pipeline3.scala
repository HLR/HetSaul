package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

/** Created by Parisa on 1/5/16.
  */

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.evaluation.evaluation
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment.{ argumentTypeLearner1, argumentXuIdentifierGivenApredicate1, predicateClassifier1 }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._

import scala.collection.JavaConversions._

object pipeline3 extends App {

  SRLDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)

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

  println("all relations number after population:" + SRLDataModel.relations().size)
  print("isPredicate test results:")
  predicateClassifier1.learn(10)
  predicateClassifier1.save()
  predicateClassifier1.test()

  print("argument classifier test results:")
  argumentTypeLearner1.learn(10)
  argumentTypeLearner1.test()
  argumentTypeLearner1.save()

  println("directly argIdentification")
  argumentXuIdentifierGivenApredicate1.learn(10)
  argumentXuIdentifierGivenApredicate1.test()
  argumentXuIdentifierGivenApredicate1.save()

  println("pipeline argIdentification")
  evaluation.Test(isArgumentXu_Gth, isArgumentPipePrediction, relations)
  println("directly argIdentification")
  evaluation.Test(isArgumentXu_Gth, isArgumentPrediction, relations)
  println("type prediction pipline:")
  evaluation.Test(argumentLabel_Gth, typeArgumentPipePrediction, relations)
  println("type prediction directly from candidates:")
  evaluation.Test(argumentLabel_Gth, typeArgumentPrediction, relations)
  print("finish!")
}
