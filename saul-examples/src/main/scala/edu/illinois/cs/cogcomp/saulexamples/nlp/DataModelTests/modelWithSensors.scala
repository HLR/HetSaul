package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.Document

import scala.collection.mutable.{ Map => MutableMap }

object modelWithSensors extends DataModel {

  /** Node Types */
  val rawDocument = node[Document]
  val document = node[TextAnnotation]
  val sentence = node[Sentence]
  val constituent = node[Constituent]

  /** Property Types */
  val label = property[Constituent]("label") {
    x: Constituent => x.getLabel
  }

  val docFeatureExample = property[TextAnnotation]("doc") {
    x: TextAnnotation => x.getNumberOfSentences.toString
  }

  val sentenceFeatureExample = property[Sentence]("sentnce") {
    x: Sentence => x.getText
  }

  /** Edge Types */
  val docTosen = edge(document, sentence)
  val SenToCons = edge(document, constituent)
}

