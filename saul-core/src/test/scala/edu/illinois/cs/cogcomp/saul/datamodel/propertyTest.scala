/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.property.PairwiseConjunction
import org.scalatest._

/** testing techniques for properties */
class propertyTest extends FlatSpec with Matchers {
  /** testing population of collections inside `Node` */
  "properties" should "work!" in {
    import toyDataModel._

    // boolean
    booleanProperty(new toyClass).mkString should be("true")

    // discrete
    stringProperty(new toyClass).mkString should be("value")
    listStringPropertyArray(new toyClass).mkString should be("listValue")
    listStringPropertyGenerator(new toyClass).mkString should be("listValue")

    // ranged
    rangedProperty(new toyClass).mkString should be("ranged")

    // Double
    doubleProperty(new toyClass) should be(1.0)
    listDoublePropertyGenerator(new toyClass).mkString should be("1.02.0")
    listDoublePropertyArray(new toyClass).mkString should be("1.02.0")

    // Int
    intProperty(new toyClass) should be(2.0)
    listIntPropertyArray(new toyClass) should be(List(1.0, 3.0))
    listIntPropertyGenerator(new toyClass) should be(List(1.0, 3.0))

    // Test cached properties (calling them multiple times)
    stringPropertyWithCache(new toyClass).mkString should be("cachedValue")
    stringPropertyWithCache(new toyClass).mkString should be("cachedValue")

    conjunctionProperty(new toyClass).mkString(",") should be("string_value_funnyRange_ranged")
    conjunctionProperty1(new toyClass).size should be(3)
    conjunctionProperty1(new toyClass).mkString(",") should be("string_value_funnyRange_ranged,string_value_boolean_true,funnyRange_ranged_boolean_true")
  }

  "properties" should "use reasonable default names if not specified" in {
    object testModel extends DataModel {
      val n = node[String]

      val p1 = property(n) { s: String => s.length }
      val p2 = property(n) { s: String => s.length == 2 }
      val p3 = property(n) { s: String => s.take(1) }

      Set(p1.name, p2.name, p3.name).size should be(3)
    }
  }
}

object toyDataModel extends DataModel {

  val toys = node[toyClass]

  // boolean
  val booleanProperty = property(toys, "boolean") {
    x: toyClass => true
  }

  // List[Int]
  val listIntPropertyArray = property(toys, "listInt") {
    x: toyClass => List(1, 3)
  }
  val listIntPropertyGenerator = property(toys, "listInt", cache = false, ordered = true) {
    x: toyClass => List(1, 3)
  }

  // Int
  val intProperty = property(toys, "int") {
    x: toyClass => 2
  }

  // List[Double]
  val listDoublePropertyArray = property(toys, "listDouble") {
    x: toyClass => List(1.0, 2.0)
  }
  val listDoublePropertyGenerator = property(toys, "listDouble", cache = false, ordered = true) {
    x: toyClass => List(1.0, 2.0)
  }

  // Double
  val doubleProperty = property(toys, "double") {
    x: toyClass => 1.0
  }

  // List[String]
  val listStringPropertyArray = property(toys, "listString") {
    x: toyClass => List("listValue")
  }
  val listStringPropertyGenerator = property(toys, "listString", cache = false, ordered = true) {
    x: toyClass => List("listValue")
  }

  // String
  val stringProperty = property(toys, "string") {
    x: toyClass => "value"
  }

  val stringPropertyWithCache = property(toys, "string", cache = true) {
    x: toyClass => "cachedValue"
  }

  // ranged property
  val rangedProperty = property(toys, "funnyRange")("string") {
    x: toyClass => "ranged"
  }
  val conjunctionProperty = property(toys) {
    x: toyClass => PairwiseConjunction(List(stringProperty, rangedProperty), x)
  }
  val conjunctionProperty1 = property(toys) {
    x: toyClass => PairwiseConjunction(List(stringProperty, rangedProperty, booleanProperty), x)
  }
}

class toyClass