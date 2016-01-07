package edu.illinois.cs.cogcomp.saul.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.toyDataModel._

import org.scalatest.{ Matchers, FlatSpec }

class jsonInterfaceTest extends FlatSpec with Matchers {
  "jsonInterface " should " successsfully convert the information about nodes, edges and properties into a json string " in {

    /** a toy data model withuot any meaning whatsoever */
    object toyDataModelJsonInterfaceTest extends DataModel {
      val predicates = node[String]
      val arguments = node[String]
      val relations = node[String]
      val sentencesToRelations = edge(arguments, relations)
      val sentencesToTrees = edge(predicates, relations)
      val isPredicate = property[String](predicates, "p") { x: String => "" }
    }

    val predictedOutput = "{\"nodes\":[\"predicates\",\"arguments\",\"relations\"],\"edges\":[\"sentencesToRelations\",\"sentencesToTrees\"],\"properties\":[\"isPredicate\"]}"

    dataModelJsonInterface.getJson(toyDataModelJsonInterfaceTest) should be(predictedOutput)
  }
}

