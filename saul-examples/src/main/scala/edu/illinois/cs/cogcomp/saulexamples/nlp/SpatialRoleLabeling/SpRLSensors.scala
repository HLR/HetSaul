/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation, Sentence }

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/** Created by taher on 7/28/16.
  */
object SpRLSensors {

  // helper methods
  def isCandidate(token: Constituent): Boolean = {

    if (Dictionaries.isPreposition(token.toString))
      return true

    val pos = SpRLDataModel.posTag(token)
    if (pos == "IN" || pos == "TO")
      return true

    return false
  }
}
