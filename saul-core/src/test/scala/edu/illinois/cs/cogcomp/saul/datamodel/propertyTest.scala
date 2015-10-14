package edu.illinois.cs.cogcomp.saul.datamodel

import org.scalatest._

/** testing techniques for proprerties */
class propertyTest extends FlatSpec with Matchers {
  /** testing population of collections inside `Node` */
  "properties" should "work!" in {
    import toyDataModel._

    // TODO
    println(toyDataModel.a)
  }
}

object toyDataModel extends DataModel {

  val a = "ads"

  // boolean
  val booleanAttribute = property[toyClass]("boolean") {
    x: toyClass => true
  }

  // List[Int]
  val listIntAttributeArray = property[toyClass]("listInt") {
    x: toyClass => List(1)
  }
  val listIntAttributeGenerator = property[toyClass]("listInt", bagOfWords = true) {
    x: toyClass => List(1)
  }

  // Int
  val intAttribute = property[toyClass]("int") {
    x: toyClass => 1
  }

  // List[Double]
  val listDoubleAttributeArray = property[toyClass]("ListDouble") {
    x: toyClass => List(1.0)
  }
  val listDoubleAttributeGenerator = property[toyClass]("ListDouble", bagOfWords = true) {
    x: toyClass => List(1.0)
  }

  // Double
  val doubleAttribute = property[toyClass]("double") {
    x: toyClass => 1.0
  }

  // List[String]
  val listStringAttributeArray = property[toyClass]("listString") {
    x: toyClass => List("value")
  }
  val listStringAttributeGenerator = property[toyClass]("listString", bagOfWords = true) {
    x: toyClass => List("value")
  }

  // String
  val stringAttribute = property[toyClass]("string") {
    x: toyClass => "value"
  }

  // ranged attribute
  val rangedAttribute = property[toyClass]("funnyRange")("string") {
    x: toyClass => "value"
  }
}

class toyClass
