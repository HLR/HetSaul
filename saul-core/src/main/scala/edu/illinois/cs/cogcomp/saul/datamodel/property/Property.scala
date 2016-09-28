/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property

import java.util

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

import scala.reflect.ClassTag

/** Base trait for representing attributes that can be defined on a
  * [[Node]] instance.
  *
  * @tparam T Type of the attribute
  */
trait Property[T] {

  private[property] val containingPackage = "LBP_Package"
  val name: String

  val tag: ClassTag[T]
  type S

  val sensor: T => S

  def apply(instance: T): S = sensor(instance)

  def featureVector(instance: T): FeatureVector

  def outputType: String = "discrete"

  def allowableValues: Array[String] = Array.empty[String]

  def compositeChildren: Option[util.LinkedList[Classifier]] = None
}

object Property {

  /** Transfer a properties to a lbj classifier. */
  def convertToClassifier[T](property: Property[T]): Classifier = new LBPClassifier[T](property)
}
