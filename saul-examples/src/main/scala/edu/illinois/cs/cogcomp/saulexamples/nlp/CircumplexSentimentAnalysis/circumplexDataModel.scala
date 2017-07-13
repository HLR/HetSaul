package edu.illinois.cs.cogcomp.saulexamples.nlp.CircumplexSentimentAnalysis

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.circumplex.datastructures.Circumplex_Post
import scala.collection.JavaConversions._

/** Created by Abiyaz on 7/7/2017.
  */
object circumplexDataModel extends DataModel {
  val circumplex_post = node[Circumplex_Post]

  val WordFeatures = property(circumplex_post) {
    x: Circumplex_Post =>
      val a = x.getWords.toList
      a
  }

  val BigramFeatures = property(circumplex_post) {
    x: Circumplex_Post => x.getWords.toList.sliding(2).map(_.mkString("-")).toList
  }

  val Valence = property(circumplex_post) {
    x: Circumplex_Post => x.getValence;
  }

  val Arousal = property(circumplex_post) {
    x: Circumplex_Post => x.getArousal;
  }

  /*var Label = property(circumplex_post) {
    x: Circumplex_Post => x.getValence :: x.getArousal :: Nil;
  }*/

  var Label = property(circumplex_post) {
    x: Circumplex_Post => x.getValence :: x.getArousal :: Nil;
  }
}
