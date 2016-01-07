package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamDataModel._

object spamClassifiers {
  object spamClassifier extends Learnable[Document](spamDataModel) {
    def label = spamLabel
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(wordFeature)
  }

  object spamClassifierWithCache extends Learnable[Document](spamDataModel) {
    def label = spamLabel
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(wordFeature)
    override val useCache = true
  }

  object deserializedSpamClassifier extends Learnable[Document](spamDataModel) {
    def label = spamLabel
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(wordFeature)
  }
}
