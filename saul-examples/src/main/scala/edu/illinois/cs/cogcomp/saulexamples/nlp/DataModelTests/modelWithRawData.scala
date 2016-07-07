/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Sentence, TextAnnotation }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.{ Document, DocumentReader }
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors

import scala.collection.JavaConversions._

object modelWithRawData {

  val model = new DataModel {
    /** Nodes */
    val rawText = node[Document]
    val annotatedText = node[TextAnnotation]
    val sentences = node[Sentence]

    /** Edges */
    val rawToAnn = edge(rawText, annotatedText)
    val textToCon = edge(annotatedText, sentences)
    textToCon.addSensor(CommonSensors.getSentences _)
    rawToAnn.addSensor(CommonSensors.annotateWithCurator _)

    /** Properties */
    val docFeatureExample = property(annotatedText, "doc") {
      x: TextAnnotation => x.getNumberOfSentences.toString
    }
  }

  import model._

  def main(args: Array[String]) {
    /** call the reader */
    val dat: List[Document] = new DocumentReader("./data/20newsToy/train").docs.toList.slice(1, 2)
    // val taList = dat.map(x => sensors.curator(x))
    // val sentenceList = taList.flatMap(x => x.sentences())

    //Add the reader objects to the model which contains raw text
    rawText.populate(dat)
    // annotatedText.populate(taList)
    //sentences.populate(sentenceList)

    //populate the graph with sensors
    // modelWithRawData.rawToAnn.populateWith(sensors.curator(_))
    // modelWithRawData.textToCon.populateWith(sensors.f _)
    //test the content of the graph
    val tests = rawText.getAllInstances
    val taa = annotatedText.getAllInstances
    val sen = sentences.getAllInstances
    println(s"tests.size = ${tests.size}")
    println(s"taa.size = ${taa.size}")
    println(s"sen.size = ${sen.size}")
    println(s"textToCon.size = ${textToCon.links.size}")
    println(s"rawToAnn.size = ${rawToAnn.links.size}")
    //The new version
    val x0 = (rawText(tests.head) ~> rawToAnn ~> textToCon).instances
    val x3 = (annotatedText(taa.head) ~> textToCon).instances
    val x4 = (sentences(sen.head) ~> -textToCon).instances

    // TODO: refine: This is a filter based on a specific property
    val a = annotatedText(taa.head).filter(docFeatureExample(_).equals("1"))

    //TODO: refien:  This is what we want to do by writing some thing like annotatedText(taa).docFeatureExample,
    // it means applying the property on a collection.
    val b = annotatedText(taa).instances.map(x => docFeatureExample(x))

    //TODO This is applying an aggregation function on the outcome of the collection property, should be doen easier
    val c = annotatedText(taa).instances.map(x => docFeatureExample(x)).mkString("_")
    //TODO Getting the neighbors of a node: This is challenging because the graph is heterogeneous unless the neighbors are always from same type.

    //val d= annotatedText(taa) ~> *
    //TODO Getting neighbors of a node within a specific distance forward and/or backward:

    //val d= annotated(taa)~> edgename(2).propertyx

    //TODO Getting neighbors of a node within a specific window

    //val d= annotated(taa)~> edgename(2,-2).propertyx

    //this gives the concatination of the edgenames that connect x to y if any

    //TODO Getting the path between two nodes

    //val d= node(x).path(node(y))

    println(s"x0.size = ${x0.size}")
    println(s"x3.size = ${x3.size}")
    println(s"x4.size = ${x4.size}")
    print("finished")
  }
}