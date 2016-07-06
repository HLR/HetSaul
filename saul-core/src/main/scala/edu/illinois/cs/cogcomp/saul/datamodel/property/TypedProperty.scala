/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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
