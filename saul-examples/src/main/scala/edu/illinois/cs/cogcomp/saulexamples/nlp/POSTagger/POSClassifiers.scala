package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.classifier.Learnable

import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSDataModel._

object POSClassifiers {
  /** After POSTaggerKnown and POSTaggerUnknown are trained,
    * this classifier will return the prediction of POSTaggerKnown if
    * the input word was observed during training or of POSTaggerUnknown
    * if it wasn't.
    */
  object POSClassifier extends Learnable[Constituent](POSDataModel) {
    def label = posLabel
    override def algorithm = "SparseNetwork"
  }

  object POSTaggerKnown extends Learnable[Constituent](POSDataModel) {
    def label = posLabel
    override def feature = using(List(wordForm, baselineLabel, labelTwoBefore, labelOneBefore,
      labelOneAfter, labelTwoAfter, L2bL1b, L1bL1a, L1aL2a))
    override def algorithm = "SparseNetwork"
  }

  object POSTaggerUnknown extends Learnable[Constituent](POSDataModel) {
    def label = posLabel
    override def algorithm = "SparseNetwork"
  }
}
