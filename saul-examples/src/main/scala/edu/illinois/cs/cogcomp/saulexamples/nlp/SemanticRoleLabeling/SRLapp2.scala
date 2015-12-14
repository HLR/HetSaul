package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.{IntPair, ViewNames}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.argumentClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._

import scala.collection.JavaConversions._
/**
 * Created by Parisa on 12/11/15.
 */
object SRLapp2 extends App{

  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)

  val treeCandidates = trees().flatMap {
    tree =>
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
  val argumentCandidates = treeCandidates.map(tree => tree.getLabel.cloneForNewViewWithDestinationLabel(ViewNames.SRL_VERB,""))
  // We also need to remove the true arguments
  val negativeArgumentCandidates = arguments(argumentCandidates)
    .filterNot(cand => (arguments() prop address).contains((arguments(cand) prop address).head))

  arguments.populate(negativeArgumentCandidates)
  println(arguments().size)
 // argumentClassifier.learn(4)
  argumentClassifier.crossValidation(3)

}
