package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.lbjava.learn.SparseAveragedPerceptron
import edu.illinois.cs.cogcomp.saul.classifier.{ SparseNetworkLBP, Learnable }
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamDataModel._

object spamClassifiers {
  object spamClassifier extends Learnable[Document](spamDataModel) {
    def label = spamLabel
    override lazy val classifier = new SparseNetworkLBP()
    override def feature = using(wordFeature)
  }

  object spamClassifierWithCache extends Learnable[Document](spamDataModel) {
    def label = spamLabel
    override val classifier = new SparseNetworkLBP()
    override def feature = using(wordFeature)
    override val useCache = true
  }
}
