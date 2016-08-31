/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.node

import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge

import scala.collection.mutable

/** @author sameer
  * @since 1/17/16.
  */
object Path {

  type Path[S <: AnyRef, T <: AnyRef] = ((S, T), Edge[S, T])

  case class State[S <: AnyRef](s: S, snode: Node[S], t: AnyRef, maxLength: Int = 10, curr: Seq[Path[AnyRef, AnyRef]] = Seq.empty)

  def findPath[S <: AnyRef](state: State[S], queue: mutable.Queue[State[_ <: AnyRef]]): Option[Seq[Path[AnyRef, AnyRef]]] = {
    import state._
    if (maxLength <= 0) {
      return None
    }
    val visited = curr.flatMap { case ((beg, end), _) => Seq(beg, end) }.toSet[AnyRef]
    for (o <- snode.outgoing; i <- o.forward.neighborsOf(s)) {
      if (i == t) {
        // found the final link!
        return Some(curr ++ Seq((s -> t) -> o.asInstanceOf[Edge[AnyRef, AnyRef]]))
      }
      if (!visited(i.asInstanceOf[AnyRef])) {
        queue += State(i.asInstanceOf[AnyRef], o.to.asInstanceOf[Node[AnyRef]], t, maxLength - 1, curr ++ Seq((s -> i.asInstanceOf[AnyRef]) -> o.asInstanceOf[Edge[AnyRef, AnyRef]]))
      }
    }
    None
  }

  def findPath[S <: AnyRef](s: S, snode: Node[S], t: AnyRef, maxLength: Int = 10): Seq[Path[AnyRef, AnyRef]] = {
    val queue = new mutable.Queue[State[_ <: AnyRef]]
    queue += State(s, snode, t, maxLength)
    while (queue.nonEmpty) {
      val s = queue.dequeue()
      val r = findPath(s, queue)
      if (r.isDefined) return r.get
    }
    Seq.empty
  }

}
