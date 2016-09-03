/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.parser

import edu.illinois.cs.cogcomp.lbjava.parse.{ FoldSeparator, Parser }

/** a utility class to convert LBJava parser to Scala iterator */
class LBJavaParserToIterable[T <: AnyRef](parser: Parser) extends Iterable[T] {
  override def iterator(): Iterator[T] = new parserToIterator(parser)

  private class parserToIterator(parser: Parser) extends Iterator[T] {
    val nextBuffer = new collection.mutable.Stack[AnyRef]

    override def hasNext: Boolean = {
      val nextElement = parser.next()
      if (nextElement == null || nextElement == FoldSeparator.separator) {
        false
      } else {
        nextBuffer.push(nextElement)
        true
      }
    }

    override def next(): T = nextBuffer.pop().asInstanceOf[T]
  }
}
