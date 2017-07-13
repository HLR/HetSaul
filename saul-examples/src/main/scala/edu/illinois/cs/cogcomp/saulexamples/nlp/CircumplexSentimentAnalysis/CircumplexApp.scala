/**
  */
package edu.illinois.cs.cogcomp.saulexamples.circumplex

import edu.illinois.cs.cogcomp.saulexamples.nlp.CircumplexSentimentAnalysis.circumplexClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.CircumplexSentimentAnalysis.circumplexDataModel._
import edu.illinois.cs.cogcomp.saulexamples.circumplex.facebook.CircumplexReader

import scala.collection.JavaConversions._

object CircumplexApp extends App {

  val Reader = new CircumplexReader("./data/circumplex/dataset-fb-valence-arousal-anon.csv.gz")

  circumplex_post.populate(Reader.posts.toList.slice(0, Reader.posts.toList.size() - 100));
  circumplex_post.populate(Reader.posts.toList.slice(Reader.posts.toList.size() - 100, Reader.posts.toList.size()), train = false)
  sentimentClassifier.learn(10)
  sentimentClassifier.testContinuous();
  sentimentClassifier.save()
}
