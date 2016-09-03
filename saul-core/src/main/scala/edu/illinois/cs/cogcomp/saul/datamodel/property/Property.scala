/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, Classifier => FeatureGenerator }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.lbjrelated.LBJClassifierEquivalent

import scala.reflect.ClassTag

/** Base trait for representing attributes that can be defined on a
  * [[Node]] instance.
  *
  * @tparam T Type of the attribute
  */
trait Property[T] extends LBJClassifierEquivalent {

  val name: String

  val tag: ClassTag[T]
  type S

  val sensor: T => S

  def apply(instance: T): S = {
    sensor(instance)
  }

  val classifier = makeClassifierWithName(name)

  def addToFeatureVector(instance: T, featureVector: FeatureVector): FeatureVector

  def addToFeatureVector(instance: T, featureVector: FeatureVector, nameOfClassifier: String): FeatureVector

  def makeClassifierWithName(n: String): FeatureGenerator
}

object Property {

  /** Transfer a list of properties to a lbj classifier. */
  def entitiesToLBJFeature[T](atts: Property[T]): FeatureGenerator = {
    atts.classifier
  }
}