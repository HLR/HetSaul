/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{ Matchers, FlatSpec }

class GraphQueriesTest extends FlatSpec with Matchers {

  object TestGraph extends DataModel {
    val firstNames = node[String]
    val lastNames = node[String]
    val name = edge(firstNames, lastNames, 'names)
    val prefix = property(firstNames, "prefix")((s: String) => s.charAt(1).toString)

    firstNames.populate(Seq("Dave", "John", "Mark", "Michael"))
    lastNames.populate(List("Dell", "Jacobs", "Maron", "Mario"))

    name.populateWith(_.charAt(0) == _.charAt(0))
  }

  "finding neighbors of a link" should "find the neighbors" in {
    import TestGraph._
    name.forward.neighborsOf("Dave") should be(Seq("Dell"))
    name.forward.neighborsOf("John") should be(Seq("Jacobs"))
  }

  "finding neighbors of a reverse link" should "find the reverse neighbors" in {
    import TestGraph._
    name.backward.neighborsOf("Jacobs") should be(Seq("John"))
    name.backward.neighborsOf("Maron") should be(Seq("Mark", "Michael"))
  }

  "atomic queries" should "return themselves" in {
    import TestGraph._
    firstNames() should be(Seq("Dave", "John", "Mark", "Michael"))
    firstNames("Jim") should be(Seq("Jim"))
  }

  "single hop with all instances" should "return their neighbors" in {
    import TestGraph._
    val query = firstNames() ~> name
    query should be(Seq("Dell", "Jacobs", "Maron", "Mario"))
  }

  "single hop with custom instances" should "return their neighbors" in {
    import TestGraph._

    val query1 = firstNames("John") ~> name
    query1 should be(Seq("Jacobs"))

    val query2 = firstNames("Mark") ~> name
    query2 should be(Seq("Maron", "Mario"))
  }

  "single reverse hop with custom instances" should "return their neighbors" in {
    import TestGraph._

    val query = lastNames() ~> -name
    query.toSet should be(firstNames.getAllInstances.toSet)

    val query1 = lastNames("Jacobs") ~> -name
    query1 should be(Seq("John"))

    val query2 = lastNames("Maron") ~> -name
    query2 should be(Seq("Mark", "Michael"))
  }

  "reverse hop with custom instances" should "return similar ones" in {
    import TestGraph._

    val query1 = firstNames("John") ~> name ~> -name
    query1 should be(Set("John"))

    val query2 = firstNames("Mark") ~> name ~> -name
    query2 should be(Seq("Mark", "Michael"))
  }

  "prop on single node, single instance" should "return the correct value" in {
    import TestGraph._

    val query1 = firstNames("John") prop prefix
    query1.toSet should be(Set("o"))

    val query2 = lastNames("Maron") prop prefix
    query2.toSet should be(Set("a"))
  }

  "prop on single node, multiple instances" should "return the correct set" in {
    import TestGraph._

    val query1 = firstNames(Seq("John", "Dave", "Mark")) prop prefix
    query1.toSet should be(Set("o", "a"))

    val query2 = lastNames() prop prefix
    query2.toSet should be(Set("a", "e"))
  }

  "prop on query, multiple instances" should "return the correct values" in {
    import TestGraph._

    val query1 = firstNames("John") ~> name prop prefix
    query1.toSet should be(Set("a"))
    query1.counts.size should be(1)
    query1.counts("a") should be(1)

    val query2 = firstNames("Mark") ~> name prop prefix
    query2.toSet should be(Set("a"))
    query2.counts.size should be(1)
    query2.counts("a") should be(2)

    val query3 = firstNames() ~> name prop prefix
    query3.toSet should be(Set("a", "e"))
    query3.counts.size should be(2)
    query3.counts("a") should be(3)
    query3.counts("e") should be(1)
  }
}
