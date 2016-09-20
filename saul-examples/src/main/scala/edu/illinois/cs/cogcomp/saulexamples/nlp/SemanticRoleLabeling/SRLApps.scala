/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import java.io.File

import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.classifier.{ ClassifierUtils, JointTrainSparseNetwork }
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLConstrainedClassifiers.argTypeConstraintClassifier

object SRLApps extends Logging {
  import SRLConfigurator._

  val properties: ResourceManager = {
    // Load the default properties if the user hasn't entered a file as an argument
    //if (args.length == 0) {
    logger.info("Loading default configuration parameters")
    new SRLConfigurator().getDefaultConfig
    //} else {
    // logger.info("Loading parameters from {}", args(0))
    //new SRLConfigurator().getConfig(new ResourceManager(args(0)))
    // }
  }
  val modelDir = properties.getString(MODELS_DIR) +
    File.separator + properties.getString(SRLConfigurator.SRL_MODEL_DIR) + File.separator
  val srlPredictionsFile = properties.getString(SRLConfigurator.SRL_OUTPUT_FILE)
  val runningMode = properties.getBoolean(SRLConfigurator.RUN_MODE)
  val trainingMode = properties.getString(SRLConfigurator.TRAINING_MODE)

  // Training parameters
  val trainPredicates = properties.getBoolean(SRLConfigurator.SRL_TRAIN_PREDICATES)
  val trainArgIdentifier = properties.getBoolean(SRLConfigurator.SRL_TRAIN_ARG_IDENTIFIERS)
  val trainArgType = properties.getBoolean(SRLConfigurator.SRL_TRAIN_ARG_TYPE)

  // Testing parameters
  val testWithConstraints = properties.getBoolean(SRLConfigurator.SRL_TEST_CONSTRAINTS)
  val testWithPipeline = properties.getBoolean(SRLConfigurator.SRL_TEST_PIPELINE)

  val useGoldPredicate = properties.getBoolean(SRLConfigurator.SRL_GOLD_PREDICATES)
  val useGoldBoundaries = properties.getBoolean(SRLConfigurator.SRL_GOLD_ARG_BOUNDARIES)

  val modelJars = properties.getString(SRLConfigurator.SRL_JAR_MODEL_PATH)

  val expName: String = {
    if (trainingMode.equals("other"))
      if (trainArgType && useGoldBoundaries && useGoldPredicate && trainingMode.equals("other")) "aTr"
      else if (trainArgIdentifier && useGoldPredicate && useGoldPredicate) "bTr"
      else if (trainArgType && useGoldPredicate && !useGoldBoundaries) "cTr"
      else if (trainPredicates && useGoldPredicate) "dTr"
      else if (trainArgIdentifier && !useGoldPredicate) "eTr"
      else if (trainArgType && !useGoldPredicate) "fTr"
      else ""
    else if (trainingMode.equals("pipeline")) "pTr"
    else if (trainingMode.equals("joint")) "jTr"
    else ""
  }

  val startTime = System.currentTimeMillis()
  logger.info("population starts.")

  // Here, the data is loaded into the graph
  val srlDataModelObject = PopulateSRLDataModel(testOnly = runningMode, useGoldPredicate, useGoldBoundaries)

  import srlDataModelObject._

  logger.info("all relations number after population:" + relations().size)
  logger.info("all sentences number after population:" + sentences().size)
  logger.info("all predicates number after population:" + predicates().size)
  logger.info("all arguments number after population:" + arguments().size)
  logger.info("all tokens number after population:" + tokens().size)
}

object RunningApps extends App with Logging {
  import SRLApps._
  import SRLApps.srlDataModelObject._
  // TRAINING
  if (!runningMode) {
    expName match {

      case "aTr" =>
        argumentTypeLearner.modelDir = modelDir + expName
        argumentTypeLearner.learn(100, relations.getTrainingInstances)
        argumentTypeLearner.test()
        argumentTypeLearner.save()

      case "bTr" =>
        argumentXuIdentifierGivenApredicate.modelDir = modelDir + expName
        logger.info("Training argument identifier")
        argumentXuIdentifierGivenApredicate.learn(100)
        logger.info("isArgument test results:")
        argumentXuIdentifierGivenApredicate.test()
        argumentXuIdentifierGivenApredicate.save()

      case "cTr" =>
        argumentTypeLearner.modelDir = modelDir + expName
        logger.info("Training argument classifier")
        argumentTypeLearner.learn(100)
        argumentTypeLearner.save()
        logger.info("argument classifier test results:")
        argumentTypeLearner.test(relations.getTestingInstances, typeArgumentPrediction, argumentLabelGold, "candidate")

      case "dTr" =>
        predicateClassifier.modelDir = modelDir + expName
        logger.info("Training predicate identifier...")
        predicateClassifier.learn(100, predicates.getTrainingInstances)
        predicateClassifier.save()
        logger.info("isPredicate test results:")
        predicateClassifier.test(predicates.getTestingInstances)

      case "eTr" =>
        argumentXuIdentifierGivenApredicate.modelDir = modelDir + expName
        logger.info("Training argument identifier...")
        argumentXuIdentifierGivenApredicate.learn(100)
        logger.info("isArgument test results:")
        argumentXuIdentifierGivenApredicate.test()
        argumentXuIdentifierGivenApredicate.save()

      case "fTr" =>
        argumentTypeLearner.modelDir = modelDir + expName
        logger.info("Training argument classifier...")
        argumentTypeLearner.learn(100)
        logger.info("argument classifier test results:")
        argumentTypeLearner.test()
        argumentTypeLearner.test(relations.getTestingInstances, typeArgumentPrediction, argumentLabelGold, "candidate")
        argumentTypeLearner.save()

      case "pTr" =>
        ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_bTr/", argumentXuIdentifierGivenApredicate)
        val training = relations.getTrainingInstances.filter(x => argumentXuIdentifierGivenApredicate(x).equals("true"))
        argumentTypeLearner.modelDir = modelDir
        argumentTypeLearner.learn(100, training)
        logger.info("Test without pipeline:")
        argumentTypeLearner.test(exclude = "candidate")
        argumentTypeLearner.save()
        logger.info("Test with pipeline:")
        argumentTypeLearner.test(
          prediction = typeArgumentPipeGivenGoldPredicate,
          groundTruth = argumentLabelGold, exclude = "candidate"
        )

      case "jTr" =>
        argumentTypeLearner.modelDir = modelDir + expName
        val outputFile = modelDir + srlPredictionsFile
        logger.info("Global training... ")
        JointTrainSparseNetwork(sentences, argTypeConstraintClassifier :: Nil, 100, true)
        argumentTypeLearner.save()
        argTypeConstraintClassifier.test(relations.getTestingInstances, outputFile, 200, exclude = "candidate")
    }
  }

  // TESTING
  if (runningMode) {
    (testWithPipeline, testWithConstraints) match {

      case (true, true) =>
        ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_bTr/", argumentXuIdentifierGivenApredicate)
        ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_aTr/", argumentTypeLearner)
        argumentTypeLearner.test(
          prediction = typeArgumentPipeGivenGoldPredicateConstrained,
          groundTruth = argumentLabelGold, exclude = "candidate"
        )

      case (true, false) =>
        ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_bTr/", argumentXuIdentifierGivenApredicate)
        ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_aTr/", argumentTypeLearner)
        argumentTypeLearner.test(
          prediction = typeArgumentPipeGivenGoldPredicate,
          groundTruth = argumentLabelGold, exclude = "candidate"
        )

      case (false, true) =>
        ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_aTr/", argumentTypeLearner)
        argTypeConstraintClassifier.test(outputGranularity = 100, exclude = "candidate")

      case (false, false) =>
        ClassifierUtils.LoadClassifier(SRLConfigurator.SRL_JAR_MODEL_PATH.value + "/models_aTr/", argumentTypeLearner)
        argumentTypeLearner.test(exclude = "candidate")
    }
  }
}

