package edu.illinois.cs.cogcomp.saulexamples.nlp.NamedEntityRecognition

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.classifier.{ Learnable, SparseNetworkLBP }
import edu.illinois.cs.cogcomp.saulexamples.nlp.NamedEntityRecognition.NERDataModel._
/** Created by Parisa on 5/17/16.
  */
object NERClassifiers {

  object NERClassifier extends Learnable[Constituent](word) {
    def label = NERLabel
    override def feature = using(surface)
    override lazy val classifier = new SparseNetworkLBP
  }
}

