package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{FlatSpec, Matchers}
class contextualFeaturesTest  extends FlatSpec with Matchers  {

  object TestGraph extends DataModel {
    val firstNames = node[String]
    val lastNames = node[String]
    val name = edge(firstNames, lastNames, 'names)
    val prefix = property[String]("prefix")((s: String) => s.charAt(1).toString)

    firstNames.populate(Seq("Dave", "John", "Mark", "Michael"))
    lastNames.populate(List("Dell", "Jacobs", "Maron", "Mario"))

    name.populateWith(_.charAt(0) == _.charAt(0))

  }

  "finding the nodes in a window in the neighbohood" should "find the neighbors in a window" in {
    import TestGraph._
    getNodeWithType[String].getWithWindow(firstNames.getAllInstances.head,0,1).toSet should be(Set(Some("Dave"), Some("John")))
  }


}
