/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{ FlatSpec, Matchers }
import util.DataModelJsonInterface

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

    val predictedOutput = "{\"nodes\":[\"predicates\",\"arguments\",\"relations\"],\"edges\":{\"sentencesToRelations\":[\"arguments\",\"relations\"],\"sentencesToTrees\":[\"predicates\",\"relations\"]},\"properties\":{\"isPredicate\":\"predicates\"}}"

    DataModelJsonInterface.getSchemaJson(toyDataModelJsonInterfaceTest).toString should be(predictedOutput)
  }
}

