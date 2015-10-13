package edu.illinois.cs.cogcomp.saul.datamodel.edge

import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Link[A <: AnyRef, B <: AnyRef](val from: Node[A], val to: Node[B], val name: Option[Symbol]) {
  val index = new mutable.HashMap[A, ArrayBuffer[B]]
  def neighborsOf(t: A): Iterable[B] = index.getOrElse(t, Seq.empty)
  def +=(a: A, b: B) = index.getOrElseUpdate(a, new ArrayBuffer) += b
  def ++=(a: A, bs: Iterable[B]) = index.getOrElseUpdate(a, new ArrayBuffer) ++= bs

  /** sensors */
  val sensors = new ArrayBuffer[A => Iterable[B]]()

  def addSensor(f: A => Iterable[B]) = sensors += f

  def populate(a: A, train: Boolean = true): Unit = {
    sensors foreach (f => {
      for (b <- f(a)) {
        this += (a, b)
        to.addInstance(b, train)
      }
    })
  }
}

case class Edge[T <: AnyRef, U <: AnyRef](forward: Link[T, U], backward: Link[U, T]) {
  def from = forward.from
  def to = forward.to
  def +=(t: T, u: U) = {
    forward += (t, u)
    backward += (u, t)
  }

  @deprecated
  def populateWith(sensor: (T) => U)(implicit d: DummyImplicit): Unit = populateWith((t: T) => Seq(sensor(t)))

  @deprecated
  def populateWith(sensor: (T) => Option[U])(implicit d1: DummyImplicit, d2: DummyImplicit): Unit = populateWith((t: T) => sensor(t).toSeq)

  @deprecated
  def populateWith(sensor: (T) => Iterable[U]) = {
    forward.from.getAllInstances foreach (t => {
      val us = sensor(t)
      forward.to.populate(us)
      forward ++= (t, us)
      for (u <- us) backward += (u, t)
    })
  }

  def populateWith(
    sensor: (T, U) => Boolean,
    from: Iterable[T] = forward.from.getAllInstances,
    to: Iterable[U] = forward.to.getAllInstances
  ) =
    for (t <- from; u <- to; if (sensor(t, u))) this += (t, u)

  def unary_- = Edge(backward, forward)

  def links = forward.index.flatMap((p) => p._2.map(b => p._1 -> b)).toSeq

  /** matchers */
  val matchers = new ArrayBuffer[(T, U) => Boolean]

  def addSensor(f: (T, U) => Boolean) = matchers += f

  def addSensor(sensor: (T) => Iterable[U]) = forward.addSensor(sensor)

  def addSensor(sensor: (T) => U)(implicit d: DummyImplicit) = forward.addSensor(a => Seq(sensor(a)))

  // def addSensor(sensor: (T) => Option[U])(implicit d1: DummyImplicit, d2: DummyImplicit) = forward.addSensor((a => sensor(a).toList))

  def addReverseSensor(sensor: (T) => Iterable[U]) = forward.addSensor(sensor)

  def addReverseSensor(sensor: (T) => U)(implicit d: DummyImplicit) = forward.addSensor(a => Seq(sensor(a)))

  // def addReverseSensor(sensor: (T) => Option[U])(implicit d1: DummyImplicit, d2: DummyImplicit) = forward.addSensor((a => sensor(a).toList))

  def populateUsingFrom(t: T, train: Boolean = true): Unit = {
    forward.populate(t)
    matchers.foreach(f => populateWith(f, Seq(t)))
  }

  def populateUsingTo(u: U, train: Boolean = true): Unit = {
    backward.populate(u)
    matchers.foreach(f => populateWith(f, to = Seq(u)))
  }
}
