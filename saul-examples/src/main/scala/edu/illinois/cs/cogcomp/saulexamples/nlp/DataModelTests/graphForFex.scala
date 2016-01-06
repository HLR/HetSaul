package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import scala.collection.JavaConverters._

object graphForFex extends DataModel {
  val firstNames = node[String]
  val lastNames = node[String]
  val name = edge(firstNames, lastNames, 'names)
  val prefix = property(firstNames, "prefix")((s: String) => s.charAt(1).toString)

  firstNames.populate(Seq("Dave", "John", "Mark", "Michael"))
  lastNames.populate(List("Dell", "Jacobs", "Maron", "Mario"))

  name.populateWith(_.charAt(0) == _.charAt(0))
  val query2 = (lastNames() prop prefix).asJava
  val query1 = (lastNames("Jacobs") ~> -name prop prefix).asJava
}

