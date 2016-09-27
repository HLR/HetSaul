/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ RealArrayStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty

import scala.reflect.ClassTag

/** Represents real valued array attributes.
  *
  * @param name Name of the property
  * @param sensor Sensor function used to generate attributes from nodes.
  * @param tag ClassTag for the type of data stored by the attribute node
  * @tparam T Type of the node that this property is associated with.
  */
case class RealArrayProperty[T <: AnyRef](name: String, sensor: T => List[Double])(implicit val tag: ClassTag[T])
  extends RealPropertyCollection[T] with TypedProperty[T, List[Double]] {

  override def outputType: String = "real%"

  override def featureVector(instance: T): FeatureVector = {
    val values = sensor(instance)

    val featureVector = new FeatureVector

    values.zipWithIndex.foreach {
      case (value, idx) => featureVector.addFeature(new RealArrayStringFeature(this.containingPackage, this.name, "", value, idx, 0))
    }

    // TODO: Commented by Daniel. Make sure this does not create any bugs
    //(0 to values.size) foreach { x => featureVector.getFeature(x).setArrayLength(values.size) }

    featureVector
  }
}
