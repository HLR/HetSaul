package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

/** Created by Parisa on 1/14/16.
  */

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.core.experiments.NotificationSender
import edu.illinois.cs.cogcomp.saul.evaluation.evaluation
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ argumentTypeLearner, argumentXuIdentifierGivenApredicate, predicateClassifier }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._

object pipelineApp extends App {

  if (args.length == 0) {
    println("Usage parameters:\n -goldPred=true/false -goldBoundary=true/false -TrainPred= true/false" +
      " -TrainIdentifier=true/false -TrainType=true/false")
    sys.exit()
  }
  def optArg(prefix: String) = args.find { _.startsWith(prefix) }.map { _.replaceFirst(prefix, "") }
  def optBoolean(prefix: String, default: Boolean) = optArg(prefix).map((x: String) => {
    if (x.trim == "true")
      true else false
  }).getOrElse(default)

  val useGoldPredicate = optBoolean("-goldPred=", default = false)
  val useGoldArgBoundaries = optBoolean("-goldBoundary=", default = false)
  val trainPredicates = optBoolean("-TrainPred=", default = false)
  val trainArgIdentifier = optBoolean("-TrainIdentifier=", default = false)
  val trainArgType = optBoolean("-TrainType=", default = true)

  println("Using the following parameters:" +
    "\n\tgoldPred: " + useGoldPredicate +
    "\n\tgoldBoundary: " + useGoldArgBoundaries +
    "\n\tTrainPred: " + trainPredicates +
    "\n\tTrainIdentifier: " + trainArgIdentifier +
    "\n\tTrainType: " + trainArgType)

  val expName = {
    if (trainArgType && useGoldArgBoundaries) "aTr"
    else if (trainArgIdentifier && useGoldPredicate) "bTr"
    else if (trainArgType && useGoldPredicate) "cTr"
    else if (trainPredicates) "dTr"
    else if (trainArgIdentifier && !useGoldPredicate) "eTr"
    else if (trainArgType && !useGoldPredicate) "fTr"
  }
  val notifier: NotificationSender = new NotificationSender("../data/notifier.key", "Saul", "SRL " + expName)
  val startTime = System.currentTimeMillis()

  if (!useGoldPredicate) {
    SRLDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  }
  populateGraphwithGoldSRL(SRLDataModel, SRLDataModel.sentences)

  if (!useGoldPredicate) {
    val predicateTrainCandidates = tokens.getTrainingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
    val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

    predicates.populate(predicateTrainCandidates)
    predicates.populate(predicateTestCandidates, train = false)
  }

  if (trainArgType && useGoldArgBoundaries) {
    //train and test the argClassifier Given the ground truth Boundaries (i.e. no negative class).
    argumentTypeLearner.setModelDir("models_aTr")
    argumentTypeLearner.learn(100)
    evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations.getTestingInstances)
    argumentTypeLearner.save()
  }

  if (!useGoldArgBoundaries && !trainPredicates) {
    val XuPalmerCandidateArgsTraining = predicates.getTrainingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))
    val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))

    sentencesToRelations.addSensor(textAnnotationToRelationMatch _)

    relations.populate(XuPalmerCandidateArgsTraining)
    relations.populate(XuPalmerCandidateArgsTesting, train = false)
  }
  println("all relations number after population:" + SRLDataModel.relations().size)

  if (trainPredicates) {
    predicateClassifier.setModelDir("models_dTr")
    println("Training predicate identifier")
    predicateClassifier.learn(100, predicates.getTestingInstances)
    predicateClassifier.save()
    print("isPredicate test results:")
    predicateClassifier.test(predicates.getTestingInstances)
  }

  if (trainArgIdentifier) {
    if (useGoldPredicate) argumentXuIdentifierGivenApredicate.setModelDir("models_bTr")
    else argumentXuIdentifierGivenApredicate.setModelDir("models_eTr")
    println("Training argument identifier")
    argumentXuIdentifierGivenApredicate.learn(100)
    print("isArgument test results:")
    argumentXuIdentifierGivenApredicate.test()
    argumentXuIdentifierGivenApredicate.save()
  }

  if (trainArgType) {
    if (useGoldPredicate) argumentTypeLearner.setModelDir("models_cTr")
    else argumentTypeLearner.setModelDir("models_fTr")
    println("Training argument classifier")
    argumentTypeLearner.learn(100)
    print("argument classifier test results:")
    evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations.getTestingInstances)
    argumentTypeLearner.save()
  }

  val endTime = System.currentTimeMillis()
  notifier.notify("Took " + ((endTime - startTime) / 60000) + " minutes")
}