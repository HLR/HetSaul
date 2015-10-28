package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator
import edu.illinois.cs.cogcomp.saulexamples.data.SRLDataReader
import SRLClassifiers.predicateClassifier
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
    textAnnotation.populate(reader.textAnnotations.toList)
    predicateClassifier.learn(2)
  }
}