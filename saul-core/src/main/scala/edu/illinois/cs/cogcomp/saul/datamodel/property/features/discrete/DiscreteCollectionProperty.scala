/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscretePrimitiveStringFeature, DiscreteArrayStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty

import scala.reflect.ClassTag

case class DiscreteCollectionProperty[T <: AnyRef](name: String, sensor: T => List[String], ordered: Boolean)(implicit val tag: ClassTag[T]) extends TypedProperty[T, List[String]] {

  override def featureVector(instance: T): FeatureVector = {
    val values = sensor(instance)

    val featureVector = new FeatureVector

    if (ordered) {
      values.zipWithIndex.foreach {
        case (value, idx) =>
          featureVector.addFeature(
            new DiscreteArrayStringFeature(
              this.containingPackage,
              this.name, "", value, (-1).toShort, 0.toShort, idx, 0
            )
          )
      }
      // TODO: Daniel commented this line. Make sure this does not introduce a bug
      //(0 to values.size) foreach { x => featureVector.getFeature(x).setArrayLength(values.size) }
    } else {
      values.foreach {
        value =>
          val id = value
          featureVector.addFeature(new DiscretePrimitiveStringFeature(
            this.containingPackage,
            this.name, id, value, (-1).toShort, 0.toShort
          ))
      }
    }
    featureVector
  }
}
