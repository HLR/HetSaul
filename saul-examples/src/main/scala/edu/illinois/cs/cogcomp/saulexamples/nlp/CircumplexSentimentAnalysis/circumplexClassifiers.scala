package edu.illinois.cs.cogcomp.saulexamples.nlp.CircumplexSentimentAnalysis

import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.learn.MultilayerPerceptron
import edu.illinois.cs.cogcomp.saulexamples.circumplex.datastructures.Circumplex_Post
import circumplexDataModel._

/**
  * Created by Abiyaz on 7/7/2017.
  */
object circumplexClassifiers {
  object sentimentClassifier extends Learnable[Circumplex_Post](circumplex_post) {
    def label = Label
    override def feature = using(WordFeatures)
    override lazy val classifier = new MultilayerPerceptron();
  }
}
