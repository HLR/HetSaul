package edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures.Tweet
import edu.illinois.cs.cogcomp.saulexamples.twitter.tweet.TweetReader
import org.apache.commons.io.filefilter.FalseFileFilter
import twitterClassifiers._
import twitterDataModel._

import scala.collection.JavaConversions._
/** Created by guest on 10/2/16.
  */
object SentimentApp extends App {

  val TrainReader = new TweetReader("data/twitter/train50k.csv.gz")
  val TestReader = new TweetReader("data/twitter/test.csv.gz")
  tweet.populate(TrainReader.tweets.toList)
  tweet.populate(TestReader.tweets.toList, train = false)
  //sentimentClassifier.learn(10)
  ClassifierUtils.LoadClassifier(sentimentClassifier)
  sentimentClassifier.classifier.discreteValue(new Tweet("here is my tweet."))
  sentimentClassifier.test()
}
