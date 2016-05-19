package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation
import edu.illinois.cs.cogcomp.saulexamples.data.DocumentReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests.modelWithSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors

import scala.collection.JavaConversions._

object graphPopulationGsensors {

  def main(args: Array[String]): Unit = {

    val data = new DocumentReader("./data/20newsToy/train").docs.toList.slice(1, 3)
    val taList = data.map(CommonSensors.annotateWithCurator)

    // populate
    // this should populate everything
    document.populate(taList)

    // The below line uses a generator sensor
    modelWithSensors.docToSen populateWith (CommonSensors.getSentences(_))
    modelWithSensors.docToSen populateWith ((x: TextAnnotation) => CommonSensors.getSentences(x).headOption)
    //TODO: make the below line work, to just use the edge name and depending on the type of sensor a generator or matching edge will be called.
    //EdisonDataModel.populateWith(EdisonDataModel.DocTosen)

    val x1 = sentence() ~> -docToSen
    val x2 = document() ~> docToSen

    println(s"x1.size = ${x1.size}")
    println(s"x2.size = ${x2.size}")

    print("finished")
  }
}
