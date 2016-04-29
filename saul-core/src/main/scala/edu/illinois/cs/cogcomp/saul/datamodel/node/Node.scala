package edu.illinois.cs.cogcomp.saul.datamodel.node

import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector
import edu.illinois.cs.cogcomp.lbjava.util.{ ExceptionlessInputStream, ExceptionlessOutputStream }
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete.DiscreteProperty
import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge

import scala.collection.mutable.{ Map => MutableMap, LinkedHashSet => MutableSet, ArrayBuffer }
import scala.collection.mutable
import scala.reflect.ClassTag

trait NodeProperty[T <: AnyRef] extends Property[T] {
  def node: Node[T]
}

/** Representation of an instance inside the Node.
  * @param t original instance
  * @param keyFunc key function used to extract the key
  * @tparam T base type of the instances
  */
class NodeInstance[T](val t: T, val keyFunc: T => Any) {
  val key: Any = keyFunc(t)
  def apply = t

  override def hashCode(): Int = key.hashCode()

  override def equals(obj: scala.Any): Boolean = obj match {
    case nt2: NodeInstance[T] => key.equals(nt2.key)
  }
}

/** A Node E is an instances of base types T */
class Node[T <: AnyRef](val keyFunc: T => Any = (x: T) => x, val tag: ClassTag[T]) {

  type NT = NodeInstance[T]

  val outgoing = new ArrayBuffer[Edge[T, _]]()
  val incoming = new ArrayBuffer[Edge[_, T]]()

  val joinNodes = new ArrayBuffer[JoinNode[_, _]]()

  val properties = new ArrayBuffer[NodeProperty[T]]

  private val collection = MutableSet[NT]()

  def getAllInstances: Iterable[T] = this.collection.toSeq.map(_.apply)

  val trainingSet = MutableSet[NT]()

  val testingSet = MutableSet[NT]()

  def getTrainingInstances: Iterable[T] = this.trainingSet.toSeq.map(_.apply)

  def getTestingInstances: Iterable[T] = this.testingSet.toSeq.map(_.apply)

  val orderingMap = MutableMap[Int, NT]()
  val reverseOrderingMap = MutableMap[NT, Int]()

  def filterNode(property: DiscreteProperty[T], value: String): Node[T] = {
    val node = new Node[T](this.keyFunc, this.tag)
    node populate collection.filter {
      nt => property.sensor(nt.apply) == value
    }.toSeq.map(_.apply)
    node
  }

  def clear() = {
    collection.clear()
    trainingSet.clear()
    testingSet.clear()
    for (e <- incoming) e.clear
    for (e <- outgoing) e.clear
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

  def contains(t: T): Boolean = collection.contains(toNT(t))

  def toNT(t: T): NT = new NT(t, keyFunc)

  private def containsNT(nt: NT): Boolean = collection.contains(nt)

  def containsUntyped(t: Any): Boolean = if (t.isInstanceOf[T]) {
    contains(t.asInstanceOf[T])
  } else false

  def addInstance(t: T, train: Boolean = true, populateEdge: Boolean = true) = {
    val nt = toNT(t)
    if (!containsNT(nt)) {
      val order = incrementCount()
      if (train) this.trainingSet += nt else this.testingSet += nt
      this.collection += nt
      this.orderingMap += (order -> nt)
      this.reverseOrderingMap += (nt -> order)
      if (populateEdge) {
        outgoing.foreach(_.populateUsingFrom(t, train))
        incoming.foreach(_.populateUsingTo(t, train))
      }
      joinNodes.foreach(_.addFromChild(this, t, train, populateEdge))
    }
  }

  def populateFrom(n: Node[_]): Unit = {
    populate(n.getTrainingInstances.map(_.asInstanceOf[T]), true, false)
    populate(n.getTestingInstances.map(_.asInstanceOf[T]), false, false)
  }

  /** Operator for adding a sequence of T into my table. */
  def populate(ts: Iterable[T], train: Boolean = true, populateEdge: Boolean = true) = {
    ts.foreach(addInstance(_, train, populateEdge))
  }

  /** Relational operators */
  val nodeOfTypeT = this
  type instanceType = T

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
      val nt = toNT(t)
      /** relative not equal to 0 */
      this.reverseOrderingMap.get(nt) match {
        case Some(ord) =>
          val targetOrd = ord + relativePos
          this.orderingMap.get(targetOrd) match {
            case Some(x) =>
              if (underSameParent(t, x.apply, filters)) {
                Some(x.apply)
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

  def prevOf(t: T, filters: List[Symbol]): Option[T] = {
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
    val nt1 = toNT(t1)
    val nt2 = toNT(t2)
    // If t1 and t2 are not under same parents, then we ignore the parent.
    (this.reverseOrderingMap.get(nt1), this.reverseOrderingMap.get(nt2)) match {
      case (Some(start), Some(end)) => {
        (start to end) map {
          position =>
            this.orderingMap.get(position) match {
              case Some(v) => if (wildCard || underSameParent(t1, v.apply, filter)) {
                Some(v.apply)
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
    (this.reverseOrderingMap.get(toNT(t)) match {
      case Some(myOrder) =>
        val start = myOrder + before
        val end = myOrder + after
        val result = (start to end).flatMap(this.orderingMap.get).filter {
          x => underSameParent(t, x.apply, filters)
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
    }).map(_.map(_.apply))
  }

  val derivedInstances = new mutable.HashMap[Int, FeatureVector]()

  def deriveInstances(properties: List[Property[_]]) = {
    val castedProperties = properties.map(_.asInstanceOf[Property[T]])
    orderingMap.foreach {
      case (instanceId, instance) =>
        val featureVector = new FeatureVector()
        castedProperties.foreach {
          property =>
            property.addToFeatureVector(instance.apply, featureVector, property.name)
        }
        derivedInstances.put(instanceId, featureVector)
    }
  }

  def writeDerivedInstances(out: ExceptionlessOutputStream) = {
    out.writeInt(count)
    out.writeInt(derivedInstances.size)
    derivedInstances.foreach {
      case (id, featureVector) =>
        out.writeInt(id)
        featureVector.write(out)
    }
  }

  def loadDerivedInstances(in: ExceptionlessInputStream) = {
    count = in.readInt()
    val instanceCount = in.readInt()
    (0 until instanceCount).foreach {
      lineIndex =>
        val id = in.readInt()
        val featureVector = new FeatureVector()
        featureVector.read(in)
        derivedInstances.put(id, featureVector)
    }
  }
}

class JoinNode[A <: AnyRef, B <: AnyRef](val na: Node[A], val nb: Node[B], matcher: (A, B) => Boolean, tag: ClassTag[(A, B)]) extends Node[(A, B)](p => na.keyFunc(p._1) -> nb.keyFunc(p._2), tag) {

  def addFromChild[T <: AnyRef](node: Node[T], t: T, train: Boolean = true, populateEdge: Boolean = true) = {
    node match {
      case this.na => matchAndAddChildrenA(t.asInstanceOf[A], train, populateEdge)
      case this.nb => matchAndAddChildrenB(t.asInstanceOf[B], train, populateEdge)
    }
  }

  private def matchAndAddChildrenA(a: A, train: Boolean = true, populateEdge: Boolean = true): Unit = {
    val instances = if (train) nb.getTrainingInstances else nb.getTestingInstances
    for ((a, b) <- instances.filter(matcher(a, _)).map(a -> _)) {
      if (!contains(a -> b))
        this addInstance (a -> b, train, populateEdge)
    }
  }

  private def matchAndAddChildrenB(b: B, train: Boolean = true, populateEdge: Boolean = true): Unit = {
    val instances = if (train) na.getTrainingInstances else na.getTestingInstances
    for ((a, b) <- instances.filter(matcher(_, b)).map(_ -> b)) {
      if (!contains(a -> b))
        this addInstance (a -> b, train, populateEdge)
    }
  }

  override def addInstance(t: (A, B), train: Boolean, populateEdge: Boolean = true): Unit = {
    assert(matcher(t._1, t._2))
    if (!contains(t)) {
      super.addInstance(t, train, populateEdge)
      na.addInstance(t._1, train, populateEdge)
      nb.addInstance(t._2, train, populateEdge)
    }
  }
}
