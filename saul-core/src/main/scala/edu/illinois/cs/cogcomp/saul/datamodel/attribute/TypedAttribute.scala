package edu.illinois.cs.cogcomp.saul.datamodel.attribute

import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete.BooleanAttribute

import scala.reflect.ClassTag

trait TypedAttribute[T <: AnyRef, U] extends Attribute[T] {
  val self = this

  override type S = U
  override val sensor: T => U

  implicit val tag: ClassTag[T]

  def is(u: U) = {
    val newName = name + "_is_" + u
    new BooleanAttribute[T](newName, sensor andThen (uu => u.equals(uu)))
  }
}
