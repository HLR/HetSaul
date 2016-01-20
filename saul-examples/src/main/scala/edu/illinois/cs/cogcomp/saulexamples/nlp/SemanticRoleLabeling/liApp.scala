package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlConstraintClassifiers.argTypeConstraintClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._
import org.slf4j.{ Logger, LoggerFactory }

import scala.collection.JavaConversions._
/** Created by Parisa on 12/27/15.
  */
object liApp extends App {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val aTr_lc = "modelsRepeating/models_aTr/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lc"
  val aTr_lex = "modelsRepeating/models_aTr/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lex"

  val useGoldPredicate = true
  val useGoldBoundaries = true

  logger.info("Add the token generation sensor to the graph..")
  srlDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  logger.info("populate the gold srl data...")
  populateGraphwithGoldSRL(srlDataModel, srlDataModel.sentences, testOnly = true)
  if (!useGoldPredicate) {

    val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

    val negativePredicateTest = predicates(predicateTestCandidates)
      .filterNot(cand => (predicates() prop address).contains(address(cand)))
    logger.info("populate the negative predicates ...")
    predicates.populate(negativePredicateTest, train = false)
  }
  logger.info("populate the negative arguments:")
  if (!useGoldBoundaries) {
    val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))

    val a = relations() ~> relationsToArguments prop address
    val b = relations() ~> relationsToPredicates prop address

    val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => a.contains(address(cand.getTarget)) && b.contains(address(cand.getSource)))

    relations.populate(negativePalmerTestCandidates, train = false)
  }

  println("all relations number after population:" + srlDataModel.relations().size)

  //Load independently trained models

  //  arg_Is_TypeConstraintClassifier.test()

  // print("argument identifier L+I model (join with classifciation) test results:")

  // arg_IdentifyConstraintClassifier.test()

  // print("argument classifier L+I model (join with classifciation) test results:")

  // arg_Is_TypeConstraintClassifier.test()

  print("argument classifier L+I model considering background knowledge  test results:")

  argumentTypeLearner.load(aTr_lc, aTr_lex)

  argTypeConstraintClassifier.test()
  argumentTypeLearner.test()
  logger.info("finished!")

  //TODO add more variations with combination of constraints
}

