package edu.illinois.cs.cogcomp.saul.datamodel.node

import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty

import scala.reflect.ClassTag

trait InstanceSet[T <: AnyRef] extends Iterable[T] {
  self =>
  def instances: Iterable[T]
  def node: Node[T]

  def ~>[U <: AnyRef](edge: Edge[T, U]): InstanceSet[U] = {
    assert(node == edge.forward.from)
    new InstanceSet[U] {
      val node: Node[U] = edge.forward.to
      val instances: Iterable[U] = self.instances.flatMap(t => edge.forward.neighborsOf(t))
    }
  }

  override def filter(pred: T => Boolean) = new InstanceSet[T] {
    val node: Node[T] = self.node
    val instances: Iterable[T] = self.instances.filter(pred)
  }

  def prop[V](p: TypedProperty[T, V]) = new PropertySet[T, V] {
    val property: TypedProperty[T, V] = p
    val underlying: InstanceSet[T] = self
  }

  override def iterator: Iterator[T] = instances.iterator
}

case class BasicSet[T <: AnyRef](node: Node[T], instances: Iterable[T]) extends InstanceSet[T]

case class NodeSet[T <: AnyRef](node: Node[T])(implicit tType:ClassTag[T]) extends InstanceSet[T] {
  type n= T
  override def instances: Iterable[T] = node.getAllInstances
}

case class SingletonSet[T <: AnyRef](node: Node[T], t: T) extends InstanceSet[T] {
  val instances: Iterable[T] = Set(t)
}

trait PropertySet[T <: AnyRef, V] extends Iterable[V] {
  self =>
  def property: TypedProperty[T, V]
  def underlying: InstanceSet[T]
  lazy val instances: Iterable[V] = underlying.instances.toSeq.map(property(_))

  override def iterator: Iterator[V] = instances.iterator

  def counts = instances.groupBy(x => x).map(p => p._1 -> p._2.size).toMap
}