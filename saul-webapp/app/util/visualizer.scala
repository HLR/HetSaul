package util

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.node.{ NodeProperty, PropertySet, InstanceSet }

object visualizer {

  private var dm: DataModel = null
  private var propertySet: PropertySet[AnyRef, AnyRef] = null
  private var instanceSet: InstanceSet[AnyRef] = null

  def init(): Unit = {
    dm = null
    propertySet = null
    instanceSet = null
  }

  def apply(dm: DataModel): Unit = {
    this.dm = dm
    if (instanceSet != null) {
      visualize(instanceSet)
    } else if (propertySet != null) {
      visualize(propertySet)
    }
  }

  def visualize[T <: AnyRef](instanceSet: InstanceSet[T]): Unit = {

    this.instanceSet = instanceSet.asInstanceOf[InstanceSet[AnyRef]]

    if (dm != null) {
      val targetNode = instanceSet.node

      dm.NODES --= dm.NODES.filter(n => n != targetNode)
      targetNode.clearData
      targetNode.populate(instanceSet.instances)
    }
  }

  def visualize[T <: AnyRef, V](propertySet: PropertySet[T, V]): Unit = {

    this.propertySet = propertySet.asInstanceOf[PropertySet[AnyRef, AnyRef]]

    if (dm != null) {
      def property = propertySet.property
      val node = propertySet.underlying.node
      //Clean up nodes from unrelated groups
      dm.NODES --= dm.NODES.filter(n => n != node)
      //Clean up not selected instances
      val instances = propertySet.underlying.instances
      node.clearData
      node.populate(instances)
    }
  }

}
