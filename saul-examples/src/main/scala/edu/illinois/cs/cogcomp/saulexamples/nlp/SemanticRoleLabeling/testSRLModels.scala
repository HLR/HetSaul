package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ argumentXuIdentifierGivenApredicate, predicateClassifier }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlSensors._
import scala.collection.JavaConversions._
/** Created by Parisa on 1/5/16.
  */
object testSRLModels extends App {
  var modelsPath = "./models/modelsFinal/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment"

  srlDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  populateGraphwithGoldSRL(srlDataModel, srlDataModel.sentences, testOnly = true)

  val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

  val negativePredicateTest = predicates(predicateTestCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))

  predicates.populate(negativePredicateTest, train = false)
  predicateClassifier.load(
    modelsPath + ".predicateClassifier1$.lc",
    modelsPath + ".predicateClassifier1$.lex"
  )
  predicateClassifier.test()

  val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))

  val a = relations() ~> relationsToArguments prop address

  val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => a.contains(address(cand.getTarget)))

  relations.populate(negativePalmerTestCandidates, train = false)

  argumentXuIdentifierGivenApredicate.load(
    modelsPath + ".argumentXuIdentifierGivenApredicate1$.lc",
    modelsPath + ".argumentXuIdentifierGivenApredicate1$.lex"
  )
  argumentXuIdentifierGivenApredicate.test()

  // moved these from pipe to here
  //    if (trainArgIdWithCandidates || trainArgTypeWithCandidates) {
  //      println("Pipeline argument identification")
  //      evaluation.Test(isArgumentXuGold, isArgumentPipePrediction, relations)
  //      println("Pipeline argument classification")
  //      evaluation.Test(argumentLabelGold, typeArgumentPipePrediction, relations)
  //    }
  //    if (trainArgTypeWithGold) {
  //      println("Direct argument identification")
  //      evaluation.Test(isArgumentXuGold, isArgumentPrediction, relations)
  //      println("Direct argument classification")
  //      evaluation.Test(argumentLabelGold, typeArgumentPrediction, relations)
  //    }

  //TODO working space for laoding more and more trained models and test

}
