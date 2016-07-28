/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{ FlatSpec, Matchers }
class contextualFeaturesTest extends FlatSpec with Matchers {

  class TestGraphC extends DataModel {
    val firstNames = node[String]
    val lastNames = node[String]
    val name = edge(firstNames, lastNames, 'names)
    val prefix = property(firstNames, "prefix")((s: String) => s.charAt(1).toString)

    firstNames.populate(Seq("Dave", "John", "Mark", "Michael"))
    lastNames.populate(List("Dell", "Jacobs", "Maron", "Mario"))

    name.populateWith(_.charAt(0) == _.charAt(0))
  }

  val graphObject = new TestGraphC()
  import graphObject._
  "finding the nodes in a window in the neighborhood" should "find the neighbors in a window" in {
    firstNames.getWithWindow(firstNames.getAllInstances.head, 0, 1).toSet should be(Set(None, Some("Dave"), Some("John")))
    firstNames.getWithWindow(firstNames.getAllInstances.head, -2, 2).toSet should be(Set(None, Some("Dave"), Some("John"), Some("Mark")))
    lastNames.getWithWindow(lastNames.getAllInstances.head, -2, 2).toSet should be(Set(None, Some("Dell"), Some("Jacobs"), Some("Maron")))
    val query2 = lastNames() prop prefix
    query2.toSet should be(Set("a", "e"))
  }

}
