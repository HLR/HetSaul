package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ TextAnnotation, Constituent, Relation }
import edu.illinois.cs.cogcomp.saul.classifier.JoinTrainSparseNetwork
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.XuPalmerCandidateGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.argumentTypeLearner
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLConstraintClassifiers.argTypeConstraintClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._

import scala.collection.JavaConversions._
/** Created by Parisa on 12/18/15.
  */
object relationAppXuPalmerCandidates extends App {

  def Xucandidates(x: Constituent, sRLDataModel: DataModel): Iterable[Relation] = {
    val p = t.generateSaulCandidates(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head)
    p.map(y => new Relation("candidate", x.cloneForNewView(x.getViewName), y.cloneForNewView(y.getViewName), 0.0))
  }

  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)
  val t = new XuPalmerCandidateGenerator(null)

  //    // Generate predicate candidates by extracting all verb tokens
  //    val predicateCandidates = tokens().filter((x: Constituent) => posTag(x).startsWith("VB"))
  //      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
  //    // Remove the true predicates from the list of candidates (since they have a different label)
  //    val negativePredicateCandidates = predicates(predicateCandidates)
  //      .filterNot(cand => (predicates() prop address).contains(address(cand)))
  //
  //    predicates.populate(negativePredicateCandidates)

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

  //  val negativeRelationCandidates = relationCandidates4.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))
  val negativePalmerTestCandidates = XuPalmerCandidateArgsTesting.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))
  val negativePalmerTrainCandidates = XuPalmerCandidateArgsTraining.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))

  relations.populate(negativePalmerTrainCandidates)
  relations.populate(negativePalmerTestCandidates, false)
  //  println("negative relation candidates:" + negativeRelationCandidates.size)
  println("all relations number after population:" + SRLDataModel.relations().size)

  argumentTypeLearner.learn(3)
  println("Training finished")
  println("Test multi class with 3 iterations:")
  argumentTypeLearner.test()
  println("Test constrained multi class:")

  argTypeConstraintClassifier.test()

  // argumentTypeLearner.forget()

  JoinTrainSparseNetwork.train[TextAnnotation](SRLDataModel, argTypeConstraintClassifier :: Nil, 50)

  println("Test joint learner joint prediction:")

  argTypeConstraintClassifier.test()

  println("Test joint learner independent prediction:")

  argumentTypeLearner.test()
}
