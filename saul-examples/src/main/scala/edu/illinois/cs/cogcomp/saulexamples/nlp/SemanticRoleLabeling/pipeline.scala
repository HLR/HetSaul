package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.{ argumentXuIdentifierGivenApredicate, predicateClassifier }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors._

import scala.collection.JavaConversions._

/** Created by Parisa on 12/11/15.
  */
object pipeline extends App {

  SRLDataModel.sentencesToTokens.addSensor(textAnnotationToTokens _)
  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)

  // Generate predicate candidates by extracting all verb tokens
  val predicateTrainCandidates = tokens.getTrainingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

  val predicateTestCandidates = tokens.getTestingInstances.filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))

  // Remove the true predicates from the list of candidates (since they have a different label)
  val negativePredicateTrain = predicates(predicateTrainCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))

  val negativePredicateTest = predicates(predicateTestCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))

  predicates.populate(negativePredicateTrain)
  predicates.populate(negativePredicateTest, false)
  predicateClassifier.learn(5)
  //predicateClassifier.save()
  //predicateClassifier.load()
  predicateClassifier.test()

  print("lable:" + predicateClassifier.classifier.classify(predicates().head))
  val predicate_pipe = predicates().filter(x => predicateClassifier.classifier.discreteValue(x).equals("true")).flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head))
  argumentXuIdentifierGivenApredicate.classifier.discreteValue(predicate_pipe)

  // predicates.getTrainingInstances.filter(x => predicateClassifier(x))

  print("finish")
}
