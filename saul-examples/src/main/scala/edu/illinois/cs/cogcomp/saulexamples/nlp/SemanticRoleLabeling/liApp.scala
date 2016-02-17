package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlConstraintClassifiers.argTypeConstraintClassifier
import org.slf4j.{ Logger, LoggerFactory }
/** Created by Parisa on 12/27/15.
  */
object liApp extends App {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  private val rootModelDir: String = "./models/models_cTr_Chris/"
  val aTr_lc = rootModelDir + "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lc"
  val aTr_lex = rootModelDir + "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lex"
  val aTr_pred = rootModelDir + "classifier-predictions.txt"

  val useGoldPredicate = true
  val useGoldBoundaries = false
  val srlGraphs = populatemultiGraphwithSRLData(true, useGoldPredicate, useGoldBoundaries)

  logger.info("all relations number after population:" + srlGraphs.relations().size)
  logger.info("all sentences number after population:" + srlGraphs.sentences().size)
  logger.info("all predicates number after population:" + srlGraphs.predicates().size)
  logger.info("all arguments number after population:" + srlGraphs.arguments().size)
  logger.info("all tokens number after population:" + srlGraphs.tokens().size)
  //Load independently trained models

  //  arg_Is_TypeConstraintClassifier.test()

  // print("argument identifier L+I model (join with classifciation) test results:")

  // arg_IdentifyConstraintClassifier.test()

  // print("argument classifier L+I model (join with classifciation) test results:")

  // arg_Is_TypeConstraintClassifier.test()

  //print("argument classifier L+I model considering background knowledge  test results:")

  argumentTypeLearner.load(aTr_lc, aTr_lex)

  //evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations.getTestingInstances)

  //test(testData: Iterable[T], prediction: Property[T], groundTruth: Property[T])
  logger.info("Join prediction: ")

  argTypeConstraintClassifier.test(srlGraphs.relations.getTestingInstances.slice(1,20), aTr_pred, 100, exclude = "candidate") //(aTr_pred, 100)

  logger.info("Independent prediction: ")

  argumentTypeLearner.test(exclude = "candidate")
  // logger.info("finished!")

  //TODO add more variations with combination of constraints
}

