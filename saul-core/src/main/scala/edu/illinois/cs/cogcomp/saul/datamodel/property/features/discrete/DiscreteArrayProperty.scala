/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscreteArrayStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty

import scala.reflect.ClassTag

case class DiscreteArrayProperty[T <: AnyRef](name: String, sensor: T => List[String])(implicit val tag: ClassTag[T]) extends TypedProperty[T, List[String]] {

  override def featureVector(instance: T): FeatureVector = {
    val values = sensor(instance)

    var __result: FeatureVector = null
    __result = new FeatureVector

    values.zipWithIndex.foreach {
      case (__value, __featureIndex) => __result.addFeature(new DiscreteArrayStringFeature(this.containingPackage, this.name, "", __value, (-1).toShort, 0.toShort, __featureIndex, 0))
    }

    (0 to values.size) foreach { x => __result.getFeature(x).setArrayLength(values.size) }
    __result
  }
}
