package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlConstraintClassifiers.{ arg_Is_TypeConstraintClassifier, arg_IdentifyConstraintClassifier, argTypeConstraintClassifier }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ argumentXuIdentifierGivenApredicate, argumentTypeLearner }

import scala.collection.JavaConversions._
/** Created by Parisa on 12/27/15.
  */
object li_App extends App {

  populateGraphwithGoldSRL(srlDataModel, srlDataModel.sentences)

  val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head))

  val a = relations() ~> relationsToArguments prop address
  val b = relations() ~> relationsToPredicates prop address

  val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))

  relations.populate(negativePalmerTestCandidates, false)

  println("all relations number after population:" + srlDataModel.relations().size)

  argumentTypeLearner.learn(100)
  argumentXuIdentifierGivenApredicate.learn(100)

  print("argument identifier L+I model (join with classifciation) test results:")

  arg_IdentifyConstraintClassifier.test()

  print("argument classifier L+I model (join with classifciation) test results:")

  argTypeConstraintClassifier.test()
  arg_Is_TypeConstraintClassifier.test()

  //TODO add more variations with combination of constraints
}

