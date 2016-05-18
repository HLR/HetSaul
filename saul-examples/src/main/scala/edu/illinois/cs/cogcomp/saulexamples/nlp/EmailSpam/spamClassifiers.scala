package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.lbjava.learn.SupportVectorMachine
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.SpamDataModel._

object SpamClassifiers {
  object SpamClassifier extends Learnable[Document](docs) {
    def label = spamLabel
    override lazy val classifier = new SupportVectorMachine()
    override def feature = using(wordFeature)
  }

  object SpamClassifierWithCache extends Learnable[Document](docs) {
    def label = spamLabel
    override lazy val classifier = new SupportVectorMachine()
    override def feature = using(wordFeature)
    override val useCache = true
  }

  object DeserializedSpamClassifier extends Learnable[Document](docs) {
    def label = spamLabel
    override lazy val classifier = new SupportVectorMachine()
    override def feature = using(wordFeature)
  }
}
