/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscretePrimitiveStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty

import scala.reflect.ClassTag

case class DiscreteProperty[T <: AnyRef](name: String, sensor: T => String, range: Option[List[String]])(implicit val tag: ClassTag[T])
  extends TypedProperty[T, String] {

  override def allowableValues: Array[String] = range.map(_.toArray[String]).getOrElse(Array.empty[String])

  override def featureVector(instance: T): FeatureVector = {
    range match {
      case Some(rangeValue) =>
        val result: String = sensor(instance)
        new FeatureVector(new DiscretePrimitiveStringFeature(containingPackage, this.name, "", result,
          rangeValue.indexOf(result).toShort, rangeValue.length.toShort))
      case None =>
        val result: String = sensor(instance)
        new FeatureVector(new DiscretePrimitiveStringFeature(containingPackage, this.name, "", result, 0, 0))
    }
  }
}
