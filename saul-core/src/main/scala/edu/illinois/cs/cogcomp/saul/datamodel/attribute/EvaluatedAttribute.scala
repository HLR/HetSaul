package edu.illinois.cs.cogcomp.saul.datamodel.attribute

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete.{ BooleanAttribute, DiscreteAttribute }

import scala.reflect.ClassTag

/** Created by haowu on 1/27/15.
  */
class EvaluatedAttribute[T <: AnyRef, U](
  val att: TypedAttribute[T, U],
  val value: U
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, String] {

  val name = att.name + "_is_" + value

  val boolMapping: (T) => Boolean = {
    t: T =>
      if (att.sensor(t).equals(this.value)) {
        true
      } else {
        false
      }
  }

  // TODO: find a better name
  val typedAttributes = new BooleanAttribute[T](name, this.boolMapping)

  override def makeClassifierWithName(n: String): Classifier = {
    val x = typedAttributes.makeClassifierWithName(n)
    x
  }

  // TODO : implement
  override def addToFeatureVector(t: T, fv: FeatureVector): FeatureVector = fv

  def addToFeatureVector(t: T, fv: FeatureVector, nameOfClassifier: String): FeatureVector = {
    fv
  }
  override val sensor: (T) => String = { t: T => "" }
}
