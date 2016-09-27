/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property

import java.util

import edu.illinois.cs.cogcomp.lbjava.classify.{Classifier, FeatureVector}
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP

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

  def outputType: String = "discrete"

  def compositeChildren: Option[util.LinkedList[Classifier]] = None
}

object Property {

  /** Transfer a properties to a lbj classifier. */
  def makeClassifier[T](property: Property[T]): Classifier = {
    new ClassifierContainsInLBP {
      name = property.name
      containingPackage = property.containingPackage

      override def getOutputType: String = property.outputType

      override def classify(o: scala.Any): FeatureVector = property.featureVector(o.asInstanceOf[T])

      override def discreteValue(o: scala.Any): String = property.featureVector(o.asInstanceOf[T]).discreteValueArray().head

      override def realValue(o: scala.Any): Double = property.featureVector(o.asInstanceOf[T]).realValueArray().head

      override def discreteValueArray(o: scala.Any): Array[String] = property.featureVector(o.asInstanceOf[T]).discreteValueArray()

      override def realValueArray(o: scala.Any): Array[Double] = property.featureVector(o.asInstanceOf[T]).realValueArray()

      override def getCompositeChildren: util.LinkedList[_] = property.compositeChildren.orNull
    }
  }

  def addToFeatureVector[T](property: Property[T], instance: T, fv: FeatureVector): FeatureVector = {
    fv.addFeatures(property.featureVector(instance))
    fv
  }
}
