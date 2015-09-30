package edu.illinois.cs.cogcomp.lfs.data_model.attribute

import edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.discrete.BooleanAttribute

import scala.reflect.ClassTag

/** Created by haowu on 1/27/15.
  */
trait TypedAttribute[T <: AnyRef, U] extends Attribute[T] {
  val fdt = this

  override type S = U
  override val mapping: T => U

  implicit val tag: ClassTag[T]

  def is(u: U) = {
    val newName = name + "_is_" + u
    new BooleanAttribute[T](newName, mapping andThen (uu => u.equals(uu)))

  }

}
