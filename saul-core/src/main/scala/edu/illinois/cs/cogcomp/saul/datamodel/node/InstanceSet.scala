package edu.illinois.cs.cogcomp.saul.datamodel.node

import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge

trait InstanceSet[T <: AnyRef] {
  self =>
  def instances: Iterable[T]
  def node: Node[T]

  def ~>[U <: AnyRef](edge: Edge[T, U]): InstanceSet[U] = {
    assert(node == edge.forward.from)
    new InstanceSet[U] {
      override def node: Node[U] = edge.forward.to
      override def instances: Iterable[U] = self.instances.flatMap(t => edge.forward.neighborsOf(t))
    }
  }

  def filter(pred: T => Boolean) = new InstanceSet[T] {
    override def instances: Iterable[T] = self.instances.filter(pred)
    override def node: Node[T] = self.node
  }

}

case class BasicSet[T <: AnyRef](node: Node[T], instances: Iterable[T]) extends InstanceSet[T]

case class NodeSet[T <: AnyRef](node: Node[T]) extends InstanceSet[T] {
  override def instances: Iterable[T] = node.getAllInstances
}

case class SingletonSet[T <: AnyRef](node: Node[T], t: T) extends InstanceSet[T] {
  override def instances: Iterable[T] = Seq(t)
}

