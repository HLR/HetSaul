package edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis

import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures.Tweet
import twitterDataModel._
/** Created by guest on 10/2/16.
  */
object twitterClassifiers {

  object twitterClassifier extends Learnable[Tweet](tweet) {
    def label = Label
    override def feature = using()
    override lazy val classifier = new SparseNetworkLearner()
  }

}

