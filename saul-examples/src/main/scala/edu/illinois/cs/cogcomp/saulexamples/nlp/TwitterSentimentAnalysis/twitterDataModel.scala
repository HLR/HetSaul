/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures.Tweet
import scala.collection.JavaConversions._
/** Created by guest on 10/2/16.
  */
object twitterDataModel extends DataModel {

  val tweet = node[Tweet]

  val WordFeatures = property(tweet) {
    x: Tweet =>
      val a = x.getWords.toList
      a
  }

  val BigramFeatures = property(tweet) {
    x: Tweet => x.getWords.toList.sliding(2).map(_.mkString("-")).toList
  }

  val Label = property(tweet) {
    x: Tweet => x.getSentimentLabel
  }
}
