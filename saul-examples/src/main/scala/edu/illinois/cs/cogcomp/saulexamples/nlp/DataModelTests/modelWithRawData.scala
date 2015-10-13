package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Sentence, TextAnnotation}
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.{Document, DocumentReader}
import edu.illinois.cs.cogcomp.saulexamples.nlp.sensors

import scala.collection.JavaConversions._

object modelWithRawData extends DataModel {

  val rawText = node[Document]
  val annotatedText = node[TextAnnotation]
  val sentences = node[Sentence]
  val rawToAnn = edge(rawText, annotatedText, 'rToa)
  val textToCon = edge(annotatedText, sentences, 'tToc)
}

object myapp {

  def main(args: Array[String]) {
    import modelWithRawData._
    //call the reader
    val dat: List[Document] = new DocumentReader("./data/20newsToy/train").docs.toList.slice(1, 3)
    //Add the reader objects to the model which contains raw text
    modelWithRawData.rawText.populate(dat)
    //populate the graph with sensors
    modelWithRawData.rawToAnn.populateWith(sensors.curator(_))
    modelWithRawData.textToCon.populateWith(sensors.f(_))

    //TODO: make the below line work, to just use the edge name and depending on the type of sensor a generator or matching edge will be called.
    //test the content of the graph
    val tests = rawText.getAllInstances
    val taa = annotatedText.getAllInstances
    val sen = sentences.getAllInstances
    //The new version
    val x3=(annotatedText(taa.head) ~> textToCon).instances
    val x4=(sentences(sen.head)~> -textToCon).instances
    //The old version
    val x1 = getFromRelation[Sentence, TextAnnotation](sen.head)
    val x2 = getFromRelation[TextAnnotation, Sentence](taa.head)

    println(s"x1.size = ${x1.size}")
    println(s"x2.size = ${x2.size}")
    println(s"x3.size = ${x3.size}")
    println(s"x4.size = ${x4.size}")
    print("finished")

  }
}