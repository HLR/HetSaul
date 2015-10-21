package edu.illinois.cs.cogcomp.saul.datamodel.node

import edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete.DiscreteProperty
import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge

import scala.collection.mutable.{ Map => MutableMap, LinkedHashSet => MutableSet, ArrayBuffer }
import scala.reflect.ClassTag

/** A Node E is an instances of base types T */
class Node[T <: AnyRef](val tag: ClassTag[T]) {

  val outgoing = new ArrayBuffer[Edge[T, _]]()
  val incoming = new ArrayBuffer[Edge[_, T]]()

  val joinNodes = new ArrayBuffer[JoinNode[_, _]]()

  private val collections = MutableSet[T]()

  def getAllInstances: Iterable[T] = this.collections

  val trainingSet = MutableSet[T]()

  val testingSet = MutableSet[T]()

  def getTrainingInstances: Iterable[T] = this.trainingSet

  def getTestingInstances: Iterable[T] = this.testingSet

  private val orderingMap = MutableMap[Int, T]()
  private val reverseOrderingMap = MutableMap[T, Int]()

  def filterNode(property: DiscreteProperty[T], value: String): Node[T] = {
    val node = new Node[T](this.tag)
    node populate collections.filter {
      property.sensor(_) == value
    }.toSeq
    node
  }

  var count = 0

  def incrementCount(): Int = this.synchronized {
    val ret = count
    count = count + 1
    ret
  }

  def decreaseCount(): Int = this.synchronized {
    val ret = count
    count = count - 1
    ret
  }

  def contains(t: T): Boolean = collections(t)

  def addInstance(t: T, train: Boolean = true) = {
    if (!contains(t)) {
      val order = incrementCount()
      if (train) this.trainingSet += t else this.testingSet += t
      this.collections += t
      this.orderingMap += (order -> t)
      this.reverseOrderingMap += (t -> order)
      outgoing.foreach(_.populateUsingFrom(t, train))
      incoming.foreach(_.populateUsingTo(t, train))
      joinNodes.foreach(_.addFromChild(this, t, train))
    }
  }

  /** Operator for adding a sequence of T into my table. */
  def populate(ts: Iterable[T], train: Boolean = true) = {
    ts.foreach(addInstance(_, train))
  }

  /** Relational operators */
  val nodeOfTypeT = this

  def apply() = NodeSet(this)
  def apply(t: T) = SingletonSet(this, t)
  def apply(ts: Iterable[T]) = BasicSet(this, ts)

  def getWithRelativePosition(t: T, relativePos: Int): Option[T] = {
    getWithRelativePosition(t, relativePos, Nil)
  }

  def getWithRelativePosition(t: T, relativePos: Int, filters: Iterable[T => Any]): Option[T] = {
    if (relativePos == 0) {
      Some(t)
    } else {
      /** relative not equal to 0 */
      this.reverseOrderingMap.get(t) match {
        case Some(ord) =>
          val targetOrd = ord + relativePos
          this.orderingMap.get(targetOrd) match {
            case Some(x) =>
              if (underSameParent(t, x, filters)) {
                Some(x)
              } else {
                None
              }
            case None => None
          }
        case _ => None
      }
    }
  }

  def nextOf(t: T, filters: List[Symbol]): Option[T] = {
    getWithWindow(t, 1, 1, Nil) match {
      case head :: more => head
      case Nil => None
    }
  }

  def pervOf(t: T, filters: List[Symbol]): Option[T] = {
    getWithWindow(t, -1, -1, Nil) match {
      case head :: more => head
      case Nil => None
    }
  }

  // TODO: add documentation to the following methods
  def getWithWindow(t: T, before: Int, after: Int): List[Option[T]] = {
    getWithWindow(t, before, after, Nil)
  }

  def getWithWindow(t: T, before: Int, after: Int, filterSym: T => Any): List[Option[T]] = {
    getWithWindow(t, before, after, filterSym :: Nil)
  }

  def underSameParent(t: T, x: T, filters: Iterable[T => Any]): Boolean = {
    filters.forall(f => f(t) == f(x))
  }

  def between(t1: T, t2: T, filter: Iterable[T => Any]): List[Option[T]] = {
    val wildCard = !underSameParent(t1, t2, filter)
    // If t1 and t2 are not under same parents, then we ignore the parent.
    (this.reverseOrderingMap.get(t1), this.reverseOrderingMap.get(t2)) match {
      case (Some(start), Some(end)) => {
        (start to end) map {
          position =>
            this.orderingMap.get(position) match {
              case Some(v) => if (wildCard || underSameParent(t1, v, filter)) {
                Some(v)
              } else {
                None
              }
              case None => None
            }
        }
      }.toList
      case _ => throw new Exception("Can't find element.")
    }
  }

  def getWithWindow(t: T, before: Int, after: Int, filters: Iterable[T => Any]): List[Option[T]] = {
    this.reverseOrderingMap.get(t) match {
      case Some(myOrder) =>
        val start = myOrder + before
        val end = myOrder + after
        val result = (start to end).flatMap(this.orderingMap.get).filter {
          x => underSameParent(t, x, filters)
        }.toList

        val pos = result.indexOf(t)
        // Now we need to shift the list
        // before is negative, after is positive.
        val tShouldBeAt = -before
        if (tShouldBeAt < 0) {
          result map (Some(_))
        } else {

          val prependAmount = tShouldBeAt - pos
          val appendAmount = (after - before + 1) - (prependAmount + result.size)

          val toPrepend = Nil.padTo(prependAmount, None)
          val toAppend = Nil.padTo(appendAmount, None)

          val middle = result map (Some(_))
          toPrepend ::: middle ::: toAppend
        }
      case _ =>
        throw new Exception("Can't find order of " + t.toString)
    }
  }
}

class JoinNode[A <: AnyRef, B <: AnyRef](val na: Node[A], val nb: Node[B], matcher: (A, B) => Boolean, tag: ClassTag[(A, B)]) extends Node[(A, B)](tag) {

  def addFromChild[T <: AnyRef](node: Node[T], t: T, train: Boolean = true) = {
    node match {
      case this.na => matchAndAddChildrenA(t.asInstanceOf[A], train)
      case this.nb => matchAndAddChildrenB(t.asInstanceOf[B], train)
    }
  }

  private def matchAndAddChildrenA(a: A, train: Boolean = true): Unit = {
    val instances = if (train) nb.getTrainingInstances else nb.getTestingInstances
    for ((a, b) <- instances.filter(matcher(a, _)).map(a -> _)) {
      if (!contains(a -> b))
        this addInstance (a -> b, train)
    }
  }

  private def matchAndAddChildrenB(b: B, train: Boolean = true): Unit = {
    val instances = if (train) na.getTrainingInstances else na.getTestingInstances
    for ((a, b) <- instances.filter(matcher(_, b)).map(_ -> b)) {
      if (!contains(a -> b))
        this addInstance (a -> b, train)
    }
  }

  override def addInstance(t: (A, B), train: Boolean): Unit = {
    assert(matcher(t._1, t._2))
    if (!contains(t)) {
      super.addInstance(t, train)
      na.addInstance(t._1, train)
      nb.addInstance(t._2, train)
    }
  }
}
