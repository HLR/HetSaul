/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property

import java.util

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, FeatureVector }

import scala.reflect.ClassTag

case class CombinedDiscreteProperty[T <: AnyRef](atts: List[Property[T]])(implicit val tag: ClassTag[T]) extends TypedProperty[T, List[_]] {

  override val sensor: (T) => List[_] = {
    t: T => atts.map(att => att.sensor(t))
  }

  val name = "combined++" + atts.map(x => { x.name }).mkString("+")

  val packageName = "LBP_Package"

  override def outputType: String = "mixed%"

  override def featureVector(instance: T): FeatureVector = {
    val featureVector = new FeatureVector()
    atts.foreach(property => featureVector.addFeatures(property.featureVector(instance)))
    featureVector
  }

  override def compositeChildren: Option[util.LinkedList[Classifier]] = {
    val result: util.LinkedList[Classifier] = new util.LinkedList[Classifier]()

    // TODO:bhargav - Check if we still need to do this.
    atts.foreach(x => result.add(Property.convertToClassifier(x)))
    Some(result)
  }
}
