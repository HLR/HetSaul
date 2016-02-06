package edu.illinois.cs.cogcomp.saul.datamodel.edge

import edu.illinois.cs.cogcomp.lbjava.util.{ ExceptionlessInputStream, ExceptionlessOutputStream }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

import scala.collection.mutable
import scala.collection.mutable.{ LinkedHashSet => MutableSet, LinkedHashMap => MutableMap, ArrayBuffer }

class Link[A <: AnyRef, B <: AnyRef](val from: Node[A], val to: Node[B], val name: Option[Symbol]) {
  val index = MutableMap[from.NT, MutableSet[to.NT]]()
  val indexWithId = MutableMap[Int, MutableSet[Int]]()

  def pairs = index.toSeq.flatMap({ case (a, bs) => bs.map(b => a.apply -> b.apply) })

  def neighborsOf(a: A): Iterable[B] = index.getOrElse(from.toNT(a), Seq.empty).toSeq.map(_.apply)

  def +=(a: A, b: B) = {
    val nta = from.toNT(a)
    val ntb = to.toNT(b)
    index.getOrElseUpdate(nta, MutableSet()) += ntb
  }

  def ++=(a: A, bs: Iterable[B]) = bs.foreach(b => this += (a, b))

  def clear = {
    index.clear()
    indexWithId.clear()
  }

  /** sensors */
  val sensors = new ArrayBuffer[A => Iterable[B]]()

  def addSensor(f: A => Iterable[B]) = sensors += f

  @deprecated("Figure out how this interacts with keyFunc")
  def deriveIndexWithId() = {
    pairs.foreach {
      case (fromInstance, toInstance) =>
        val fromId = from.reverseOrderingMap(from.toNT(fromInstance))
        val toId = to.reverseOrderingMap(to.toNT(toInstance))
        indexWithId.getOrElseUpdate(fromId, new mutable.LinkedHashSet) += toId
    }
  }

  @deprecated("Figure out how this interacts with keyFunc")
  def writeIndexWithId(out: ExceptionlessOutputStream) = {
    out.writeInt(indexWithId.size)
    indexWithId.foreach {
      case (fromId, toIds) =>
        out.writeInt(fromId)
        out.writeInt(toIds.size)
        toIds.foreach {
          case toId =>
            out.writeInt(toId)
        }
    }
  }

  @deprecated("Figure out how this interacts with keyFunc")
  def loadIndexWithId(in: ExceptionlessInputStream) = {
    val indexWithIdSize = in.readInt()
    (0 until indexWithIdSize).foreach {
      _ =>
        val fromId = in.readInt()
        val toIdsSize = in.readInt()
        val toIds = new mutable.LinkedHashSet[Int]()
        (0 until toIdsSize).foreach {
          _ =>
            val toId = in.readInt()
            toIds.add(toId)
        }
        indexWithId.put(fromId, toIds)
    }
  }
}

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
      case (a, b) => {
        this += (a.asInstanceOf[T], b.asInstanceOf[U])
      }
    }
  }

  def clear: Unit = {
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

case class AsymmetricEdge[T <: AnyRef, U <: AnyRef](forward: Link[T, U], backward: Link[U, T],
  ms: Seq[(T, U) => Boolean] = Seq.empty[(T, U) => Boolean])
  extends Edge[T, U] {
  val matchers = {
    val m = ArrayBuffer.empty[(T, U) => Boolean]
    m ++= ms
    m
  }

  override def unary_- : Edge[U, T] = AsymmetricEdge(backward, forward, matchers.map(f => (u: U, t: T) => f(t, u)))
}

case class SymmetricEdge[T <: AnyRef](
  link: Link[T, T],
  ms: Seq[(T, T) => Boolean] = Seq.empty[(T, T) => Boolean]
)
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