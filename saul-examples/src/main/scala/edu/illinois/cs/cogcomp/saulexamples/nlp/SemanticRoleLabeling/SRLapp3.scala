package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation }
import edu.illinois.cs.cogcomp.core.datastructures.{ IntPair, ViewNames }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.relationClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._

import scala.collection.JavaConversions._
/** Created by Parisa on 12/11/15.
  */
object SRLapp3 extends App {

  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)
  println("arg number from ground truth:" + SRLDataModel.arguments().size)
  println("predicate number from ground truth:" + SRLDataModel.predicates().size)
  println("relation number from ground truth:" + SRLDataModel.relations().size)

  // Generate predicate candidates by extracting all verb tokens
  val predicateCandidates = tokens().filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
  // Remove the true predicates from the list of candidates (since they have a different label)
  val negativePredicateCandidates = predicates(predicateCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))

  predicates.populate(negativePredicateCandidates)
  println("negative predicate candidates:" + negativePredicateCandidates.size)
  println("all predicates:" + SRLDataModel.predicates().size)

  // Exclude argument candidates (trees) that contain predicates
  val treeCandidates = trees().flatMap { tree =>
    val subtrees = SRLSensors.getSubtreeArguments(List(tree))
    // First we need to get the list of predicates that are relevant to each tree
    val treePredicates = trees(tree) ~> -sentencesToTrees ~> sentencesToRelations ~> relationsToPredicates
    // Now we need to filter the trees based on whether they contain all predicates of the sentence
    subtrees.filterNot { subtree =>
      treePredicates.forall(pred => {
        // Containment relationship
        val treeSpan: IntPair = subtree.getLabel.getSpan
        val predSpan: IntPair = pred.getSpan
        treeSpan.getFirst <= predSpan.getFirst && treeSpan.getSecond >= predSpan.getSecond
      }) || treePredicates.exists(pred => pred.getSpan.equals(subtree.getLabel.getSpan))
    }
  }
  // Finally we need to convert the trees to argument phrases
  val argumentCandidates = treeCandidates.map(tree => tree.getLabel.cloneForNewView(ViewNames.SRL_VERB))
  // We also need to remove the true arguments
  val negativeArgumentCandidates = arguments(argumentCandidates)
    .filterNot(cand => (arguments() prop address).contains((arguments(cand) prop address).head))

  println("negative arg candidates:" + negativeArgumentCandidates.size)
  arguments.populate(negativeArgumentCandidates)
  println("all arg number:" + SRLDataModel.arguments().size)

  //  generate all candidate relations based on candidate arguments and predicates
  val relationCandidates2 = for {
    x <- predicates()
    y <- arguments()
    if !(y.getSpan.getFirst <= x.getSpan.getFirst && y.getSpan.getSecond >= x.getSpan.getSecond)
  } yield new Relation("candidate", x, y, 0.0)

  println("relation candidates:" + relationCandidates2.size)

  relations.populate(relationCandidates2)
  println("all relations number after population:" + SRLDataModel.relations().size)
  println("arg number after re-population:" + SRLDataModel.arguments().size)
  println("arg number after re-population:" + (SRLDataModel.relations() ~> relationsToArguments).size)
  println("pred number after re-population:" + SRLDataModel.predicates().size)
  println("pred number after re-population:" + (SRLDataModel.relations() ~> relationsToPredicates).size)

  //println("reduced by =", (relationCandidates.toList.size - relationCandidates2.toList.size))

  // filter the positive relations
  val positiveRelationCandidates = relations(relationCandidates2).
    filter(cand => ((relations() ~> relationsToArguments prop address).contains(relations(cand) ~> relationsToArguments prop address)) &&
      ((relations() ~> relationsToPredicates prop address).contains(relations(cand) ~> relationsToPredicates prop address)))
  println("positive relation candidates:" + positiveRelationCandidates.size)
  relations.un_populate(positiveRelationCandidates, train = false)
  println("all relations number after un_population:" + SRLDataModel.relations().size)

  relationClassifier.crossValidation(3)

}
