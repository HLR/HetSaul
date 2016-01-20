package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

/** Created by Parisa on 1/14/16.
  */

import edu.illinois.cs.cogcomp.saul.evaluation.evaluation
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ argumentTypeLearner, argumentXuIdentifierGivenApredicate, predicateClassifier }
import org.slf4j.{ LoggerFactory, Logger }

object pipelineAppMultiGraph extends App {
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

  var srlGraphs: List[srlMultiGraph] = populatemultiGraphwithSRLData(useGoldPredicate, useGoldArgBoundaries)
  logger.info("population finished.")
  println(srlGraphs.map(x => (x.sentences() ~> x.sentencesToRelations).size).sum)
  println(srlGraphs.map(x => x.relations().size).sum)
  print(srlGraphs.map(x => (x.relations() ~> x.relationsToArguments).size).sum)
  logger.info("population finished")

  if (trainArgType && useGoldArgBoundaries && useGoldPredicate) {
    //train and test the argClassifier Given the ground truth Boundaries (i.e. no negative class).
    argumentTypeLearner.setModelDir("models_aTr")
    argumentTypeLearner.learn(10, srlGraphs.flatMap(x => x.relations.trainingSet))
    evaluation.Test(srlGraphs.head.argumentLabelGold, srlGraphs.head.typeArgumentPrediction, srlGraphs.flatMap(x => x.relations.testingSet))
    argumentTypeLearner.test(srlGraphs.flatMap((x => x.relations.testingSet)))
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
    //  evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations)
    println("\n =============================================================")
    argumentTypeLearner.test()
    argumentTypeLearner.save()
  }

  println("all relations number after population:" + srlDataModel.relations().size)
  if (trainPredicates && !useGoldPredicate) {
    predicateClassifier.setModelDir("models_dTr")
    println("Training predicate identifier")
    //   predicateClassifier.learn(100, predicates.trainingSet)
    predicateClassifier.save()
    print("isPredicate test results:")
    //   predicateClassifier.test(predicates.testingSet)
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
    argumentTypeLearner.learn(100, srlGraphs.flatMap(x => x.relations.trainingSet))
    print("argument classifier test results:")
    evaluation.Test(srlGraphs.head.argumentLabelGold, srlGraphs.head.typeArgumentPrediction, srlGraphs.flatMap(x => x.relations.testingSet))
    println("\n =============================================================")
    argumentTypeLearner.test(srlGraphs.flatMap(x => x.relations.testingSet))
    argumentTypeLearner.save()
  }
}
