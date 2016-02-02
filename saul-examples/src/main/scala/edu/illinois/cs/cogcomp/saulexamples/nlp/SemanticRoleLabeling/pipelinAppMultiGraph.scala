package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

/** Created by Parisa on 1/14/16.
  */

import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{argumentTypeLearner, argumentXuIdentifierGivenApredicate, predicateClassifier}
import org.slf4j.{Logger, LoggerFactory}

object pipelineAppMultiGraph extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  if (args.length == 0){
    println("Usage parameters:\n -goldPred=true/false -goldBoundary=true/false -TrainPred=true/false" +
      " -TrainIdentifier=true/false -TrainType=true/false")
    sys.exit()
  }
  def optArg(prefix: String) = args.find { _.startsWith(prefix) }.map { _.replaceFirst(prefix, "") }
  def optBoolean(prefix: String, default: Boolean) = optArg(prefix).map((x: String) => {
    if (x.trim == "true") true else false
  }).getOrElse(default)

  val useGoldPredicate = optBoolean("-goldPred=", true)
  val useGoldArgBoundaries = optBoolean("-goldBoundary=", true)
  val trainPredicates = optBoolean("-TrainPred=", false)
  val trainArgIdentifier = optBoolean("-TrainIdentifier=", false)
  val trainArgType = optBoolean("-TrainType=", true)

  logger.info("Using the following parameters:" +
    "\n\tgoldPred: " + useGoldPredicate +
    "\n\tgoldBoundary: " + useGoldArgBoundaries +
    "\n\tTrainPred: " + trainPredicates +
    "\n\tTrainIdentifier: " + trainArgIdentifier +
    "\n\tTrainType: " + trainArgType)

  val expName = {
    if (trainArgType && useGoldArgBoundaries && useGoldPredicate) "aTr"
    else if (trainArgIdentifier && useGoldPredicate && useGoldPredicate) "bTr"
    else if (trainArgType && useGoldPredicate && !useGoldArgBoundaries) "cTr"
    else if (trainPredicates && useGoldPredicate) "dTr"
    else if (trainArgIdentifier && !useGoldPredicate) "eTr"
    else if (trainArgType && !useGoldPredicate) "fTr"
  }
  val startTime = System.currentTimeMillis()
  logger.info("population starts.")

  val srlGraphs = populatemultiGraphwithSRLData(false, useGoldPredicate, useGoldArgBoundaries)
 import srlGraphs._
  logger.info("population finished.")
  println("sen:"+(sentences()~> sentencesToRelations).size)
  println("rel:"+relations().size)
  print("arg"+arguments().size)
  print("tok"+srlGraphs.tokens().size)
  if (trainArgType && useGoldArgBoundaries && useGoldPredicate) {
    argumentTypeLearner.setModelDir("models_aTr")
    argumentTypeLearner.learn(100, relations.trainingSet)
    argumentTypeLearner.test()
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
    argumentTypeLearner.save()
    print("argument classifier test results:")
    argumentTypeLearner.test(relations.testingSet, typeArgumentPrediction, argumentLabelGold,"candidate")
    }

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
    argumentTypeLearner.learn(100, relations.trainingSet)
    print("argument classifier test results:")
    argumentTypeLearner.test(relations.testingSet, typeArgumentPrediction, argumentLabelGold,"candidate")
    println("\n =============================================================")
    argumentTypeLearner.test(relations.testingSet)
    argumentTypeLearner.save()
  }
}
