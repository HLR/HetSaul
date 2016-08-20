/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete

import scala.reflect.ClassTag

/** Represents a boolean attribute on a [[edu.illinois.cs.cogcomp.saul.datamodel.node.Node]] instance.
  */
class BooleanProperty[T <: AnyRef](override val name: String, sensor: T => Boolean)(implicit val t: ClassTag[T])
  extends DiscreteProperty[T](name, { t: T => sensor(t).toString }, Some("true" :: "false" :: Nil))
