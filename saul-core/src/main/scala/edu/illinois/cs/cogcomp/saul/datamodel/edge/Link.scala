package edu.illinois.cs.cogcomp.saul.datamodel.edge

import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Link[A <: AnyRef, B <: AnyRef](val from: Node[A], val to: Node[B], val name: Option[Symbol]) {
  val index = new mutable.HashMap[A, ArrayBuffer[B]]
  def neighborsOf(t: A): Iterable[B] = index.getOrElse(t, Seq.empty)
  def +=(a: A, b: B) = index.getOrElseUpdate(a, new ArrayBuffer) += b
  def ++=(a: A, bs: Iterable[B]) = index.getOrElseUpdate(a, new ArrayBuffer) ++= bs
}

case class Edge[T <: AnyRef, U <: AnyRef](forward: Link[T, U], backward: Link[U, T]) {
  def from = forward.from
  def to = forward.to
  def +=(t: T, u: U) = {
    forward += (t, u)
    backward += (u, t)
  }

  def populateWith(sensor: (T) => Iterable[U]) = {
    forward.from.getAllInstances foreach (t => {
      val us = sensor(t)
      forward.to.populate(us)
      forward ++= (t, us)
      for (u <- us) backward += (u, t)
    })
  }

  def populateWith(sensor: (T) => U)(implicit d: DummyImplicit): Unit = populateWith((f: T) => List(sensor(f)))

  def populateWith(sensor: (T) => Option[U])(implicit d1: DummyImplicit, d2: DummyImplicit): Unit = populateWith((f: T) => sensor(f).toList)

  def populateWith(
    sensor: (T, U) => Boolean,
    from: Iterable[T] = forward.from.getAllInstances,
    to: Iterable[U] = forward.to.getAllInstances
  ) =
    for (t <- from; u <- to; if (sensor(t, u))) this += (t, u)

  def unary_- = Edge(backward, forward)
}
