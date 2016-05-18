package edu.illinois.cs.cogcomp.saulexamples.nlp.NamedEntityRecognition

import edu.illinois.cs.cogcomp.saulexamples.nlp.NamedEntityRecognition.NERClassifiers.NERClassifier
import scala.collection.JavaConversions._
import edu.illinois.cs.cogcomp.saulexamples.NER.NERDataReader
import NERDataModel._
/** Created by Parisa on 5/17/16.
  */
object NERApp {
  val Reader = new NERDataReader("", "", "")
  val allWords = Reader.readData().map(x => Reader.candidateGenerator(x)).flatten
  word.populate(allWords)
  NERClassifier.learn(10)
  NERClassifier.test()
}
