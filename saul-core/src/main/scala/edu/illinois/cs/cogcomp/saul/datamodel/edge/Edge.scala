/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.edge

import edu.illinois.cs.cogcomp.core.datastructures.vectors.{ ExceptionlessInputStream, ExceptionlessOutputStream }

import scala.collection.mutable.ArrayBuffer

/** Represents an Edge in the data model graph. Edges are used to connect two nodes in the data model graph.
  * @tparam T Type of the source node's data
  * @tparam U Type of the target node's data.
  */
trait Edge[T <: AnyRef, U <: AnyRef] {
  def forward: Link[T, U]
  def backward: Link[U, T]
  def matchers: ArrayBuffer[(T, U) => Boolean]

  def from = forward.from
  def to = forward.to
  def +=(t: T, u: U) = {
    forward += (t, u)
    backward += (u, t)
  }

  def populateFrom(e: Edge[_, _]): Unit = {
    e.links.foreach {
      case (a, b) => this += (a.asInstanceOf[T], b.asInstanceOf[U])
    }
  }

  def clear(): Unit = {
    forward.clear
    backward.clear
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
    for (t <- from; u <- to; if sensor(t, u)) this += (t, u)

  def unary_- : Edge[U, T]

  def links = forward.pairs.toSeq

  def addSensor(f: (T, U) => Boolean) = matchers += f

  def addSensor(sensor: (T) => Iterable[U]) = forward.addSensor(sensor)

  def addSensor(sensor: (T) => U)(implicit d: DummyImplicit) = forward.addSensor(a => Seq(sensor(a)))

  def addReverseSensor(sensor: (T) => Iterable[U]) = forward.addSensor(sensor)

  def addReverseSensor(sensor: (T) => U)(implicit d: DummyImplicit) = forward.addSensor(a => Seq(sensor(a)))

  def populateUsingFrom(t: T, train: Boolean = true): Unit = {
    forward.sensors foreach (f => {
      for (u <- f(t)) {
        this += (t, u)
        to.addInstance(u, train)
      }
    })
    matchers.foreach(f => populateWith(f, Seq(t)))
  }

  def populateUsingTo(u: U, train: Boolean = true): Unit = {
    backward.sensors foreach (f => {
      for (t <- f(u)) {
        this += (t, u)
        from.addInstance(t, train)
      }
    })
    matchers.foreach(f => populateWith(f, to = Seq(u)))
  }

  def deriveIndexWithIds() = {
    forward.deriveIndexWithId()
    backward.deriveIndexWithId()
  }

  def writeIndexWithIds(out: ExceptionlessOutputStream) = {
    forward.writeIndexWithId(out)
    backward.writeIndexWithId(out)
  }

  def loadIndexWithIds(in: ExceptionlessInputStream) = {
    forward.loadIndexWithId(in)
    backward.loadIndexWithId(in)
  }

  def apply(t: T) = from(t) ~> this
  def apply(ts: Iterable[T]) = from(ts) ~> this
}

/** Represents an edge between two different data types.
  *
  * @param forward Forward link
  * @param backward Backward link
  * @param ms Matching functions
  * @tparam T Type of the source node's data
  * @tparam U Type of the target node's data.
  */
case class AsymmetricEdge[T <: AnyRef, U <: AnyRef](forward: Link[T, U], backward: Link[U, T], ms: Seq[(T, U) => Boolean] = Seq.empty[(T, U) => Boolean])
  extends Edge[T, U] {

  val matchers = {
    val m = ArrayBuffer.empty[(T, U) => Boolean]
    m ++= ms
    m
  }

  override def unary_- : Edge[U, T] = AsymmetricEdge(backward, forward, matchers.map(f => (u: U, t: T) => f(t, u)))
}

/** Represents an edge between the same data type.
  *
  * @param link Link
  * @param ms Matching functions
  * @tparam T Type of the source node's data
  */
case class SymmetricEdge[T <: AnyRef](link: Link[T, T], ms: Seq[(T, T) => Boolean] = Seq.empty[(T, T) => Boolean])
  extends Edge[T, T] {
  def forward = link
  def backward = link
  val matchers = {
    val m = ArrayBuffer.empty[(T, T) => Boolean]
    m ++= ms
    m
  }

  override def unary_- = this
}
