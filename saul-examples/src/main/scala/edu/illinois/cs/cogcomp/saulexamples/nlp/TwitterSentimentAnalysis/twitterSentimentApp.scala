package edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis

import java.util

import com.twitter.hbc.core.endpoint.Location
import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis.twitterClassifiers.sentimentClassifier
import edu.illinois.cs.cogcomp.saulexamples.twitter.tweet.{ ClassifierMessageHandler, Locations, TwitterClient }

import scala.collection.JavaConversions._

/** Created by parisa on 10/4/16.
  */
object twitterSentimentApp extends App {

  // Set up location filters
  val locations: List[Location] = util.Arrays.asList(Locations.URBANA_CHAMPAIGN, Locations.New_ORLEANS).toList;
  // Set up search-term filters
  //List<String> terms = Arrays.asList("machine learning", "natural language processing");
  // Set up language filters
  //List<String> languages = Arrays.asList("en", "es");

  val client: TwitterClient = new TwitterClient(null, locations, null);
  ClassifierUtils.LoadClassifier(sentimentClassifier)
  // A separate thread for handling the queue of tweets
  val messageHandler: ClassifierMessageHandler = new ClassifierMessageHandler(client.getMsgQueue(), client.getClient(), sentimentClassifier.classifier);
  val thread: Thread = new Thread(messageHandler);
  thread.start();
}
