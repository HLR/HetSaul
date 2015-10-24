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
  val label = discretePropertyOf[Constituent]('label) {
    x => x.getLabel
  }

  val docFeatureExample = discretePropertyOf[TextAnnotation]('doc) {
    x: TextAnnotation => x.getNumberOfSentences.toString
  }
  val sentenceFeatureExample = discretePropertyOf[Sentence]('sentnce) {
    x: Sentence => x.getText
  }

  /** Edge Types */
  val docTosen = edge(document, sentence, 'dTos)
  val SenToCons = edge(document, constituent, 'tToc)
}

