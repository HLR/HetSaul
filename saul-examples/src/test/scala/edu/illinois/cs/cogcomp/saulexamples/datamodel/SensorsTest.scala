/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{ Matchers, FlatSpec }

class SensorsTest extends FlatSpec with Matchers {

  "adding matching sensor" should "populate direct links" in {
    val dm = new DataModel {
      val n1 = node[String]
      val n2 = node[String]
      val e = edge(n1, n2)
      e.addSensor(_.charAt(0) == _.charAt(0))
    }
    import dm._

    n1.getAllInstances.size should be(0)
    n2.getAllInstances.size should be(0)
    e.links.size should be(0)

    n1.populate(Seq("Dave", "John", "Mark", "Michael"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(0)
    e.links.size should be(0)

    n2.populate(Seq("Diamond", "Jacobs", "Maron"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(3)
    e.links.size should be(4)
  }

  "adding generating sensor" should "populate direct links and neighbors" in {
    val dm = new DataModel {
      val n1 = node[String]
      val n2 = node[String]
      val e = edge(n1, n2)
      e.addSensor((s: String) => s.toUpperCase)
    }
    import dm._

    n1.getAllInstances.size should be(0)
    n2.getAllInstances.size should be(0)
    e.links.size should be(0)

    n1.populate(Seq("Dave", "John", "Mark", "Michael"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(4)
    e.links.size should be(4)

    n2.getAllInstances.toSet should be(n1.getAllInstances.map(_.toUpperCase).toSet)
    e.links.forall { case (first, second) => first.toUpperCase == second } should be(right = true)
  }

  "adding 2 singleton generating sensors" should "populate indirect links and neighbors" in {
    val dm = new DataModel {
      val n1 = node[String]
      val n2 = node[String]
      val n3 = node[String]
      val e1 = edge(n1, n2)
      val e2 = edge(n2, n3)
      e1.addSensor((s: String) => s.toUpperCase)
      e2.addSensor((s: String) => s.toLowerCase)
    }
    import dm._

    n1.getAllInstances.size should be(0)
    n2.getAllInstances.size should be(0)
    n3.getAllInstances.size should be(0)
    e1.links.size should be(0)
    e2.links.size should be(0)

    n1.populate(Seq("Dave", "John", "Mark", "Michael"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(4)
    n3.getAllInstances.size should be(4)
    e1.links.size should be(4)
    e2.links.size should be(4)

    n2.getAllInstances.toSet should be(n1.getAllInstances.map(_.toUpperCase).toSet)
    e1.links.forall { case (first, second) => first.toUpperCase == second } should be(right = true)
    n3.getAllInstances.toSet should be(n1.getAllInstances.map(_.toLowerCase).toSet)
    e2.links.forall { case (first, second) => first.toLowerCase == second } should be(right = true)
  }

  "adding a multiple generator sensors" should "populate indirect links and neighbors" in {
    val dm = new DataModel {
      type P = (String, Seq[String])
      val n1 = node[P]
      val n2 = node[String]
      val e = edge(n1, n2)
      e.addSensor((p: P) => p._2)
    }
    import dm._

    n1.getAllInstances.size should be(0)
    n2.getAllInstances.size should be(0)
    e.links.size should be(0)

    n1.populate(Seq("males" -> Seq("blue"), "females" -> Seq("pink"), "unisex" -> Seq("yellow", "green")))
    n1.getAllInstances.size should be(3)
    n2.getAllInstances.size should be(4)
    e.links.size should be(4)

    (n1() ~> e).instances.size should be(4)
    (n1("males" -> Seq("blue")) ~> e).instances.size should be(1)
    (n1("unisex" -> Seq("yellow", "green")) ~> e).instances.size should be(2)

    (n2() ~> -e).instances.size should be(3)
    (n2("blue") ~> -e).instances.size should be(1)
    (n2("green") ~> -e).instances.size should be(1)

    (n1() ~> e ~> -e).instances.size should be(3)
    (n1("males" -> Seq("blue")) ~> e ~> -e).instances.size should be(1)
    (n1("unisex" -> Seq("yellow", "green")) ~> e ~> -e).instances.size should be(1)
  }

  "adding generating and matching sensors" should "populate indirect links and neighbors" in {
    val dm = new DataModel {
      val n1 = node[String]
      val n2 = node[String]
      val n3 = node[String]
      val e1 = edge(n1, n2)
      val e2 = edge(n2, n3)
      e1.addSensor(_.charAt(0) == _.charAt(0))
      e2.addSensor((s: String) => s.toUpperCase)
    }
    import dm._

    n1.getAllInstances.size should be(0)
    n2.getAllInstances.size should be(0)
    n3.getAllInstances.size should be(0)
    e1.links.size should be(0)
    e2.links.size should be(0)

    n1.populate(Seq("Dave", "John", "Mark", "Michael"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(0)
    n3.getAllInstances.size should be(0)
    e1.links.size should be(0)
    e2.links.size should be(0)

    n2.populate(Seq("Diamond", "Jacobs", "Maron"))
    n1.getAllInstances.size should be(4)
    n2.getAllInstances.size should be(3)
    n3.getAllInstances.size should be(3)
    e1.links.size should be(4)
    e2.links.size should be(3)

    n3.getAllInstances.toSet should be(n2.getAllInstances.map(_.toUpperCase).toSet)
    e2.links.forall { case (first, second) => first.toUpperCase == second } should be(right = true)
  }

}
