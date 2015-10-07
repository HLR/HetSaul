package edu.illinois.cs.cogcomp.saulexamples.nlp.FeatureExamples

import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.collection.JavaConversions._

object edisonDataModel extends DataModel {

  /** Node Types */
  val document = node[TextAnnotation] //(
  // PrimaryKey = {
  //   t:TextAnnotation => t.getId}//.hashCode().toString}
  // )

  val sentence = node[Sentence] //(
  //    PrimaryKey = {
  //      t: Sentence => t.hashCode().toString
  //   }
  //   ,
  //    SecondaryKeyMap = MutableMap(
  //      'dTos -> ((t: Sentence) => t.getSentenceConstituent.getTextAnnotation.getId)
  //    )
  //  )

  val relations = node[Relation]

  val Chunk_constituents = node[Constituent] //(//example of adding keys we can change this
  //    PrimaryKey = {
  //      t: Constituent => String.valueOf(t.hashCode)
  //    },
  //    SecondaryKeyMap = MutableMap(
  //      'sentenceId -> ((t: Constituent) => String.valueOf(t.getTextAnnotation.getId)),
  //      'addressId -> ((t: Constituent) => String.valueOf(t.getSpan.toString))
  //    )
  //  )

  /** Property Types
    */

  val label = discreteAttributeOf[Constituent]('rubish) {
    x => x.getLabel
  }

  val Eaddress = discreteAttributeOf[Constituent]('address) {
    x => x.getSpan.toString
  }

  val Eview = discreteAttributeOf[Constituent]('CviewName) {
    x => x.getViewName
  }

  val Rveiw = discreteAttributeOf[Relation]('RveiwName) {
    x: Relation => x.getSource.getViewName
  }
  val DocFeatureExample = discreteAttributeOf[TextAnnotation]('doc) {
    x: TextAnnotation => x.getNumberOfSentences.toString
  }
  val sentenceFeatureExample = discreteAttributeOf[Sentence]('sentnce) {
    x: Sentence => x.getText
  }

  /** Edge Types */
  val DocTosen = edge[TextAnnotation, Sentence]('dTos) //(PID,'dTos2)// {
  //   ta: TextAnnotation => ta.sentences()
  //}
  val SenToCons = edge[TextAnnotation, Constituent]('tToc)
  //val DocTosen=edge[TextAnnotation,Sentence](util.f: TextAnnotation => List[Sentence])('dTos)
  // val DocTosen=edge[TextAnnotation,Sentence]('dTos)

  // val SentoCons=edge[Sentence,Constituent]('sToC)//todo complete definition
  // val ConstToConst=edge[Constituent,Constituent]('cRc)(PID===PID) //TODO set connections

  // val DocTosen=edge[TextAnnotation,Sentence](util.f: TextAnnotation => List[Sentence])('dTos2)// oneToMany
}
