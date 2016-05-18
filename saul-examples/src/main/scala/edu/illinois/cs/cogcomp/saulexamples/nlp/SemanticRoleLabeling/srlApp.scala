package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import java.io.File

import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.classifier.JointTrainSparseNetwork
import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlConstraintClassifiers.argTypeConstraintClassifier
import org.slf4j.{ Logger, LoggerFactory }

object srlApp extends App {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val properties: ResourceManager = {
    // Load the default properties if the user hasn't entered a file as an argument
    if (args.length == 0) {
      logger.info("Loading default configuration parameters")
      new ExamplesConfigurator().getDefaultConfig
    } else {
      logger.info("Loading parameters from {}", args(0))
      new ExamplesConfigurator().getConfig(new ResourceManager(args(0)))
    }
  }
  val modelDir = properties.getString(ExamplesConfigurator.MODELS_DIR) +
    File.separator + properties.getString(ExamplesConfigurator.SRL_MODEL_DIR) + File.separator
  val srlPredictionsFile = properties.getString(ExamplesConfigurator.SRL_OUTPUT_FILE)

  val trainingMode = properties.getString(ExamplesConfigurator.TRAINING_MODE)

  // Testing parameters
  val testWithConstraints = properties.getBoolean(ExamplesConfigurator.SRL_TEST_CONSTRAINTS)
  val testWithPipeline = properties.getBoolean(ExamplesConfigurator.SRL_TEST_PIPELINE)

  val useGoldPredicate = properties.getBoolean(ExamplesConfigurator.SRL_GOLD_PREDICATES)
  val useGoldBoundaries = properties.getBoolean(ExamplesConfigurator.SRL_GOLD_ARG_BOUNDARIES)

  val srlGraphs = populatemultiGraphwithSRLData(testOnly = false, useGoldPredicate, useGoldBoundaries)

  import srlGraphs._

  logger.info("all relations number after population:" + srlGraphs.relations().size)
  logger.info("all sentences number after population:" + srlGraphs.sentences().size)
  logger.info("all predicates number after population:" + srlGraphs.predicates().size)
  logger.info("all arguments number after population:" + srlGraphs.arguments().size)
  logger.info("all tokens number after population:" + srlGraphs.tokens().size)

  val argumentTypeLearner_lc = argumentTypeLearner.getClassNameForClassifier + ".lc"
  val argumentTypeLearner_lex = argumentTypeLearner.getClassNameForClassifier + ".lex"
  val argumentIdentifier_lc = argumentXuIdentifierGivenApredicate.getClassNameForClassifier + ".lc"
  val argumentIdentifier_lex = argumentXuIdentifierGivenApredicate.getClassNameForClassifier + ".lex"

  // TRAINING
  if (trainingMode.equals("joint")) {
    //argumentTypeLearner.setModelDir(modelDir) todo where is the set model?
    val outputFile = modelDir + srlPredictionsFile
    logger.info("Join train:... ")
    for (i <- 0 until 20) {
      JointTrainSparseNetwork(srlGraphs, argTypeConstraintClassifier :: Nil, 5)
      logger.info("test join train on testing data after " + (i * 5) + " iterations:... ")
      argumentTypeLearner.save()
      argTypeConstraintClassifier.test(srlGraphs.relations.getTestingInstances, outputFile, 200, exclude = "candidate")
      logger.info("test join train on training data after " + (i * 5) + " iterations:... ")
      argTypeConstraintClassifier.test(srlGraphs.relations.getTrainingInstances, outputFile, 200, exclude = "candidate")
    }
  } else if (trainingMode.equals("pipeline")) {
    val modelLCb = modelDir + argumentIdentifier_lc
    val modelLEXb = modelDir + argumentIdentifier_lex
    argumentXuIdentifierGivenApredicate.load(modelLCb, modelLEXb)
    val training = relations.getTrainingInstances.filter(x => argumentXuIdentifierGivenApredicate(x).equals("true"))
    argumentTypeLearner.setModelDir(modelDir)
    argumentTypeLearner.learn(100, training)
    logger.info("Test without pipeline:")
    argumentTypeLearner.test(exclude = "candidate")
    argumentTypeLearner.save()
    logger.info("Test with pipeline:")
    argumentTypeLearner.test(
      prediction = typeArgumentPipeGivenGoldPredicate,
      groundTruth = argumentLabelGold, exclude = "candidate"
    )
  }

  // TESTING
  if (testWithPipeline) {
    //load the argument identifier model,
    val modelLCb = modelDir + argumentIdentifier_lc
    val modelLEXb = modelDir + argumentIdentifier_lex
    argumentXuIdentifierGivenApredicate.load(modelLCb, modelLEXb)
    argumentXuIdentifierGivenApredicate.test()
    val modelLCa = modelDir + argumentTypeLearner_lc
    val modelLEXa = modelDir + argumentTypeLearner_lex
    argumentTypeLearner.load(modelLCa, modelLEXa)

    if (testWithConstraints)
      argumentTypeLearner.test(
        prediction = typeArgumentPipeGivenGoldPredicateConstrained,
        groundTruth = argumentLabelGold, exclude = "candidate"
      )
    else
      argumentTypeLearner.test(
        prediction = typeArgumentPipeGivenGoldPredicate,
        groundTruth = argumentLabelGold, exclude = "candidate"
      )
  } else {
    if (testWithConstraints) {
      val modelLCc = modelDir + argumentTypeLearner_lc
      val modelLEXc = modelDir + argumentTypeLearner_lex
      argumentTypeLearner.load(modelLCc, modelLEXc)
      argTypeConstraintClassifier.test(outputGranularity = 100, exclude = "candidate")
    } else {
      val modelLCa = modelDir + argumentTypeLearner_lc
      val modelLEXa = modelDir + argumentTypeLearner_lex
      argumentTypeLearner.load(modelLCa, modelLEXa)
      argumentTypeLearner.test(exclude = "candidate")
      // This is to print to file for standard CoNLL evaluation -commented out for later
      //    val goldOutFile = "srl.gold"
      //    val goldWriter = new PrintWriter(new File(goldOutFile))
      //    val predOutFile = "srl.predicted"
      //    val predWriter = new PrintWriter(new File(predOutFile))
      //     argumentTypeLearner.test(prediction= typeArgumentPrediction, groundTruth =  argumentLabelGold, exclude="candidate")
      //    val predictedViews = predArgViewGenerator.toPredArgList(srlGraphs, typeArgumentPrediction)
      //    val goldViews = predArgViewGenerator.toPredArgList(srlGraphs, argumentLabelGold)
      //
      //    predictedViews.foreach(pav => CoNLLFormatWriter.printPredicateArgumentView(pav, predWriter))
      //    goldViews.foreach(pav => CoNLLFormatWriter.printPredicateArgumentView(pav, goldWriter))
      //    predWriter.close()
      //    goldWriter.close()
    }
  }

}

