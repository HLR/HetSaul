/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.math._

object RandomDataModel extends DataModel {
  val randomNode = node[String]
  val th = Pi / 3
  val c = cos(th)
  val s = sin(th)
  val r = scala.util.Random
  r.setSeed(0)

  val randomLabel = property(randomNode, cache = true) {
    x: String =>
      (2 * (if (r.nextGaussian() > 0) 1 else 0) - 1).toString
  }

  val randomProperty = property(randomNode, cache = true) {
    x: String =>
      val p = List(2 * r.nextGaussian(), 0.5 * r.nextGaussian())
      val p1new = p(1) + randomLabel(x).toDouble
      List(c * p1new - s * p(1), c * p1new + s * p(1))
  }
}
