package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors

object modelWithSensors extends DataModel {

  /** Node Types */
  val rawDocument = node[Document]
  val document = node[TextAnnotation]
  val sentence = node[Sentence]
  val constituent = node[Constituent]

  /** Property Types */
  val label = property(constituent, "label") {
    x: Constituent => x.getLabel
  }

  val docFeatureExample = property(document, "doc") {
    x: TextAnnotation => x.getNumberOfSentences.toString
  }

  val sentenceFeatureExample = property(sentence, "sentence") {
    x: Sentence => x.getText
  }

  /** Edge Types */
  val docToSen = edge(document, sentence)
  docToSen.addSensor(CommonSensors.getSentences _)
  val senToCons = edge(document, constituent)
  senToCons.addSensor(CommonSensors.textAnnotationToTokens _)
}

