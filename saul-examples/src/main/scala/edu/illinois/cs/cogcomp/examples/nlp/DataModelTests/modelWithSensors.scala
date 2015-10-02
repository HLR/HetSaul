package edu.illinois.cs.cogcomp.examples.nlp.FeatureExamples.DataModelSpecificExamples
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.lfs.data_model.DataModel
import edu.illinois.cs.cogcomp.lfs.data_model.DataModel._
import edu.illinois.cs.cogcomp.lfs.data_model.edge.Edge

import scala.collection.mutable.{Map => MutableMap}

/** Created by Parisa on 10/1/15.
  */
object modelWithSensors extends DataModel {

  /** Node Types
    */
  val document = node[TextAnnotation]

  val sentence = node[Sentence]

  /** Property Types
    */

  val label = discreteAttributeOf[Constituent]('label) {
    x =>
      {
        x.getLabel
      }
  }

  val DocFeatureExample = discreteAttributeOf[TextAnnotation]('doc) {
    x: TextAnnotation =>
      {
        x.getNumberOfSentences.toString
      }
  }
  val sentenceFeatureExample = discreteAttributeOf[Sentence]('sentnce) {
    x: Sentence =>
      {
        x.getText
      }
  }

  /** Edge Types
    */

  val DocTosen = edge[TextAnnotation, Sentence]('dTos)
  val SenToCons = edge[TextAnnotation, Constituent]('tToc)

  val NODES = List(document, sentence)
  val PROPERTIES = List(DocFeatureExample, sentenceFeatureExample)
  val EDGES: List[Edge[_, _]] = DocTosen
}

