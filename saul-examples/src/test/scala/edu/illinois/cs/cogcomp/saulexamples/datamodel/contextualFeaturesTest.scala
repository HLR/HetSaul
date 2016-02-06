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
  "finding the nodes in a window in the neighbohood" should "find the neighbors in a window" in {
    graphObject.getNodeWithType[String].getWithWindow(graphObject.firstNames.getAllInstances.head, 0, 1).toSet should be(Set(None, Some("Dave"), Some("John")))
    graphObject.firstNames.getWithWindow(graphObject.firstNames.getAllInstances.head, -2, 2).toSet should be(Set(None, Some("Dave"), Some("John"), Some("Mark")))
    graphObject.lastNames.getWithWindow(graphObject.lastNames.getAllInstances.head, -2, 2).toSet should be(Set(None, Some("Dell"), Some("Jacobs"), Some("Maron")))
    val query2 = graphObject.lastNames() prop graphObject.prefix
    query2.toSet should be(Set("a", "e"))
  }

}
