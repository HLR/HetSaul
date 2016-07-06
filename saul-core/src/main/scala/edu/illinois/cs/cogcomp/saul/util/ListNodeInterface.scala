/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.util

import scala.reflect.ClassTag

class ListNodeInterface[T <: AnyRef](val l: List[T])(implicit val tag: ClassTag[T]) {

  //  def toNode: Node[T] = {
  //    toNode(PrimaryKey = { t: T => String.valueOf(t.hashCode())})
  //  }
  //
  //  def toNode(PrimaryKey: T => String)(implicit tag: ClassTag[T]): Node[T] = {
  //    toNode(PrimaryKey, t => HashMap[Symbol, String]())
  //  }
  //
  //  def toNode(PrimaryKey: T => String, SecondaryKey: T => Map[Symbol, String])(implicit tag: ClassTag[T]): Node[T] = {
  //    val combinedKeyFunction: T => Map[Symbol, String] = {
  //      t: T => SecondaryKey(t) + ('PID -> PrimaryKey(t))
  //    }
  //    new Node[T](PrimaryKey, combinedKeyFunction, tag, null) // TODO: fix this null
  //  }
}