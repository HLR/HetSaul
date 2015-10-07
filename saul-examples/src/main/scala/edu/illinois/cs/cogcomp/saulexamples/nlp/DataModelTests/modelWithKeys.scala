package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.collection.mutable.{ Map => MutableMap }

/** Created by Parisa on 10/4/15.
  */

object modelWithKeys extends DataModel {

  /** Node Types
    */
  val document = node[TextAnnotation](
    PrimaryKey = {
    t: TextAnnotation => t.getId
  }
  )

  val sentence = node[Sentence](
    PrimaryKey = {
    t: Sentence => t.hashCode().toString
  },
    SecondaryKeyMap = MutableMap(
      'dTos -> ((t: Sentence) => t.getSentenceConstituent.getTextAnnotation.getId)
    )
  )
  /** Property Types
    */
  val label = discreteAttributeOf[Constituent]('label) {
    x =>
      {
        x.getLabel
      }
  }

  val docFeatureExample = discreteAttributeOf[TextAnnotation]('doc) {
    x: TextAnnotation =>
      {
        x.getNumberOfSentences.toString
      }
  }
  val sentenceFeatureExample = discreteAttributeOf[Sentence]('sentence) {
    x: Sentence =>
      {
        x.getText
      }
  }

  /** Edge Types
    */
  val docTosen = edge[TextAnnotation, Sentence]('dTos)

}
