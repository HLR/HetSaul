package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._
import org.slf4j.{ Logger, LoggerFactory }
/** Created by Parisa on 12/27/15.
  */
object liApp extends App {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  private val rootModelDir: String = "./models/modelsRepeating/models_aTr_Chris/"
  val aTr_lc = rootModelDir + "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lc"
  val aTr_lex = rootModelDir + "edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lex"
  val aTr_pred = rootModelDir + "classifier-predictions.txt"

  val useGoldPredicate = false
  val useGoldBoundaries = false

  logger.info("Add the token generation sensor to the graph..")
  srlDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  logger.info("populate the gold srl data...")
  populateGraphwithGoldSRL(srlDataModel, srlDataModel.sentences, testOnly = true)

  if (!useGoldPredicate) {

    val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
    logger.info("populate the negative predicates ...")
    predicates.populate(predicateTestCandidates, train = false)
  }

  if (!useGoldBoundaries) {
    logger.info("generate the negative arguments:")
    val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))
    sentencesToRelations.addSensor(textAnnotationToRelationMatch _)
    logger.info("populate the negative arguments:")
    relations.populate(XuPalmerCandidateArgsTesting, train = false)
  }

  logger.info("all relations number after population:" + srlDataModel.relations().size)
  logger.info("all sentences number after population:" + srlDataModel.sentences().size)
  logger.info("all predicates number after population:" + srlDataModel.predicates().size)
  logger.info("all arguments number after population:" + srlDataModel.arguments().size)
  logger.info("all tokens number after population:" + srlDataModel.tokens().size)
  //Load independently trained models

  //  arg_Is_TypeConstraintClassifier.test()

  // print("argument identifier L+I model (join with classifciation) test results:")

  // arg_IdentifyConstraintClassifier.test()

  // print("argument classifier L+I model (join with classifciation) test results:")

  // arg_Is_TypeConstraintClassifier.test()

  //print("argument classifier L+I model considering background knowledge  test results:")

  //argumentTypeLearner.load(aTr_lc, aTr_lex)

  //evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations.getTestingInstances)

  //test(testData: Iterable[T], prediction: Property[T], groundTruth: Property[T])

  //argTypeConstraintClassifier.test(aTr_pred, 100)
  //argumentTypeLearner.test(relations.getTestingInstances, argumentLabelGold, typeArgumentPrediction, exclude = "candidate")
  //argumentTypeLearner.test(exclude="candidate")
  // logger.info("finished!")

  //TODO add more variations with combination of constraints
}

