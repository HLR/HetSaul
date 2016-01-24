package util

import edu.illinois.cs.cogcomp.saul.datamodel.node.InstanceSet
import edu.illinois.cs.cogcomp.saul.datamodel.node.instanceCache

object visualizer {

  def visualize(instanceSet: InstanceSet[String]): Unit = {

    val sourceNode = instanceCache.node
    val targetNode = instanceSet.node
    sourceNode.clearData
    sourceNode.populate(instanceCache.instances)
    targetNode.clearData
    targetNode.populate(instanceSet.instances)
  }

}
