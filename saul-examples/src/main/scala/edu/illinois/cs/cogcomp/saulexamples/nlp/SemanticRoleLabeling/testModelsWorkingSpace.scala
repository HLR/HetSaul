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
object testModelsWorkingSpace extends App {

  srlDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  populateGraphwithGoldSRL(srlDataModel, srlDataModel.sentences)

  val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

  val negativePredicateTest = predicates(predicateTestCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))

  predicates.populate(negativePredicateTest, false)
  predicateClassifier.load("./models/modelsFinal/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment.predicateClassifier1$.lc", "./models/modelsFinal/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment.predicateClassifier1$.lex")
  predicateClassifier.test()

  val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head))

  val a = relations() ~> relationsToArguments prop address
  val b = relations() ~> relationsToPredicates prop address

  val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))

  relations.populate(negativePalmerTestCandidates, false)

  argumentXuIdentifierGivenApredicate.load("./models/modelsFinal/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment.argumentXuIdentifierGivenApredicate1$.lc", "./models/modelsFinal/edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment.argumentXuIdentifierGivenApredicate1$.lex")
  argumentXuIdentifierGivenApredicate.test()

  //TODO working space for laoding more and more trained models and test

  print("finish")

}
