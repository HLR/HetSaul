package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge
import org.scalatest.{ Matchers, FlatSpec }

class SensorsTest extends FlatSpec with Matchers {

  def links[A <:AnyRef,B <:AnyRef](e: Edge[A,B]): Seq[(A,B)] = e.forward.index.flatMap((p) => p._2.map(b => p._1->b)).toSeq

  "adding matching sensor" should "populate direct links" in {
    val dm = new DataModel {
      val n1 = node[String]
      val n2 = node[String]
      val e = edge(n1,n2)
      e.addSensor(_.charAt(0) == _.charAt(0))
    }
    import dm._

    n1.getAllInstances.size should be(0)
    n2.getAllInstances.size should be(0)
    links(e).size should be(0)

    n1.populate(Seq("Dave", "John", "Mark", "Michael"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(0)
    links(e).size should be(0)

    n2.populate(Seq("Diamond", "Jacobs", "Maron"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(3)
    links(e).size should be(4)
  }

  "adding matching sensor" should "populate indirect links" in {
    val dm = new DataModel {
      val n1 = node[String]
      val n2 = node[String]
      val n3 = node[String]
      val e1 = edge(n1,n2)
      val e2 = edge(n2,n3)
      e1.addSensor(_.charAt(0) == _.charAt(0))
      e2.addSensor(_.charAt(0) == _.charAt(0))
    }
    import dm._

    n1.getAllInstances.size should be(0)
    n2.getAllInstances.size should be(0)
    n3.getAllInstances.size should be(0)
    links(e1).size should be(0)
    links(e2).size should be(0)

    n1.populate(Seq("Dave", "John", "Mark", "Michael"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(0)
    n3.getAllInstances.size should be(0)
    links(e1).size should be(0)
    links(e2).size should be(0)

    n2.populate(Seq("Diamond", "Jacobs", "Maron"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(3)
    n3.getAllInstances.size should be(0)
    links(e1).size should be(4)
    links(e2).size should be(0)

    n3.populate(Seq("Drew", "Jamerson", "Magic"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(3)
    n3.getAllInstances.size should be(3)
    links(e1).size should be(4)
    links(e2).size should be(3)
  }

}
