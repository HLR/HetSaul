/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.saulexamples.data.DocumentReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests.modelWithKeys._
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors

import scala.collection.JavaConversions._

object graphPopulationMsensors {
  def main(args: Array[String]): Unit = {
    val data = new DocumentReader("./data/20newsToy/train").docs.toList.slice(0, 2)
    val taList = data.map(CommonSensors.annotateWithCurator)
    val sentenceList = taList.flatMap(_.sentences())

    modelWithSensors.document populate taList
    modelWithSensors.sentence.populate(sentenceList)
    modelWithSensors.docToSen populateWith (CommonSensors.textAnnotationSentenceAlignment _)

    val taa = modelWithSensors.document.getAllInstances
    val sen = modelWithSensors.sentence.getAllInstances
    val x1 = sentence() ~> -docTosen
    val x2 = document() ~> docTosen

    logger.debug(s"x1.size = ${x1.size}")
    logger.debug(s"x2.size = ${x2.size}")
  }
}
