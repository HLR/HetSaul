package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import org.scalatest.{FlatSpec, Matchers}

/** Created by taher on 8/14/16.
  */
class RobertsDataModelReaderTests extends FlatSpec with Matchers {
  val path = getResourcePath("SpRL/2012/")

  private def getResourcePath(relativePath: String): String =
    getClass.getClassLoader.getResource(relativePath).getPath

  import RobertsDataModel._

  PopulateSpRLDataModel(path, true, "2012")

  "Roberts Data Model Readers" should "Read train_sample correctly." in {

    val sentenceList = sentences().toList
    val relationList = relations().toList

    sentenceList.size should be(5)

    relationList.count(x => x.getSentence == sentenceList(0)) should be(32)

    relationList.count(x => x.getSentence == sentenceList(0) &&
      x.getLabel == RobertsRelation.RobertsRelationLabels.GOLD) should be(1)

    relationList.count(x => x.getSentence == sentenceList(1)) should be(16)

    relationList.filter(x => x.getSentence == sentenceList(1)).foreach(println)
    relationList.count(x => x.getSentence == sentenceList(1) &&
      x.getLabel == RobertsRelation.RobertsRelationLabels.GOLD) should be(2)
  }

}
