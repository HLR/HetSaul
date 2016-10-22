/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis

import java.util

import com.twitter.hbc.core.endpoint.Location
import edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis.twitterClassifiers.sentimentClassifier
import edu.illinois.cs.cogcomp.saulexamples.twitter.tweet.{ ClassifierMessageHandler, Locations, TwitterClient }

import scala.collection.JavaConversions._

/** Created by parisa on 10/4/16.
  */
object twitterStreamApp extends App {

  // Set up location filters
  val locations: List[Location] = util.Arrays.asList(Locations.URBANA_CHAMPAIGN, Locations.New_ORLEANS).toList;
  // Set up search-term filters
  //List<String> terms = Arrays.asList("machine learning", "natural language processing");
  // Set up language filters
  //List<String> languages = Arrays.asList("en", "es");

  val client: TwitterClient = new TwitterClient(null, locations, null);
  sentimentClassifier.load()
  // A separate thread for handling the queue of tweets
  val messageHandler: ClassifierMessageHandler = new ClassifierMessageHandler(client.getMsgQueue(), client.getClient(), sentimentClassifier.classifier);
  val thread: Thread = new Thread(messageHandler);
  thread.start();
}
