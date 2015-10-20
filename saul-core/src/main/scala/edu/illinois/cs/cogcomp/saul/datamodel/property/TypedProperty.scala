package edu.illinois.cs.cogcomp.saul.datamodel.property

import edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete.BooleanProperty

import scala.reflect.ClassTag

trait TypedProperty[T <: AnyRef, U] extends Property[T] {
  val self = this

  override type S = U
  override val sensor: T => U

  implicit val tag: ClassTag[T]

  def is(u: U) = {
    val newName = name + "_is_" + u
    new BooleanProperty[T](newName, sensor andThen (uu => u.equals(uu)))
  }
}
