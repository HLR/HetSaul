package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers

import java.io.File

import edu.illinois.cs.cogcomp.core.utilities.XmlModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2017.SpRL2017Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLDataReader
import org.apache.commons.io.FileUtils

import scala.util.Random
import scala.collection.JavaConversions._

/**
  * Created by taher on 2017-04-23.
  */
object dataSetUtils {

  def create10Folds(path: String): Unit = {
    val parentDir = new File(path).getParent
    val reader = new SpRLDataReader(path, classOf[SpRL2017Document])
    reader.readData()
    val doc = reader.documents.head
    val foldSize = Math.ceil(doc.getScenes.length / 10.0).toInt
    val folds = doc.getScenes.sortBy(x => Random.nextGaussian()).zipWithIndex.groupBy(x => x._2 / foldSize)
    folds.foreach(f => {
      val test = new SpRL2017Document
      test.setScenes(f._2.map(_._1))
      val train = new SpRL2017Document
      train.setScenes(folds.filter(_._1 != f._1).flatMap(_._2.map(_._1)).toList)
      FileUtils.forceMkdir(new File(parentDir + s"/fold${f._1 + 1}"))
      XmlModel.write(test, parentDir + s"/fold${f._1 + 1}/test.xml")
      XmlModel.write(train, parentDir + s"/fold${f._1 + 1}/train.xml")
    })
  }

}
