/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, RealPrimitiveStringFeature }
import edu.illinois.cs.cogcomp.saul.datamodel.property.{ Property, TypedProperty }

import scala.reflect.ClassTag

trait RealPropertyCollection[T <: AnyRef] extends Property[T]

case class RealGenProperty[T <: AnyRef](name: String, sensor: T => List[Double])(implicit val tag: ClassTag[T]) extends RealPropertyCollection[T] with TypedProperty[T, List[Double]] {

  override def outputType: String = "real%"

  override def featureVector(instance: T): FeatureVector = {
    val values = sensor(instance)

    val featureVector = new FeatureVector

    values.zipWithIndex.foreach {
      case (value, idx) => featureVector.addFeature(new RealPrimitiveStringFeature(this.containingPackage, this.name, idx + "", value))
    }

    featureVector
  }
}
