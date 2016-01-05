package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment.{ argumentXuIdentifierGivenApredicate1, predicateClassifier1 }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._

import scala.collection.JavaConversions._

/** Created by Parisa on 12/11/15.
  */
object pipeline extends App {

  SRLDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)

  // Generate predicate candidates by extracting all verb tokens
  val predicateTrainCandidates = tokens.getTrainingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
  val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

  // Remove the true predicates from the list of candidates (since they have a different label)
  val negativePredicateTrain = predicates(predicateTrainCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))
  val negativePredicateTest = predicates(predicateTestCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))

  predicates.populate(negativePredicateTrain)
  predicates.populate(negativePredicateTest, false)
  predicateClassifier1.learn(5)
  //predicateClassifier.save()
  //predicateClassifier.load()
  predicateClassifier1.test()
  predicateClassifier1.save()

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
  argumentXuIdentifierGivenApredicate1.test()

  println("pipeline argIdentification")
  //  val res = relations.getTestingInstances.map(x => {
  //    print("value", isArgumentPipePrediction(x))
  //    isArgumentPipePrediction(x)
  //  })
  //
  //  val res1 = relations.getAllInstances.map(x => isArgumentPipePrediction(x))
  //
  //  evaluation.Test(isArgumentXu_Gth, isArgumentPipePrediction, relations)
  //
  //  println("directly argIdentification")
  //  evaluation.Test(isArgumentXu_Gth, isArgumentPrediction, relations)

  print("finish")
}
