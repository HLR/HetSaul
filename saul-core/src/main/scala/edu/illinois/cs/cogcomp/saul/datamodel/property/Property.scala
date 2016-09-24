/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, Classifier => FeatureGenerator }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP
import edu.illinois.cs.cogcomp.saul.lbjrelated.LBJClassifierEquivalent

import scala.reflect.ClassTag

/** Base trait for representing attributes that can be defined on a
  * [[Node]] instance.
  *
  * @tparam T Type of the attribute
  */
trait Property[T] {

  val containingPackage = "LBP_Package"
  val name: String

  val tag: ClassTag[T]
  type S

  val sensor: T => S

  def apply(instance: T): S = sensor(instance)

  def featureVector(instance: T): FeatureVector

  def value(instance: T): Option[T] = None

  private val classifier = makeClassifierWithName(name)

  def addToFeatureVector(instance: T, featureVector: FeatureVector): FeatureVector = {
    featureVector.addFeatures(this.classifier.classify(instance))
    featureVector
  }

  def addToFeatureVector(instance: T, featureVector: FeatureVector, nameOfClassifier: String): FeatureVector = {
    featureVector.addFeatures(makeClassifierWithName(nameOfClassifier).classify(instance))
    featureVector
  }

  final def makeClassifierWithName(n: String): FeatureGenerator = {
    new ClassifierContainsInLBP {
      name = n

      override def getOutputType: String = {
        tag match {
          case discrete: ClassTag[String] => "discrete"
          case discreteArray: ClassTag[List[String]] => "discrete%"
          case _ => "discrete"
        }
      }

      override def classify(o: scala.Any): FeatureVector = featureVector(o.asInstanceOf[T])

      override def discreteValue(o: scala.Any): String = featureVector(o.asInstanceOf[T]).discreteValueArray().head

      override def realValue(o: scala.Any): Double = featureVector(o.asInstanceOf[T]).realValueArray().head

      override def discreteValueArray(o: scala.Any): Array[String] = featureVector(o.asInstanceOf[T]).discreteValueArray()

      override def realValueArray(o: scala.Any): Array[Double] = featureVector(o.asInstanceOf[T]).realValueArray()
    }
  }
}

object Property {

  /** Transfer a list of properties to a lbj classifier. */
  def entitiesToLBJFeature[T](atts: Property[T]): FeatureGenerator = {
    atts.classifier
  }
}