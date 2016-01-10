package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{ Matchers, FlatSpec }

class NodeTest extends FlatSpec with Matchers {

  object TestGraph extends DataModel {
    val n1 = node[String]((s: String) => s.toLowerCase)
    val n2 = node[String]((s: String) => s.length)

    val e = edge(n1, n2)
    e.addSensor((s: String) => s.take(4))
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
  }
}
