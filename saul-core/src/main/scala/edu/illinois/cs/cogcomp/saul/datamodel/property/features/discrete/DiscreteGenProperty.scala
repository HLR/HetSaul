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

class DiscreteGenProperty[T <: AnyRef](val name: String, val sensor: T => List[String])(implicit val tag: ClassTag[T]) extends TypedProperty[T, List[String]] {

  override def featureVector(instance: T): FeatureVector = {
    val values = sensor(instance)

    var __result: FeatureVector = null
    __result = new FeatureVector

    values foreach (
      x => {
        __result.addFeature(new DiscretePrimitiveStringFeature(this.containingPackage, this.name, x, x, values.indexOf(x).toShort, 0.toShort))
      }
    )

    __result
  }
}
