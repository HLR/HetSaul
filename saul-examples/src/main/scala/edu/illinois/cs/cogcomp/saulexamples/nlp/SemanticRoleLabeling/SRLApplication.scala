package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation }
import edu.illinois.cs.cogcomp.core.datastructures.{ IntPair, ViewNames }
import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator
import edu.illinois.cs.cogcomp.saulexamples.data.SRLDataReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._

import scala.collection.JavaConversions._

/** The main object for the SRL project. Reads in the data and initiates the training. */
object SRLApplication {

  def main(args: Array[String]) {
    val rm = new ExamplesConfigurator().getDefaultConfig
    val reader = new SRLDataReader(
      rm.getString(ExamplesConfigurator.TREEBANK_HOME.key),
      rm.getString(ExamplesConfigurator.PROPBANK_HOME.key)
    )
    reader.readData()

    // Here we populate everything
    sentences.populate(reader.textAnnotations.toList)

    // Generate predicate candidates by extracting all verb tokens
    val predicateCandidates = tokens().filter((x: Constituent) => posTag(x).startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
    // Remove the true predicates from the list of candidates (since they have a different label)
    val negativePredicateCandidates = predicates(predicateCandidates)
      .filterNot(cand => (predicates() prop address).contains(address(cand)))

    predicates.populate(negativePredicateCandidates)

    predicateClassifier.learn(2)
    predicateClassifier.crossValidation(3)
    predicateSenseClassifier.learn(5)

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

    arguments.populate(negativeArgumentCandidates)

    argumentClassifier.learn(4)
    argumentClassifier.crossValidation(3)

    val relationCandidates = for { x <- predicates(); y <- arguments() } yield new Relation("candidate", x, y, 0.0)

    relations.populate(relationCandidates, train = false)
    relationClassifier.learn(3)
    relationClassifier.crossValidation(3)
  }
}