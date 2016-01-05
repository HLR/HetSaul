package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment.predicateClassifier1
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._

import scala.collection.JavaConversions._
/**
 * Created by Parisa on 1/5/16.
 */
object testModelsWorkingSpace extends App{

  SRLDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)

  val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

  val negativePredicateTest = predicates(predicateTestCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))

  predicates.populate(negativePredicateTest, false)
  predicateClassifier1.load("./models/models/isPredicate/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.predicateClassifier$.lc","./models/models/isPredicate/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.predicateClassifier$.lex")
 // predicateClassifier1.load()
  predicateClassifier1.test()

  ////
//  val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head))
//
//  val a = relations() ~> relationsToArguments prop address
//  val b = relations() ~> relationsToPredicates prop address
//
//  val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))
//
//  relations.populate(negativePalmerTestCandidates, false)
//
//
//
// // argumentXuIdentifierGivenApredicate1.test()
//
//  //argumentTypeLearner1.test()

}
