package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation }
import edu.illinois.cs.cogcomp.core.datastructures.{ IntPair, ViewNames }
import edu.illinois.cs.cogcomp.saulexamples.data.XuPalmerCandidateGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.relationClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel_all._

import scala.collection.JavaConversions._
/** Created by Parisa on 12/11/15.
  */
object SRLapp3 extends App {

  populateGraphwithTextAnnotation(SRLDataModel_all, SRLDataModel_all.sentences)
  val t = new XuPalmerCandidateGenerator(null)
  // Generate predicate candidates by extracting all verb tokens
  val predicateCandidates = tokens().filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
  // Remove the true predicates from the list of candidates (since they have a different label)
  val negativePredicateCandidates = predicates(predicateCandidates)
    .filterNot(cand => (predicates() prop address).contains(address(cand)))

  predicates.populate(negativePredicateCandidates)
  // val ee= (sentences(predicates().head.getTextAnnotation)~> sentencesTostringTree).head
  // t.generateSaulCandidates(predicates().head,
  val XuPalmerCandidateArgs = predicates().flatMap(

    (x =>

      {
        val p = t.generateSaulCandidates(x, (sentences(x.getTextAnnotation) ~> sentencesTostringTree).head)
        p.map(y => new Relation("candidate", x.cloneForNewView(x.getViewName), y.cloneForNewView(y.getViewName), 0.0))
      })

  )

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
  println("all arg number:" + SRLDataModel_all.arguments().size)

  println("all relations number after population:" + SRLDataModel_all.relations().size)
  println("arg number after re-population:" + SRLDataModel_all.arguments().size)
  println("arg number after re-population:" + (SRLDataModel_all.relations() ~> relationsToArguments).size)
  println("pred number after re-population:" + SRLDataModel_all.predicates().size)
  println("pred number after re-population:" + (SRLDataModel_all.relations() ~> relationsToPredicates).size)

  //  generate all candidate relations based on candidate arguments and predicates
  //  val relationCandidates4 = for {
  //    x <- predicates()
  //    y <- arguments()
  //    if (!(y.getSpan.getFirst <= x.getSpan.getFirst && y.getSpan.getSecond >= x.getSpan.getSecond))
  //  } yield new Relation("candidate", x.cloneForNewView(x.getViewName), y.cloneForNewView(y.getViewName), 0.0)

  //  println("relation candidates:" + relationCandidates4.size)
  val a = relations() ~> relationsToArguments prop address
  val b = relations() ~> relationsToPredicates prop address

  //  val negativeRelationCandidates = relationCandidates4.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))
  val negativePalmerCandidates = XuPalmerCandidateArgs.filterNot(cand => (a.contains(address(cand.getTarget))) && b.contains(address(cand.getSource)))

  // relations.populate(negativeRelationCandidates)
  //  println("negative relation candidates:" + negativeRelationCandidates.size)
  println("all relations number after population:" + SRLDataModel_all.relations().size)

  relationClassifier.crossValidation(3)

}
