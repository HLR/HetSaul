package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Constituent, Relation}
import edu.illinois.cs.cogcomp.core.datastructures.{IntPair, ViewNames}
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
    val predicateCandidates = tokens()
      .filter((x: Constituent) => (tokens(x) prop posTag).head.startsWith("VB"))
      .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
    // Remove the true predicates from the list of candidates (since they have a different label)
    val negativePredicateCandidates = predicates(predicateCandidates)
      .filterNot(cand => (predicates() prop address).contains((predicates(cand) prop address).head))

    predicates.populate(negativePredicateCandidates)

    predicateClassifier.learn(2)
    predicateClassifier.crossValidation(3)
    predicateSenseClassifier.learn(5)

    // Exclude argument candidates (trees) that contain a predicate
    // First we need to get the list of predicates that are relevant to each tree
    val treePredicates = trees() ~> -sentencesToTrees ~> sentencesToRelations ~> relationsToPredicates
    // Now we need to filter the trees based on whether they contain a predicate
    val treeCandidates = trees().filterNot(tree => {
      treePredicates.exists(pred => {
        // Containment relationship
          val treeSpan: IntPair = tree.getLabel.getSpan
          val predSpan: IntPair = pred.getSpan
          treeSpan.getFirst <= predSpan.getFirst && treeSpan.getSecond >= predSpan.getSecond
        })
    })
    // Finally we need to convert the trees to argument phrases
    val argumentCandidates = treeCandidates.map(tree => tree.getLabel)
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