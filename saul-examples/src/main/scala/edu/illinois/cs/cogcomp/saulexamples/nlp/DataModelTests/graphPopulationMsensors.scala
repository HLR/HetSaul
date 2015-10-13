package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

/** Created by Parisa on 10/2/15.
  */

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Sentence, TextAnnotation }
import edu.illinois.cs.cogcomp.saulexamples.data.{ Document, DocumentReader }
import edu.illinois.cs.cogcomp.saulexamples.nlp.sensors

import scala.collection.JavaConversions._

object graphPopulationMsensors {
  def main(args: Array[String]): Unit = {

    val dat: List[Document] = new DocumentReader("./data/20newsToy/train").docs.toList.slice(0, 2)
    val taList = dat.map(x => sensors.curator(x))
    val sentenceList = taList.flatMap(x => x.sentences())

    modelWithSensors.document populate taList
    modelWithSensors.sentence.populate(sentenceList)
    modelWithSensors.docTosen populateWith (sensors.alignment: (TextAnnotation, Sentence) => Boolean)

    val taa = modelWithSensors.document.getAllInstances
    val sen = modelWithSensors.sentence.getAllInstances
    val x1 = modelWithSensors.getFromRelation[Sentence, TextAnnotation](sen.head)
    val x2 = modelWithSensors.getFromRelation[TextAnnotation, Sentence](taa.head)

    println(s"x1.size = ${x1.size}")
    println(s"x2.size = ${x2.size}")

    print("finished")
  }

}
