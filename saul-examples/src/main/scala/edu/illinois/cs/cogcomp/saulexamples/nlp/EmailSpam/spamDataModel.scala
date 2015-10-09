package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.Document

import scala.collection.JavaConversions._

object spamDataModel extends DataModel {

  val docs = node[Document]

  val wordFeature = discreteAttributesGeneratorOf[Document]('wordF) {
    x: Document => x.getWords.toList
  }

  val bigramFeature = discreteAttributesGeneratorOf[Document]('bigram) {
    x: Document =>
      val words = x.getWords.toList
      /** bigram features */
      words.sliding(2).map(_.mkString("-")).toList
  }

  val spamLable = discreteAttributeOf[Document]('label) {
    x: Document => x.getLabel
  }

  /** sample properties defined, just for demonstraction of the idea
    * Will be removed at the time of merging the PR.
    */

  // boolean
  val booleanAttribute = property[Document]('boolean) {
    x: Document => true
  }
  val booleanAttributeOldWay = booleanAttributeOf[Document]('boolean) {
    x: Document => true
  }

  // List[Int]
  val listIntAttribute = property[Document]('boolean) {
    x: Document => List(1)
  }
  val listIntAttributeOldWay = intAttributesGeneratorOf[Document]('boolean) {
    x: Document => List(1)
  }

  // Int
  val intAttribute = property[Document]('boolean) {
    x: Document => 1
  }
  val intAttributeOldWay = intAttributeOf[Document]('boolean) {
    x: Document => 1
  }

  // List[Double]
  val listDoubleAttribute = property[Document]('boolean) {
    x: Document => List(1.0)
  }
  val listDoubleAttributeOldWay = realAttributesGeneratorOf[Document]('boolean) {
    x: Document => List(1.0)
  }


  // Double
  val doubleAttribute = property[Document]('boolean) {
    x: Document => 1.0
  }
  val doubleAttributeOldWay = realAttributeOf[Document]('boolean) {
    x: Document => 1.0
  }


  // List[String]
  val listStringAttribute = property[Document]('boolean) {
    x: Document => List("value")
  }
  val listStringAttributeOldWay = discreteAttributesArrayOf[Document]('boolean) {
    x: Document => List("value")
  }

  // String
  val stringAttribute = property[Document]('boolean) {
    x: Document => "value"
  }
  val stringAttributeOldWay = discreteAttributeOf[Document]('boolean) {
    x: Document => "value"
  }

}
