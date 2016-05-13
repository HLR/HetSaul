package edu.illinois.cs.cogcomp.saul.parser

import edu.illinois.cs.cogcomp.lbjava.parse.Parser

class LBJavaParserToIterable[T <: AnyRef](parser: Parser) extends Iterable[T] {
  override def iterator: Iterator[T] = new parserToIterator(parser)
  private class parserToIterator(parser: Parser) extends Iterator[T] {
    override def hasNext: Boolean = if (parser.next() == null) false else true
    override def next(): T = next()
  }
}
