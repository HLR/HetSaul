package edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.discrete

import scala.reflect.ClassTag

/** Created by haowu on 2/5/15.
  */
class BooleanAttribute[T <: AnyRef](
  override val name: String,
  p: T => Boolean
)(implicit val t: ClassTag[T]) extends DiscreteAttribute[T](name, { t: T => p(t).toString }, Some("true" :: "false" :: Nil))

