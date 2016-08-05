package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import scala.collection.immutable.HashSet

/** Created by taher on 8/4/16.
  */
object Dictionaries {

  val prepositions = HashSet(
    "about", "above", "across", "after", "against", "along", "among", "around", "as", "at", "before",
    "behind", "beneath", "beside", "between", "by", "down", "during", "for", "from", "in", "inside",
    "into", "like", "of", "off", "on", "onto", "over", "round", "through", "to", "towards", "with"
  )

  def isPreposition(word: String): Boolean = {
    val w = if (word == null) "" else word.toLowerCase.trim
    prepositions.contains(w)
  }
}
