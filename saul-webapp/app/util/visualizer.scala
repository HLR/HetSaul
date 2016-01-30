package util

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.node.{ PropertySet, InstanceSet, instanceCache }

object visualizer {

  var dm: DataModel = null
  var propertySet: PropertySet[String, String] = null
  var instanceSet: InstanceSet[String] = null

  def apply(dm: DataModel): Unit = {
    this.dm = dm
    if (instanceSet != null) {
      visualize(instanceSet)
    } else if (propertySet != null) {
      visualize(propertySet)
    }
  }

  def visualize(instanceSet: InstanceSet[String]): Unit = {

    this.instanceSet = instanceSet

    if (dm != null) {
      val sourceNode = instanceCache.node
      val targetNode = instanceSet.node

      dm.NODES --= dm.NODES.filter(n =>
        n != sourceNode && n != targetNode)
      if (sourceNode != null) {
        sourceNode.clearData
        sourceNode.populate(instanceCache.instances)
      }
      targetNode.clearData
      targetNode.populate(instanceSet.instances)
    }
  }

  def visualize(propertySet: PropertySet[String, String]): Unit = {

    this.propertySet = propertySet

    if (dm != null) {
      val node = propertySet.underlying.node
      //Clean up nodes from irrelated groups
      dm.NODES --= dm.NODES.filter(n => n != node)
      //Clean up not selected instances
      val instances = propertySet.underlying.instances
      node.clearData
      node.populate(instances)
    }
  }

}
