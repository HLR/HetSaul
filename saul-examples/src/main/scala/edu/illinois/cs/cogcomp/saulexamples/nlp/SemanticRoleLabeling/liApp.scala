package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlConstraintClassifiers.argTypeConstraintClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._

import scala.collection.JavaConversions._
/** Created by Parisa on 12/27/15.
  */
object liApp extends App {

  val aTr_lc = "modelsRepeating/models_aTr/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lc"
  val aTr_lex = "modelsRepeating/models_aTr/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.argumentTypeLearner$.lex"

  val useGoldPredicate = false

  srlDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  populateGraphwithGoldSRL(srlDataModel, srlDataModel.sentences, testOnly = true)
  if (!useGoldPredicate) {
    val predicateTrainCandidates = tokens.getTrainingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
    val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

    val negativePredicateTrain = predicates(predicateTrainCandidates)
      .filterNot(cand => (predicates() prop address).contains(address(cand)))
    val negativePredicateTest = predicates(predicateTestCandidates)
      .filterNot(cand => (predicates() prop address).contains(address(cand)))

    predicates.populate(negativePredicateTrain)
    predicates.populate(negativePredicateTest, train = false)
  }
  val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))

  val a = relations() ~> relationsToArguments prop address
  val b = relations() ~> relationsToPredicates prop address

  val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => a.contains(address(cand.getTarget)) && b.contains(address(cand.getSource)))

  relations.populate(negativePalmerTestCandidates, train = false)

  println("all relations number after population:" + srlDataModel.relations().size)

  //Load independently trained models

  //  arg_Is_TypeConstraintClassifier.test()

  print("argument identifier L+I model (join with classifciation) test results:")

  // arg_IdentifyConstraintClassifier.test()

  print("argument classifier L+I model (join with classifciation) test results:")

  // arg_Is_TypeConstraintClassifier.test()

  print("argument classifier L+I model considering background knowledge  test results:")

  argumentTypeLearner.load(aTr_lc, aTr_lex)

  argTypeConstraintClassifier.test()

  //TODO add more variations with combination of constraints
}

