package edu.illinois.cs.cogcomp.saul.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.real.{ RealAttributeCollection, RealAttribute }
import org.scalatest._

/** testing techniques for proprerties */
class propertyTest extends FlatSpec with Matchers {
  /** testing population of collections inside `Node` */
  "properties" should "work!" in {
    import toyDataModel._

    booleanAttribute.classifier.classify(new toyClass).discreteValueArray().mkString should be("true")

    stringAttribute.classifier.classify(new toyClass).discreteValueArray().mkString should be("value")
    //    listStringAttributeArray.makeClassifierWithName("newClassifier").classify(new toyClass).realValueArray().mkString should be("1.02.0")
    //    listStringAttributeGenerator.makeClassifierWithName("newClassifier").classify(new toyClass).realValueArray().mkString should be("1.02.0")
    //
    //    listStringAttributeArray.classifier.classify(new toyClass).discreteValueArray().mkString should be("value")
    //    listStringAttributeGenerator.classifier.classify(new toyClass).discreteValueArray().mkString should be("value")

    // ranged
    rangedAttribute.classifier.classify(new toyClass).discreteValueArray().mkString should be("ranged")

    // Double
    doubleAttribute.classifier.classify(new toyClass).realValueArray().mkString should be("1.0")
    listDoubleAttributeGenerator.makeClassifierWithName("newClassifier").classify(new toyClass).realValueArray().mkString should be("1.02.0")
    listDoubleAttributeArray.makeClassifierWithName("newClassifier").classify(new toyClass).realValueArray().mkString should be("1.02.0")

    // Int
    intAttribute.classifier.classify(new toyClass).realValueArray().mkString should be("2.0")
    listIntAttributeArray.classifier.classify(new toyClass).realValueArray().mkString should be("1.03.0")
    listIntAttributeGenerator.classifier.classify(new toyClass).realValueArray().mkString should be("1.03.0")

  }
}

object toyDataModel extends DataModel {

  // boolean
  val booleanAttribute = property[toyClass]("boolean") {
    x: toyClass => true
  }

  // List[Int]
  val listIntAttributeArray = property[toyClass]("listInt") {
    x: toyClass => List(1, 3)
  }
  val listIntAttributeGenerator = property[toyClass]("listInt", ordered = true) {
    x: toyClass => List(1, 3)
  }

  // Int
  val intAttribute = property[toyClass]("int") {
    x: toyClass => 2
  }

  // List[Double]
  val listDoubleAttributeArray = property[toyClass]("listDouble") {
    x: toyClass => List(1.0, 2.0)
  }
  val listDoubleAttributeGenerator = property[toyClass]("listDouble", ordered = true) {
    x: toyClass => List(1.0, 2.0)
  }
  val listDoubleAttributeArrayOld = realAttributesArrayOf[toyClass]('listDouble) {
    x: toyClass => List(1.0, 2.0)
  }
  val listDoubleAttributeGeneratorOld = realAttributesGeneratorOf[toyClass]('listDouble) {
    x: toyClass => List(1.0, 2.0)
  }

  // Double
  val doubleAttribute = property[toyClass]("double") {
    x: toyClass => 1.0
  }

  // List[String]
  val listStringAttributeArray = property[toyClass]("listString") {
    x: toyClass => List("listValue")
  }
  val listStringAttributeGenerator = property[toyClass]("listString", ordered = true) {
    x: toyClass => List("listValue")
  }

  // String
  val stringAttribute = property[toyClass]("string") {
    x: toyClass => "value"
  }

  // ranged attribute
  val rangedAttribute = property[toyClass]("funnyRange")("string") {
    x: toyClass => "ranged"
  }
}

class toyClass

