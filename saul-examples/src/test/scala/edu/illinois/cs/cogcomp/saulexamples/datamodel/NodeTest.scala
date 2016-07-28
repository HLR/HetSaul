/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{ Matchers, FlatSpec }

class NodeTest extends FlatSpec with Matchers {

  class TestGraph extends DataModel {
    val n1 = node[String]((s: String) => s.toLowerCase)
    val n2 = node[String]((s: String) => s.length)

    val e = edge(n1, n2)
    e.addSensor((s: String) => s.take(4))
  }

  object TestGraph extends TestGraph

  "populating a node with propagation" should "propagate edges" in {
    val src = new TestGraph
    src.n1.populate(Seq("Test"))
    src.n1.getAllInstances.size should be(1)
    src.n2.getAllInstances.size should be(1)
    src.e.forward.index.size should be(1)

    val dest = new TestGraph
    dest.n1.populate(src.n1.getAllInstances)
    dest.n1.getAllInstances.size should be(1)
    dest.n2.getAllInstances.size should be(1)
    dest.e.forward.index.size should be(1)
  }

  "populating a node without propagation" should "not change the neighbors" in {
    val src = new TestGraph
    src.n1.populate(Seq("Test"), populateEdge = false)
    src.n1.getAllInstances.size should be(1)
    src.n2.getAllInstances.size should be(0)
    src.e.forward.index.size should be(0)

    val dest = new TestGraph
    dest.n1.populate(src.n1.getAllInstances, populateEdge = false)
    dest.n1.getAllInstances.size should be(1)
    dest.n2.getAllInstances.size should be(0)
    dest.e.forward.index.size should be(0)
  }

  "adding from model" should "not propagate to edges" in {
    val src = new TestGraph
    src.n1.populate(Seq("Test"), populateEdge = false)
    src.n1.getAllInstances.size should be(1)
    src.n2.getAllInstances.size should be(0)
    src.e.forward.index.size should be(0)

    val dest = new TestGraph
    dest.addFromModel(src)
    dest.n1.getAllInstances.size should be(1)
    dest.n2.getAllInstances.size should be(0)
    dest.e.forward.index.size should be(0)
  }

  "adding from model" should "should copy everything" in {
    val src = new TestGraph
    src.n1.populate(Seq("Test"))
    src.n1.getAllInstances.size should be(1)
    src.n2.getAllInstances.size should be(1)
    src.e.forward.index.size should be(1)

    val dest = new TestGraph
    dest.addFromModel(src)
    dest.n1.getAllInstances.size should be(1)
    dest.n2.getAllInstances.size should be(1)
    dest.e.forward.index.size should be(1)
  }

  "clearing the model" should "remove all instances" in {
    import TestGraph._

    n1.populate(Seq("Test"))
    n1.getAllInstances.size should be(1)
    n2.getAllInstances.size should be(1)
    e.forward.index.size should be(1)

    clearInstances
    n1.getAllInstances.size should be(0)
    n2.getAllInstances.size should be(0)
    e.forward.index.size should be(0)
  }

  "clearing a node" should "remove all instances of its neighboring edges" in {
    import TestGraph._

    n1.populate(Seq("Test"))
    n1.getAllInstances.size should be(1)
    n2.getAllInstances.size should be(1)
    e.forward.index.size should be(1)

    n1.clear
    n1.getAllInstances.size should be(0)
    n2.getAllInstances.size should be(1)
    e.forward.index.size should be(0)
  }

  "populating a node" should "respect String key function" in {
    import TestGraph._

    n1.populate(Seq("Test"))
    n1.getAllInstances.size should be(1)
    n1.populate(Seq("test"))
    n1.getAllInstances.size should be(1)
    n1.populate(Seq("tset"))
    n1.getAllInstances.size should be(2)
    clearInstances
  }

  "populating a node" should "respect Int key function" in {
    import TestGraph._

    n2.populate(Seq("Test1"))
    n2.getAllInstances.size should be(1)
    n2.populate(Seq("test2"))
    n2.getAllInstances.size should be(1)
    n2.populate(Seq("test"))
    n2.getAllInstances.size should be(2)
    clearInstances
  }

  "populating an edge" should "respect key functions" in {
    import TestGraph._

    n1.populate(Seq("test1"))
    n1.getAllInstances.size should be(1)
    n2.getAllInstances.size should be(1)
    n1.populate(Seq("Test1"))
    n1.getAllInstances.size should be(1)
    n2.getAllInstances.size should be(1)
    n1.populate(Seq("same1"))
    n1.getAllInstances.size should be(2)
    n2.getAllInstances.size should be(1)
    clearInstances
  }

  "querying the edge" should "respect key functions" in {
    import TestGraph._

    n1.populate(Seq("test1"))
    n1.getAllInstances.size should be(1)
    n2.getAllInstances.size should be(1)
    e.forward.neighborsOf("test2").size should be(0)
    e.forward.neighborsOf("Test1").size should be(1)
    e.backward.neighborsOf("test2").size should be(0)
    e.backward.neighborsOf("Same").size should be(1)
  }
}
