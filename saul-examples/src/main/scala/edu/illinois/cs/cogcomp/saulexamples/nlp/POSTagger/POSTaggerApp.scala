package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.nlp.corpusreaders.PennTreebankPOSReader

import scala.collection.JavaConversions._

object POSTaggerApp {

  def main(args: Array[String]) {
    val trainDataReader = new PennTreebankPOSReader("train")

    trainDataReader.readFile("./data/POSTagger/corpus.test")
    val trainData = trainDataReader.getTextAnnotations

    println(trainData.size())
    println(trainData.head.hasView(ViewNames.POS))
  }
}
