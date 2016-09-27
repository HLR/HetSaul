/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ RealPrimitiveStringFeature, RealArrayStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty

import scala.reflect.ClassTag

case class RealCollectionProperty[T <: AnyRef](name: String, sensor: T => List[Double], ordered: Boolean)(implicit val tag: ClassTag[T]) extends RealPropertyCollection[T] with TypedProperty[T, List[Double]] {

  override def outputType: String = "real%"

  override def featureVector(instance: T): FeatureVector = {
    val values = sensor(instance)
    val featureVector = new FeatureVector

    if (ordered) {
      values.zipWithIndex.foreach {
        case (value, idx) =>
          featureVector.addFeature(new RealArrayStringFeature(
            containingPackage,
            name, "", value, idx, 0
          ))
      }
      // TODO: commented by Daniel. Make sure this does not introduce any bugs
      //(0 to values.size) foreach { x => featureVector.getFeature(x).setArrayLength(values.size) }
    } else {
      values.zipWithIndex.foreach {
        case (value, idx) =>
          featureVector.addFeature(new RealPrimitiveStringFeature(
            containingPackage,
            this.name, idx + "", value
          ))
      }
    }

    featureVector
  }
}
