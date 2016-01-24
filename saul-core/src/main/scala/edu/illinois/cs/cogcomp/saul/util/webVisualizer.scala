package edu.illinois.cs.cogcomp.saul.util

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.node.{ instanceCache, Node, InstanceSet }

object webVisualizer extends DataModel {

  var sourceNode: Node[String] = null
  var isInitiated = false

  def visualize(instanceSet: InstanceSet[String]): Unit = {
    sourceNode = instanceCache.node
    isInitiated = true
    val targetNode = instanceSet.node
    targetNode.clear
    targetNode.populate(instanceSet.instances)
    sourceNode.clear
    sourceNode.populate(instanceCache.instances)
  }

}

