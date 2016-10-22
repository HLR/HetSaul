/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Triplet.{ SpRelation, SpRelationLabels }
import org.scalatest.{ FlatSpec, Matchers }

/** Created by taher on 8/14/16.
  */
class SpRLDataModelReaderTests extends FlatSpec with Matchers {
  val path = getResourcePath("SpRL/2012/")

  private def getResourcePath(relativePath: String): String =
    getClass.getClassLoader.getResource(relativePath).getPath

  import SpRLDataModel._

  PopulateSpRLDataModel(path, true, "2012", "Relation", null)

  "SpRL Data Model Reader" should "Reads data correctly." in {

    val sentenceList = sentences().collect {
      case s if !s.getSentence.getSentenceConstituent.getTextAnnotation.getId.startsWith("example.xml") =>
        s.getSentence
    }.toList

    val relationList = relations().toList

    sentenceList.size should be(5)

    relationList.count(x => x.getSentence == sentenceList(0) &&
      x.getLabel == SpRelationLabels.GOLD) should be(1)

    relationList.count(x => x.getSentence == sentenceList(1) &&
      x.getLabel == Triplet.SpRelationLabels.GOLD) should be(2)

    relationList.count(x => x.getSentence == sentenceList(2) &&
      x.getLabel == Triplet.SpRelationLabels.GOLD) should be(1)

    relationList.count(x => x.getSentence == sentenceList(3) &&
      x.getLabel == Triplet.SpRelationLabels.GOLD) should be(2)
  }

  "SpRL Data Model Features" should "be correct for examples of the paper." in {
    val examples = sentences().collect {
      case s if s.getSentence.getSentenceConstituent.getTextAnnotation.getId.startsWith("example.xml") =>
        s.getSentence
    }.toList

    val e1 = examples(0)
    val rels1 = relations().filter(_.getSentence == e1).toList
    val golds1 = rels1.filter(_.getLabel == Triplet.SpRelationLabels.GOLD).toList
    val rel11 = golds1.head

    val e2 = examples(1)
    val rels2 = relations().filter(_.getSentence == e2).toList
    val golds2 = rels2.filter(_.getLabel == Triplet.SpRelationLabels.GOLD).toList
    val rel21 = golds2.filter(_.getTrajector.getText == "bushes").head
    val rel22 = golds2.filter(_.getTrajector.getText == "trees").head

    val e3 = examples(2)
    val rels3 = relations().filter(_.getSentence == e3).toList
    val golds3 = rels3.filter(_.getLabel == Triplet.SpRelationLabels.GOLD).toList
    val rel31 = golds3.head

    val e4 = examples(3)
    val rels4 = relations().filter(_.getSentence == e4).toList
    val golds4 = rels4.filter(_.getLabel == Triplet.SpRelationLabels.GOLD).toList
    val rel41 = golds4.head

    golds1.size should be(1)
    golds2.size should be(2)
    golds3.size should be(1)
    golds4.size should be(1)

    BF1(rel11) should be("cars")
    BF1(rel21) should be("bushes")
    BF1(rel22) should be("trees")
    BF1(rel31) should be("football")
    BF1(rel41) should be("trees")

    BF2(rel11) should be("house")
    BF2(rel21) should be("hill")
    BF2(rel22) should be("hill")
    BF2(rel31) should be("column")
    BF2(rel41) should be(undefined)

    BF3(rel11) should be("in_front_of")
    BF3(rel21) should be("on")
    BF3(rel22) should be("on")
    BF3(rel31) should be("on_top")
    BF3(rel41) should be("on_the_right")

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

    JF2_1(rel11) should be("cars::in_front_of::house")
    JF2_1(rel21) should be("bushes::on::hill")
    JF2_1(rel22) should be("trees::on::hill")

    JF2_2(rel11) should be("false")
    JF2_2(rel21) should be("false")
    JF2_2(rel22) should be("false")

    JF2_3(rel11) should be("parked,the")
    JF2_3(rel21) should be("and,small,trees,the")
    JF2_3(rel22) should be("the")

    JF2_4(rel11) should be("in_front_of::↓POBJ")
    JF2_4(rel21) should be("on::↓POBJ")
    JF2_4(rel22) should be("on::↓POBJ")

    JF2_5(rel11) should be("cars")
    JF2_5(rel21) should be("bushes")
    JF2_5(rel22) should be("trees")

    JF2_6(rel11) should be("↓POBJ")
    JF2_6(rel21) should be("↓POBJ")
    JF2_6(rel22) should be("↓POBJ")

    JF2_7(rel11) should be("↑NSUBJ↓PREP::in_front_of")
    JF2_7(rel21) should be("↓PREP::on")
    JF2_7(rel22) should be("↑CONJ↓PREP::on")

    JF2_8(rel11) should startWith("hyp:dwelling,hyp:home,hyp:domicile,hyp:abode")
    JF2_8(rel21) should startWith("hyp:natural_elevation,hyp:elevation,hyp:structure")
    JF2_8(rel22) should startWith("hyp:natural_elevation,hyp:elevation,hyp:structure")

    JF2_9(rel11) should startWith("hyp:motor_vehicle,hyp:automotive_vehicle,hyp:wheeled_vehicle,hyp:compartment")
    JF2_9(rel21) should startWith("hyp:woody_plant,hyp:ligneous_plant,hyp:wilderness,hyp:wild,hyp:vegetation")
    JF2_9(rel22) should startWith("hyp:woody_plant,hyp:ligneous_plant,hyp:plane_figure,hyp:two-dimensional_figure")

    JF2_10(rel11) should be("TRAJECTOR_parked_INDICATOR_the_LANDMARK")
    JF2_10(rel21) should be("TRAJECTOR_and_small_trees_INDICATOR_the_LANDMARK")
    JF2_10(rel22) should be("TRAJECTOR_INDICATOR_the_LANDMARK")

    JF2_11(rel11) should be("")
    JF2_11(rel21) should be("")
    JF2_11(rel22) should be("")
    JF2_11(rels1(3)) should be("of") // cars parked in[INDICATOR] front[TRAJECTOR] of the house: front---prep--->of

    // TODO: enable these tests when SRL became more accurate or using curator for annotation
    //JF2_12(rel11) should be("TRAJECTOR=A1;INDICATOR=AM-LOC;LANDMARK=AM-LOC")
    //JF2_12(rel21) should be("TRAJECTOR=;INDICATOR=;LANDMARK=")
    //JF2_12(rel22) should be("TRAJECTOR=;INDICATOR=;LANDMARK=")

    JF2_13(rel11) should be("false")
    JF2_13(rel21) should be("false")
    JF2_13(rel22) should be("false")
    JF2_13(rel41) should be("true")

    JF2_14(rel11) should be("car::in_front_of::house")
    JF2_14(rel21) should be("bush::on::hill")
    JF2_14(rel22) should be("tree::on::hill")

    JF2_15(rel11) should be("false")
    JF2_15(rel21) should be("false")
    JF2_15(rel22) should be("false")
    JF2_15(rel31) should be("true") // a huge column[LANDMARK] with a football[TRAJECTOR] on top[INDICATOR]:
    // with---POBJ--->football

    BH1(rel11) should be("TRAJECTOR_INDICATOR_LANDMARK")
    BH1(rel21) should be("TRAJECTOR_INDICATOR_LANDMARK")
    BH1(rel22) should be("TRAJECTOR_INDICATOR_LANDMARK")
    BH1(rel31) should be("LANDMARK_TRAJECTOR_INDICATOR")
    BH1(rel41) should be("TRAJECTOR_INDICATOR")
  }

}
