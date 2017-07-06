package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers

import edu.illinois.cs.cogcomp.saulexamples.data.CLEFImageReader
import edu.illinois.cs.cogcomp.saulexamples.vision.{Image, Segment, SegmentRelation}

import scala.collection.JavaConversions._

/** Created by taher on 2017-02-28.
  */
class ImageReaderHelper(dataDir: String, trainFileName: String, testFileName: String, isTrain: Boolean) {

  lazy val reader = new CLEFImageReader(dataDir, trainFileName, testFileName, false)

  def getImageRelationList: List[SegmentRelation] = {

    if (isTrain) {
      reader.trainingRelations.toList
    } else {
      reader.testRelations.toList
    }
  }

  def getSegmentList: List[Segment] = {

    if (isTrain) {
      reader.trainingSegments.toList
    } else {
      reader.testSegments.toList
    }
  }

  def getImageList: List[Image] = {

    if (isTrain) {
      reader.trainingImages.toList
    } else {
      reader.testImages.toList
    }
  }
}
