package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator
import edu.illinois.cs.cogcomp.saulexamples.data.SRLDataReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.predicateClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._

import scala.collection.JavaConversions._

/** The main object for the SRL project. Reads in the data and initiates the training. */
object SRLApplication {

  def main(args: Array[String]) {
    val rm = new ExamplesConfigurator().getDefaultConfig
    val reader = new SRLDataReader(
      rm.getString(ExamplesConfigurator.TREEBANK_HOME.getFirst),
      rm.getString(ExamplesConfigurator.PROPBANK_HOME.getFirst)
    )
    reader.readData()
    sentences.populate(reader.textAnnotations.toList)

    //From now on we populate the test collections

    val predicateCandidates = tokens().filter((x: Constituent) => (tokens(x) prop posTag).head.startsWith("VB"))

    predicates.populate(predicateCandidates, train = false)

    predicateClassifier.learn(2)
  }
}