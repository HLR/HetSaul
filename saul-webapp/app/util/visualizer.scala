package util

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.node.{ PropertySet, InstanceSet, instanceCache }

object visualizer {

  var dm: DataModel = null
  var propertySet: PropertySet[String, String] = null

  def apply(dm: DataModel): Unit = {
    this.dm = dm
    if (propertySet != null) {
      visualize(propertySet)
    }
  }

  def visualize(instanceSet: InstanceSet[String]): Unit = {
    val sourceNode = instanceCache.node
    val targetNode = instanceSet.node
    sourceNode.clearData
    sourceNode.populate(instanceCache.instances)
    targetNode.clearData
    targetNode.populate(instanceSet.instances)
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
