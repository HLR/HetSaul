package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

/** Created by Parisa on 1/14/16.
  */

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.evaluation.evaluation
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ predicateClassifier, argumentTypeLearner, argumentXuIdentifierGivenApredicate }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._
import org.slf4j.{ LoggerFactory, Logger }

import scala.collection.JavaConversions._

object pipelineApp extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  if (args.length > 0)
    println("Run with this parameters:\n -goldPred=true/false -goldBoundary=true/false -TrainPred=true/false" +
      " -TrainIdentifier=true/false -TrainType=true/false")
  def optArg(prefix: String) = args.find { _.startsWith(prefix) }.map { _.replaceFirst(prefix, "") }
  def optBoolean(prefix: String, default: Boolean) = optArg(prefix).map((x: String) => {
    if (x.trim == "true") true else false
  }).getOrElse(default)

  val useGoldPredicate = optBoolean("-goldPred=", false)
  val useGoldArgBoundaries = optBoolean("-goldBoundary=", false)
  val trainPredicates = optBoolean("-TrainPred=", false)
  val trainArgIdentifier = optBoolean("-TrainIdentifier=", false)
  val trainArgType = optBoolean("-TrainType=", true)

  if (!useGoldPredicate) {
    sentencesToTokens.addSensor(textAnnotationToTokens _)
  }
  populateGraphwithGoldSRL(srlDataModel, sentences)
  if (!useGoldPredicate) {

    val predicateTrainCandidates = tokens.getTrainingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
    val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

    val negativePredicateTrain = predicates(predicateTrainCandidates)
      .filterNot(cand => (predicates() prop address).contains(address(cand)))
    val negativePredicateTest = predicates(predicateTestCandidates)
      .filterNot(cand => (predicates() prop address).contains(address(cand)))
    logger.info("Populate the negative training predicate ...")
    predicates.populate(negativePredicateTrain)
    logger.info("Populate the negative testing predicate ...")
    predicates.populate(negativePredicateTest, train = false)
  }

  if (!useGoldArgBoundaries && !trainPredicates) {
    val XuPalmerCandidateArgsTraining = predicates.getTrainingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))
    val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))

    val a = relations() ~> relationsToArguments prop address
    sentencesToRelations.addSensor(textAnnotationToRelationMatch _)
    val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => a.contains(address(cand.getTarget)))
    val negativePalmerTrainCandidates = XuPalmerCandidateArgsTraining.filterNot(cand => a.contains(address(cand.getTarget)))
    logger.info("Populate the negative training arguments...")
    relations.populate(negativePalmerTrainCandidates)
    logger.info("Populate the negative testing arguments...")
    relations.populate(negativePalmerTestCandidates, train = false)
  }
  logger.info(""+(sentences() ~> sentencesToRelations).size)
  println(relations().size)
  print((relations() ~> relationsToArguments).size)
  logger.info("population finished.")
  if (trainArgType && useGoldArgBoundaries && useGoldPredicate) {
    //train and test the argClassifier Given the ground truth Boundaries (i.e. no negative class).
    argumentTypeLearner.setModelDir("models_aTr")
    argumentTypeLearner.learn(100)
    evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations.getTestingInstances)
    // argumentTypeLearner.test()
    argumentTypeLearner.save()
  }

  if (trainArgIdentifier && useGoldPredicate) {
    argumentXuIdentifierGivenApredicate.setModelDir("models_bTr")
    println("Training argument identifier")
    argumentXuIdentifierGivenApredicate.learn(100)
    print("isArgument test results:")
    argumentXuIdentifierGivenApredicate.test()
    argumentXuIdentifierGivenApredicate.save()
  }

  if (trainArgType && useGoldPredicate && !useGoldArgBoundaries) {
    argumentTypeLearner.setModelDir("models_cTr")
    println("Training argument classifier")
    argumentTypeLearner.learn(100)
    print("argument classifier test results:")
    evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations.getTestingInstances)
    println("\n =============================================================")
    argumentTypeLearner.test()
    argumentTypeLearner.save()
  }

  println("all relations number after population:" + srlDataModel.relations().size)
  if (trainPredicates && !useGoldPredicate) {
    predicateClassifier.setModelDir("models_dTr")
    println("Training predicate identifier")
    predicateClassifier.learn(100, predicates.trainingSet)
    predicateClassifier.save()
    print("isPredicate test results:")
    predicateClassifier.test(predicates.testingSet)
  }

  if (trainArgIdentifier && !useGoldPredicate) {
    argumentXuIdentifierGivenApredicate.setModelDir("models_eTr")
    println("Training argument identifier")
    argumentXuIdentifierGivenApredicate.learn(100)
    print("isArgument test results:")
    argumentXuIdentifierGivenApredicate.test()
    argumentXuIdentifierGivenApredicate.save()
  }

  if (trainArgType && !useGoldPredicate) {
    argumentTypeLearner.setModelDir("models_fTr")
    println("Training argument classifier")
    argumentTypeLearner.learn(100)
    print("argument classifier test results:")
    evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations.getTestingInstances)
    //argumentTypeLearner.test()
    argumentTypeLearner.save()
  }
}
