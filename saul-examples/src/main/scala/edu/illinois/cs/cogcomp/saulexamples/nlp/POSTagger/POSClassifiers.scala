package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, TextAnnotation }
import edu.illinois.cs.cogcomp.saul.classifier.Learnable

import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSDataModel._

object POSClassifiers {
  object POSClassifier extends Learnable[Constituent](POSDataModel) {
    def label = posLabel
    override def algorithm = "SparseNetwork"
  }
}
