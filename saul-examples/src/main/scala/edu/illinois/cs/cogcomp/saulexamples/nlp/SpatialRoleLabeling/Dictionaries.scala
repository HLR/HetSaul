/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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
  var spLexicon = HashSet[String]()

  def isPreposition(word: String): Boolean = {
    val w = if (word == null) "" else word.toLowerCase.trim
    prepositions.contains(w)
  }
}
