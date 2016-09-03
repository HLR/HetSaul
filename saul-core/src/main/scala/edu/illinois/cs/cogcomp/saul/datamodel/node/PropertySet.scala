/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.node

import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty

import scala.collection.mutable.ArrayBuffer

trait PropertySet[T <: AnyRef, V] extends Iterable[V] {
  self =>
  def property: TypedProperty[T, V]
  def underlying: InstanceSet[T]
  lazy val propValues: Iterable[V] = {
    val ab = new ArrayBuffer[V]
    ab ++= underlying.map(property(_))
    ab
  }

  override def iterator: Iterator[V] = propValues.iterator

  def counts = propValues.groupBy(x => x).map(p => p._1 -> p._2.size)
}

