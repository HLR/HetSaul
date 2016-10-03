package edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis

import edu.illinois.cs.cogcomp.saulexamples.twitter.tweet.TweetReader
import org.apache.commons.io.filefilter.FalseFileFilter
import twitterClassifiers._
import twitterDataModel._

import scala.collection.JavaConversions._
/** Created by guest on 10/2/16.
  */
object twitterApp extends App {

  val TrainReader = new TweetReader("data/twitter/train50k.csv.gz")
  val TestReader = new TweetReader("data/twitter/test.csv.gz")
  tweet.populate(TrainReader.tweets.toList)
  tweet.populate(TestReader.tweets.toList, train = false)
  twitterClassifier.learn(10)
  twitterClassifier.test()
}
