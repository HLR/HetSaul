package edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete

import scala.reflect.ClassTag

class BooleanProperty[T <: AnyRef](
  override val name: String,
  sensor: T => Boolean
)(implicit val t: ClassTag[T]) extends DiscreteProperty[T](name, { t: T => sensor(t).toString }, Some("true" :: "false" :: Nil))

