package edu.illinois.cs.cogcomp.saul.parser

import edu.illinois.cs.cogcomp.lbjava.parse.Parser

/** Created by haowu on 12/14/14.
  * Email : haowu@haowu.me
  */
class LBJIteratorParserScala[T <: AnyRef](val data: Iterable[T]) extends Parser {

  private var it: Option[Iterator[T]] = None

  override def next(): AnyRef = {
    it match {
      case Some(i) => {
        if (i.hasNext) {
          i.next().asInstanceOf[AnyRef]
        } else {
          null
        }

      }

      case None => { null }
    }
  }

  override def close(): Unit = {

  }

  override def reset(): Unit = {
    if (data != null) {
      this.it = Some(data.iterator)
    }
  }
}
