/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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
