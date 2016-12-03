/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import java.io.File

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.saul.classifier.{ ClassifierUtils, JointTrainSparseNetwork }
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLConstrainedClassifiers.argTypeConstraintClassifier

object SRLscalaConfigurator {

  val TREEBANK_HOME = "../saul-examples/src/test/resources/SRLToy/treebank"
  val PROPBANK_HOME = "../saul-examples/src/test/resources/SRLToy/propbank"

  val TEST_SECTION = 0
  val TRAIN_SECTION_S = 2
  val TRAIN_SECTION_E = 21

  val MODELS_DIR = "../models"
  val USE_CURATOR = false

  // The running mode of the program. Can be "true" for only testing, or  "false" for training
  val TEST_MODE: Boolean = true

  // The training mode for the examples. Can be "pipeline", "joint", "jointLoss" or "other"
  val TRAINING_MODE = "joint"

  /*********** SRL PROPERTIES ***********/
  // The (sub)directory to store and retrieve the trained SRL models (to be used with MODELS_DIR)
  val SRL_MODEL_DIR = "srl"
  val SRL_JAR_MODEL_PATH = "models"

  // This is used to determine the parse view in SRL experiments (can be ViewNames.GOLD or ViewNames.STANFORD)
  // For replicating the published experiments this needs to be GOLD
  val SRL_PARSE_VIEW = ViewNames.PARSE_GOLD

  // A file to store the predictions of the SRL classifier (for argument types only)
  val SRL_OUTPUT_FILE = "srl-predictions.txt"

  // Whether to use gold predicates (if FALSE, predicateClassifier will be used instead)
  val SRL_GOLD_PREDICATES = true

  // Whether to use gold argument boundaries (if FALSE, argumentXuIdentifierGivenApredicate will be used instead)
  val SRL_GOLD_ARG_BOUNDARIES = true

  /*Testing parameters*/

  // Should we use the pipeline during testing
  val SRL_TEST_PIPELINE = false
  // Should we use constraints during testing
  val SRL_TEST_CONSTRAINTS = false

  /*Training parameters*/

  // Should we train a predicate classifier given predicate candidates
  val SRL_TRAIN_PREDICATES = false
  // Should we train an argument identifier given the XuPalmer argument candidates
  val SRL_TRAIN_ARG_IDENTIFIERS = false
  // Should we train an argument type classifier
  val SRL_TRAIN_ARG_TYPE = true

}

object SRLApps extends Logging {

  import SRLscalaConfigurator._

  val modelDir = MODELS_DIR + File.separator + SRL_MODEL_DIR + File.separator

  val expName: String = {
    if (TRAINING_MODE.equals("other"))
      if (SRL_TRAIN_ARG_TYPE && SRL_GOLD_ARG_BOUNDARIES && SRL_GOLD_PREDICATES && TRAINING_MODE.equals("other")) "aTr"
      else if (SRL_TRAIN_ARG_IDENTIFIERS && SRL_GOLD_PREDICATES && SRL_GOLD_PREDICATES) "bTr"
      else if (SRL_TRAIN_ARG_TYPE && SRL_GOLD_PREDICATES && !SRL_GOLD_ARG_BOUNDARIES) "cTr"
      else if (SRL_TRAIN_PREDICATES && SRL_GOLD_PREDICATES) "dTr"
      else if (SRL_TRAIN_ARG_IDENTIFIERS && !SRL_GOLD_PREDICATES) "eTr"
      else if (SRL_TRAIN_ARG_TYPE && !SRL_GOLD_PREDICATES) "fTr"
      else ""
    else if (TRAINING_MODE.equals("pipeline")) "pTr"
    else if (TRAINING_MODE.equals("joint")) "jTr"
    else if (TRAINING_MODE.equals("jointLoss")) "lTr"
    else ""
  }

  val startTime = System.currentTimeMillis()
  logger.info("population starts.")

  // Here, the data is loaded into the graph
  val srlDataModelObject = PopulateSRLDataModel(testOnly = TEST_MODE, SRL_GOLD_PREDICATES, SRL_GOLD_ARG_BOUNDARIES)

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
  import SRLscalaConfigurator._
  // TRAINING
  if (!TEST_MODE) {
    expName match {

      case "aTr" =>
        argumentTypeLearner.modelDir = modelDir + expName
        argumentTypeLearner.learn(30, relations.getTrainingInstances)
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
        val outputFile = modelDir + SRL_OUTPUT_FILE
        logger.info("Global training... ")
        JointTrainSparseNetwork(sentences, argTypeConstraintClassifier :: Nil, 30, init = true)
        argumentTypeLearner.save()
        argTypeConstraintClassifier.test(relations.getTestingInstances, outputFile, 200, exclude = "candidate")

      case "lTr" =>
        argumentTypeLearner.modelDir = modelDir + expName
        val outputFile = modelDir + SRL_OUTPUT_FILE
        logger.info("Global training using loss augmented inference... ")
        JointTrainSparseNetwork(sentences, argTypeConstraintClassifier :: Nil, 30, init = true, lossAugmented = true)
        argumentTypeLearner.save()
        argTypeConstraintClassifier.test(relations.getTestingInstances, outputFile, 200, exclude = "candidate")
    }

  }

  // TESTING
  if (TEST_MODE) {
    (SRL_TEST_PIPELINE, SRL_TEST_CONSTRAINTS) match {

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

