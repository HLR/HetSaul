/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis

import edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis.twitterClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis.twitterDataModel._
import edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures.Tweet
import edu.illinois.cs.cogcomp.saulexamples.twitter.tweet.TweetReader

import scala.collection.JavaConversions._
/** Created by guest on 10/2/16.
  */
object SentimentApp extends App {

  val TrainReader = new TweetReader("data/twitter/train50k.csv.gz")
  val TestReader = new TweetReader("data/twitter/test.csv.gz")
  tweet.populate(TrainReader.tweets.toList)
  tweet.populate(TestReader.tweets.toList, train = false)
  sentimentClassifier.learn(10)
  sentimentClassifier.test()
  sentimentClassifier.classifier.discreteValue(new Tweet("here is my tweet."))
  sentimentClassifier.save()
}
