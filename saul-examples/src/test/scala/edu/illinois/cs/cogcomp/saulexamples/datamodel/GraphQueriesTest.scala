package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{Matchers, FlatSpec}

/**
 * @author sameer
 * @since 10/8/15.
 */
class GraphQueriesTest extends FlatSpec with Matchers {

  object TestGraph extends DataModel {
    val firstNames = node[String]
    val lastNames = node[String]
    val name = edge(firstNames, lastNames, 'names)

    firstNames.populate(Seq("Dave", "John", "Mark", "Michael"))

    name.populateWith(List("Dell", "Jacobs", "Maron", "Mario"), _.charAt(0) == _.charAt(0))
  }

  "finding neighbors of a link" should "find the neighbors" in {
    import TestGraph._
    name.forward.neighborsOf("Dave").toSet should be(Set("Dell"))
    name.forward.neighborsOf("John").toSet should be(Set("Jacobs"))
  }

  "finding neighbors of a reverse link" should "find the reverse neighbors" in {
    import TestGraph._
    // TODO: Doesn't work right now
    // name.backward.neighborsOf("Jacobs").toSet should be(Set("John"))
    // name.backward.neighborsOf("Maron").toSet should be(Set("Mark", "Michael"))
  }

  "atomic queries" should "return themselves" in {
    import TestGraph._
    firstNames().instances
    firstNames().instances should be(Seq("Dave", "John", "Mark", "Michael"))
    println(firstNames("Jim").instances.mkString(", "))
    firstNames("Jim").instances should be(Seq("Jim"))
  }

  "single hop with all instances" should "return their neighbors" in {
    import TestGraph._
    val query = firstNames() ~> name
    query.instances.toSet should be(Set("Dell", "Jacobs", "Maron", "Mario"))
  }

  "single hop with custom instances" should "return their neighbors" in {
    import TestGraph._

    val query1 = firstNames("John") ~> name
    query1.instances.toSet should be(Set("Jacobs"))

    val query2 = firstNames("Mark") ~> name
    query2.instances.toSet should be(Set("Maron", "Mario"))
  }

  "single reverse hop with custom instances" should "return their neighbors" in {
    import TestGraph._

    val query = lastNames() ~> -name
    // TODO: Doesn't work right now
    // query.instances.toSet should be(firstNames.getAllInstances.toSet)

    val query1 = lastNames("Jacobs") ~> -name
    // TODO: Doesn't work right now
    // query1.instances.toSet should be(Set("John"))

    val query2 = lastNames("Maron") ~> -name
    // TODO: Doesn't work right now
    // query2.instances.toSet should be(Set("Mark", "Michael"))
  }

  "reverse hop with custom instances" should "return similar ones" in {
    import TestGraph._

    val query1 = firstNames("John") ~> name ~> -name
    // TODO: Doesn't work right now
    // query1.instances.toSet should be(Set("John"))

    val query2 = firstNames("Mark") ~> name ~> -name
    // TODO: Doesn't work right now
    // query2.instances.toSet should be(Set("Mark", "Michael"))
  }
}
