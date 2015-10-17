package edu.illinois.cs.cogcomp.saul.datamodel.attribute

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete.{ BooleanAttribute, DiscreteAttribute }

import scala.reflect.ClassTag

class EvaluatedAttribute[T <: AnyRef, U](
  val attribute: TypedAttribute[T, U],
  val value: U
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, String] {

  val name = attribute.name + "_is_" + value

  val boolMapping: (T) => Boolean = {
    t: T => attribute.sensor(t).equals(this.value)
  }

  val typedAttributes = new BooleanAttribute[T](name, this.boolMapping)

  override def makeClassifierWithName(n: String): Classifier = {
    typedAttributes.makeClassifierWithName(n)
  }

  override def addToFeatureVector(instance: T, featureVector: FeatureVector): FeatureVector = featureVector

  def addToFeatureVector(instance: T, featureVector: FeatureVector, nameOfClassifier: String) = featureVector
  override val sensor: (T) => String = { t: T => "" }
}
