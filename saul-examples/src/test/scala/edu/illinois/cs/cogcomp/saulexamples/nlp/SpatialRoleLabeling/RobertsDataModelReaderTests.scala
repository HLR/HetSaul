package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import org.scalatest.{FlatSpec, Matchers}

/** Created by taher on 8/14/16.
  */
class RobertsDataModelReaderTests extends FlatSpec with Matchers {
  val path = getResourcePath("SpRL/2012/")

  private def getResourcePath(relativePath: String): String =
    getClass.getClassLoader.getResource(relativePath).getPath

  import RobertsDataModel._

  PopulateSpRLDataModel(path, true, "2012", "Roberts")

  "Roberts Data Model Reader" should "Read data correctly." in {

    val sentenceList = sentences()
      .filterNot(x => x.getSentenceConstituent.getTextAnnotation.getId.startsWith("example.xml")).toList
    val relationList = relations().toList

    sentenceList.size should be(5)

    relationList.count(x => x.getSentence == sentenceList(0)) should be(32)

    relationList.count(x => x.getSentence == sentenceList(0) &&
      x.getLabel == RobertsRelation.RobertsRelationLabels.GOLD) should be(1)

    relationList.count(x => x.getSentence == sentenceList(1)) should be(16)

    relationList.count(x => x.getSentence == sentenceList(1) &&
      x.getLabel == RobertsRelation.RobertsRelationLabels.GOLD) should be(2)

    relationList.count(x => x.getSentence == sentenceList(2)) should be(18)

    relationList.count(x => x.getSentence == sentenceList(2) &&
      x.getLabel == RobertsRelation.RobertsRelationLabels.GOLD) should be(1)

    relationList.count(x => x.getSentence == sentenceList(3)) should be(32)

    relationList.count(x => x.getSentence == sentenceList(3) &&
      x.getLabel == RobertsRelation.RobertsRelationLabels.GOLD) should be(2)
  }

  "Roberts Data Model Features" should "be correct for examples of the paper." in {
    val examples = sentences()
      .filter(x => x.getSentenceConstituent.getTextAnnotation.getId.contains("example.xml")).toList
    val e1 = examples(0)
    val rels1 = relations().filter(x => x.getSentence == e1).toList
    val golds1 = rels1.filter(x => x.getLabel == RobertsRelation.RobertsRelationLabels.GOLD).toList
    val rel11 = golds1.head

    val e2 = examples(1)
    val rels2 = relations().filter(x => x.getSentence == e2).toList
    val golds2 = rels2.filter(x => x.getLabel == RobertsRelation.RobertsRelationLabels.GOLD).toList
    val rel21 = golds2.filter(x => x.getTrajector.getText == "bushes").head
    val rel22 = golds2.filter(x => x.getTrajector.getText == "trees").head

    rels1.size should be(18)
    rels2.size should be(9)

    golds1.size should be(1)
    golds2.size should be(2)

    BF1(rel11) should be("cars")
    BF1(rel21) should be("bushes")
    BF1(rel22) should be("trees")

    BF2(rel11) should be("house")
    BF2(rel21) should be("hill")
    BF2(rel22) should be("hill")

    BF3(rel11) should be("in_front_of")
    BF3(rel21) should be("on")
    BF3(rel22) should be("on")

    BF4(rel11) should be("car")
    BF4(rel21) should be("bush")
    BF4(rel22) should be("tree")

    BF5(rel11) should be("house")
    BF5(rel21) should be("hill")
    BF5(rel22) should be("hill")

    BF6(rel11) should be("↑NSUBJ↓PREP")
    BF6(rel21) should be("↓PREP")
    BF6(rel22) should be("↑CONJ↓PREP")

    BF7(rel11) should be("↓POBJ")
    BF7(rel21) should be("↓POBJ")
    BF7(rel22) should be("↓POBJ")

  }

}
