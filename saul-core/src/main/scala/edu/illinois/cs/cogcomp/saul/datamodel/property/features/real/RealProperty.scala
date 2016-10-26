/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ RealPrimitiveStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty

import scala.reflect.ClassTag

case class RealProperty[T <: AnyRef](name: String, sensor: T => Double)(implicit val tag: ClassTag[T]) extends TypedProperty[T, Double] {

  override def outputType: String = "real"

  override def featureVector(instance: T): FeatureVector = {
    val result: Double = sensor(instance)
    new FeatureVector(new RealPrimitiveStringFeature(containingPackage, name, "", result))
  }
}
