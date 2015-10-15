package edu.illinois.cs.cogcomp.saulexamples.nlp.SRL

import edu.illinois.cs.cogcomp.saulexamples.data.SRLDataReader

import scala.collection.JavaConversions._

/** The main object for the SRL project. Reads in the data and initiates the training.
  *
  * @author Christos Christodoulopoulos
  */
object SRLApplication {

  // TODO set these
  val treebankHome: String = ""
  val propbankHome: String = ""

  def main(args: Array[String]) {
    import SRLDataModel._
    val reader = new SRLDataReader(treebankHome, propbankHome)
    reader.readData()

    textAnnotation.populate(reader.textAnnotations.toList)
  }
}