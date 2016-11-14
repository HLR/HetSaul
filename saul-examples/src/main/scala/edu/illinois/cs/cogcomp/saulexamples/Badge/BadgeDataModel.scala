/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.Badge

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeClassifiers.BadgeClassifier

/** Created by Parisa on 9/13/16.
  */
object BadgeDataModel extends DataModel {

  val badge = node[String]

  val BadgeFeature1 = property(badge) {
    x: String =>
      {
        val tokens = x.split(" ")
        tokens(1).charAt(1).toString
      }
  }

  val BadgeLabel = property(badge)("true", "false") {
    x: String =>
      {
        val tokens = x.split(" ")
        if (tokens(0).equals("+"))
          "true"
        else
          "false"
      }
  }

  val BadgeOppositLabel = property(badge)("true", "false") {
    x: String =>
      {
        val tokens = x.split(" ")
        if (tokens(0).equals("+"))
          "false"
        else
          "true"
      }
  }

  val BadgePrediction = property(badge)("true", "false") { x: String => BadgeClassifier(x) }
}
