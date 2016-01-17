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
    //    println("Path: " + pathAB.map(_._1).mkString(", "))
    pathAB.map(_._1) should be(Seq("a" -> "b"))

    val pathBC = Path.findPath("b", n, "c")
    //    println("Path: " + pathBC.map(_._1).mkString(", "))
    pathBC.map(_._1) should be(Seq("b" -> "c"))
  }

  "finding path of reverse link" should "return nothing" in {
    import TestGraph._

    val pathAB = Path.findPath("b", n, "a")
    //    println("Path: " + pathAB.map(_._1).mkString(", "))
    pathAB.map(_._1) should be(Seq.empty)

    val pathBC = Path.findPath("c", n, "b")
    //    println("Path: " + pathBC.map(_._1).mkString(", "))
    pathBC.map(_._1) should be(Seq.empty)
  }

  "finding two hop path" should "return the path" in {
    import TestGraph._

    val pathAB = Path.findPath("a", n, "c")
    //    println("Path: " + pathAB.map(_._1).mkString(", "))
    pathAB.map(_._1) should be(Seq("a" -> "b", "b" -> "c"))
  }

  "finding two hop path with limitation of 1" should "return nothing" in {
    import TestGraph._

    val pathAB = Path.findPath("a", n, "c", maxLength = 1)
    //    println("Path: " + pathAB.map(_._1).mkString(", "))
    pathAB.map(_._1) should be(Seq.empty)
  }

  "finding two hop path with limitation of 2" should "return the path" in {
    import TestGraph._

    val pathAB = Path.findPath("a", n, "c", maxLength = 2)
    //    println("Path: " + pathAB.map(_._1).mkString(", "))
    pathAB.map(_._1) should be(Seq("a" -> "b", "b" -> "c"))
  }
}
