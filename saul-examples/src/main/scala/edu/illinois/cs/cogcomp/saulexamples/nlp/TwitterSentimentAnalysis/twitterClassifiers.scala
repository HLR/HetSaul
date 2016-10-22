/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis

import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures.Tweet
import twitterDataModel._
/** Created by guest on 10/2/16.
  */
object twitterClassifiers {

  object sentimentClassifier extends Learnable[Tweet](tweet) {
    def label = Label
    override def feature = using(WordFeatures, BigramFeatures)
    override lazy val classifier = new SparseNetworkLearner()
  }

}

