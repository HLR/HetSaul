package edu.illinois.cs.cogcomp.saulexamples.nlp.SRL

import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator
import edu.illinois.cs.cogcomp.saulexamples.data.SRLDataReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.SRL.SRLClassifiers.predicateClassifier

import scala.collection.JavaConversions._

/** The main object for the SRL project. Reads in the data and initiates the training.
  *
  * @author Christos Christodoulopoulos
  */
object SRLApplication {
  val SRLconfig = "./saul-examples/config/SRL.properties"
  // TODO set these

  def main(args: Array[String]) {
    import SRLDataModel._
    val rm = new ExamplesConfigurator().getDefaultConfig
    val reader = new SRLDataReader(rm.getString(ExamplesConfigurator.TREEBANK_HOME.getFirst), rm.getString(ExamplesConfigurator.PROPBANK_HOME.getFirst))
    reader.readData()
    textAnnotation.populate(reader.textAnnotations.toList)
    predicateClassifier.learn(2)

  }
}