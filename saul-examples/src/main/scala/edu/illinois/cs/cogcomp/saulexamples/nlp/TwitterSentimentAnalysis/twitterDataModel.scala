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
      val a= x.getWords.toList
      a
  }

  val BigramFeatures = property(tweet) {
    x: Tweet => x.getWords.toList.sliding(2).map(_.mkString("-")).toList
  }

  val Label = property(tweet) {
    x: Tweet => x.getSentimentLabel
  }
}
