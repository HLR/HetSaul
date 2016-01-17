package edu.illinois.cs.cogcomp.saul.datamodel.node

import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge
import org.scalautils.Or

import scala.collection.immutable.Queue
import scala.collection.mutable

/** @author sameer
  * @since 1/17/16.
  */
//case class Path[S, I, T](link: (S, I), edge: Edge[S, I], rest: Path[I, _, T]) {
//  def length: Int = 1 + rest.length
//  def instances: Seq[(Any, Node[_])] = Seq(link._2 -> edge.to) ++ rest.instances
//  def edges: Seq[Edge[_, _]] = Seq(edge) ++ rest.edges
//}
//
//case class SingleLink[S, T](l: (S, T), e: Edge[S, T]) extends Path[S,T,T](l, e, null) {
//  override def length: Int = 1
//
//  override def edges: Seq[Edge[_, _]] = Seq(edge)
//
//  override def instances: Seq[(Any, Node[_])] = Seq(l._2 -> edge.to)
//}

object Path {

  type Path[S <: AnyRef, T <: AnyRef] = ((S, T), Edge[S, T])

  case class State[S <: AnyRef](s: S, snode: Node[S], t: AnyRef, maxLength: Int = 10, curr: Seq[Path[AnyRef, AnyRef]] = Seq.empty)

  def findPath[S <: AnyRef](state: State[S], queue: mutable.Queue[State[_ <: AnyRef]]): Option[Seq[Path[AnyRef, AnyRef]]] = {
    import state._
    if (maxLength <= 0) {
      return None
    }
    val visited = curr.flatMap(p => Seq(p._1._1, p._1._2)).toSet[AnyRef]
    for (o <- snode.outgoing) {
      for (i <- o.forward.neighborsOf(s)) {
        if (i == t) {
          // found the final link!
          return Some(curr ++ Seq((s -> t) -> o.asInstanceOf[Edge[AnyRef, AnyRef]]))
        }
        if (!visited(i.asInstanceOf[AnyRef])) {
          queue += State(i.asInstanceOf[AnyRef], o.to.asInstanceOf[Node[AnyRef]], t, maxLength - 1, curr ++ Seq((s -> i.asInstanceOf[AnyRef]) -> o.asInstanceOf[Edge[AnyRef, AnyRef]]))
        }
      }
    }
    None
  }

  def findPath[S <: AnyRef](s: S, snode: Node[S], t: AnyRef, maxLength: Int = 10): Seq[Path[AnyRef, AnyRef]] = {
    val queue = new mutable.Queue[State[_ <: AnyRef]]
    queue += State(s, snode, t, maxLength)
    while (!queue.isEmpty) {
      val s = queue.dequeue()
      val r = findPath(s, queue)
      if (r.isDefined) return r.get
    }
    Seq.empty
  }

}
