/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.node.Path
import org.scalatest.{ Matchers, FlatSpec }

class PathTest extends FlatSpec with Matchers {

  object TestGraph extends DataModel {
    val n = node[String]
    val prop = property(n, "prefix")((s: String) => s.charAt(1).toString)
    val e = edge(n, n)

    n.populate(Seq("a", "b", "c"))
    e.populateWith((a, b) => Set("a" -> "b", "b" -> "c").apply(a -> b))
  }

  "finding path of a link" should "return the link" in {
    import TestGraph._

    val pathAB = Path.findPath("a", n, "b")
    pathAB.map(_._1) should be(Seq("a" -> "b"))

    val pathBC = Path.findPath("b", n, "c")
    pathBC.map(_._1) should be(Seq("b" -> "c"))
  }

  "finding path of reverse link" should "return nothing" in {
    import TestGraph._

    val pathAB = Path.findPath("b", n, "a")
    pathAB.map(_._1) should be(Seq.empty)

    val pathBC = Path.findPath("c", n, "b")
    pathBC.map(_._1) should be(Seq.empty)
  }

  "finding two hop path" should "return the path" in {
    import TestGraph._

    val pathAB = Path.findPath("a", n, "c")
    pathAB.map(_._1) should be(Seq("a" -> "b", "b" -> "c"))
  }

  "finding two hop path with limitation of 1" should "return nothing" in {
    import TestGraph._

    val pathAB = Path.findPath("a", n, "c", maxLength = 1)
    pathAB.map(_._1) should be(Seq.empty)
  }

  "finding two hop path with limitation of 2" should "return the path" in {
    import TestGraph._

    val pathAB = Path.findPath("a", n, "c", maxLength = 2)
    pathAB.map(_._1) should be(Seq("a" -> "b", "b" -> "c"))
  }
}
