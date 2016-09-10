/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.edge

import edu.illinois.cs.cogcomp.core.datastructures.vectors.{ ExceptionlessInputStream, ExceptionlessOutputStream }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

import scala.collection.mutable
import scala.collection.mutable.{ LinkedHashSet => MutableSet, LinkedHashMap => MutableMap, ArrayBuffer }

/** Represents a link between two nodes in the data model graph.
  *
  * @param from Source [[Node]] instance of the link.
  * @param to Target [[Node]] instance of the link.
  * @param name Name of the Link
  * @tparam A Type of the source node.
  * @tparam B Type of the target node.
  */
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
