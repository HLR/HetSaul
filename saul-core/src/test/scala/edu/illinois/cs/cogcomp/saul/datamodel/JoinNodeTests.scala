/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel

import org.scalatest.{ BeforeAndAfter, FlatSpec, Matchers }

class JoinNodeTests extends FlatSpec with Matchers with BeforeAndAfter {

  private object TestDataModel extends DataModel {
    val names = node[String]
    val characters = node[Character]

    val nameToCharacters = edge(names, characters)
    nameToCharacters.addSensor({ name: String => name.toList.map((c: Char) => Char.box(c.toUpper)) })

    val joinNames = join(names, names)((_: String, _: String) => true) // all-pairs
    val joinNamesFirstCharMatch = join(names, names)((a: String, b: String) => a.charAt(0) == b.charAt(0))

    val joinNodeWithChar = join(names, characters)((_: String, _: Character) => true) // all-pairs
    val joinNodeWithCharFirstCharMatch = join(names, characters)((a: String, b: Character) => a.charAt(0) == b)

    // Probably an unreal use-case.
    val complexJoinNode = join(joinNodeWithCharFirstCharMatch, characters)(
      { case ((_: String, _: Character), _: Character) => true }
    ) // all-pairs
  }

  before {
    TestDataModel.clearInstances()
  }

  "Populating join nodes" should "work correctly" in {
    val data = List("Abe", "Adele", "Bach", "Mozart")

    import TestDataModel._
    names.populate(data)

    val numNames = data.size
    val numChars = data.flatMap(_.toList.map(_.toUpper)).distinct.size

    names.getAllInstances.size should be(numNames)
    characters.getAllInstances.size should be(numChars)

    joinNames.getAllInstances.size should be(10) // 4_choose_2 + 4

    joinNamesFirstCharMatch.getAllInstances.size should be(5) // A=2_choose_2 + 2; B=1; M=1

    joinNodeWithChar.getAllInstances.size should be(numNames * numChars) // all-pairs

    joinNodeWithCharFirstCharMatch.getAllInstances.size should be(numNames) // one for each name.

    complexJoinNode.getAllInstances.size should be(numNames * numChars)
  }
}
