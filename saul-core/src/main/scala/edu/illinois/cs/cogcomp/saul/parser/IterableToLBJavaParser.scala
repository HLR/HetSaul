/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.parser

import edu.illinois.cs.cogcomp.lbjava.parse.Parser

/** Converts an instance of [[Iterable]] to LBJava's [[Parser]] class
  * @param data Input Iterable data
  * @tparam T Type for the data processed
  */
class IterableToLBJavaParser[T <: AnyRef](val data: Iterable[T]) extends Parser {
  private var it: Option[Iterator[T]] = None

  override def next(): AnyRef = {
    it match {
      case Some(i) =>
        if (i.hasNext) {
          i.next().asInstanceOf[AnyRef]
        } else {
          null
        }
      case None => null
    }
  }

  override def close(): Unit = {}

  override def reset(): Unit = {
    if (data != null) this.it = Some(data.iterator)
  }
}
