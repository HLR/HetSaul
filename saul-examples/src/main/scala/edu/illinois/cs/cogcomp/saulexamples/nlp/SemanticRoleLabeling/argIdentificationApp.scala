package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation
import edu.illinois.cs.cogcomp.saul.classifier.JointTrainSparseNetwork
import edu.illinois.cs.cogcomp.saulexamples.data.XuPalmerCandidateGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.{ argumentTypeLearner, argumentXuIdentifierGivenApredicate }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLConstraintClassifiers.{ arg_IdentifyConstraintClassifier, arg_TypeConstraintClassifier }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._

import scala.collection.JavaConversions._

object argIdentificationApp extends App {
  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)

  val t = new XuPalmerCandidateGenerator(null)

  val XuPalmerCandidateArgsTraining = predicates.getTrainingInstances.flatMap(
    (x =>
      {
        val p = t.generateSaulCandidates(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head)
        p.map(y => new Relation("candidate", x.cloneForNewView(x.getViewName), y.cloneForNewView(y.getViewName), 0.0))
      })
  )
  val XuPalmerCandidateArgsTesting = predicates.getTestingInstances.flatMap(

    (x =>
      {
        val p = t.generateSaulCandidates(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head)
        p.map(y => new Relation("candidate", x.cloneForNewView(x.getViewName), y.cloneForNewView(y.getViewName), 0.0))
      })

  )
  val a = relations() ~> relationsToArguments prop address
  val b = relations() ~> relationsToPredicates prop address

  val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))
  val negativePalmerTrainCandidates = XuPalmerCandidateArgsTraining.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))

  relations.populate(negativePalmerTrainCandidates)
  relations.populate(negativePalmerTestCandidates, false)

  println("all relations number after population:" + SRLDataModel.relations().size)

  argumentXuIdentifierGivenApredicate.learn(80)

  print("argument identifier test results:")

  argumentXuIdentifierGivenApredicate.test()

  argumentTypeLearner.learn(80)

  print("argument classifier test results:")

  argumentTypeLearner.test()

  print("argument classifier and classifier L+I model test results:")

  arg_TypeConstraintClassifier.test()

  print("argument identifier and classifier L+I model test results:")

  arg_IdentifyConstraintClassifier.test()

  JointTrainSparseNetwork.train(SRLDataModel, arg_TypeConstraintClassifier :: arg_IdentifyConstraintClassifier :: Nil, 40)

  print("argument classifier and classifier IBT model test results:")

  arg_TypeConstraintClassifier.test()

  print("argument identifier and classifier IBT model test results:")

  arg_IdentifyConstraintClassifier.test()

  //  argumentTypeLearner.learn(3)
  //  println("Training finished")
  //  println("Test multi class with 3 iterations:")
  //  argumentTypeLearner.test()
  //  println("Test constrained multi class:")
  //
  //  argTypeConstraintClassifier.test()
  //
  //  // argumentTypeLearner.forget()
  //
  //  JoinTrainSparseNetwork.train[TextAnnotation](SRLDataModel, argTypeConstraintClassifier:: Nil,50)
  //
  //  println("Test joint learner joint prediction:")
  //
  //  argTypeConstraintClassifier.test()
  //
  //  println("Test joint learner independent prediction:")
  //
  //  argumentTypeLearner.test()
}
