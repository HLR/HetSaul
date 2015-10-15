package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete

import scala.reflect.ClassTag

class BooleanAttribute[T <: AnyRef](
  override val name: String,
  sensor: T => Boolean
)(implicit val t: ClassTag[T]) extends DiscreteAttribute[T](name, { t: T => sensor(t).toString }, Some("true" :: "false" :: Nil))

