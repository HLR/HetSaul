package util

import edu.illinois.cs.cogcomp.saul.datamodel.node.{ PropertySet, InstanceSet }

object VisualizerInstance {

  var propertySet: PropertySet[AnyRef, AnyRef] = null
  var instanceSet: InstanceSet[AnyRef] = null

  def init: Unit = {
    propertySet = null
    instanceSet = null
  }

  def visualize[T <: AnyRef](instanceSet: InstanceSet[T]): Unit = {
    this.instanceSet = instanceSet.asInstanceOf[InstanceSet[AnyRef]]
  }

  def visualize[T <: AnyRef, V](propertySet: PropertySet[T, V]): Unit = {
    this.propertySet = propertySet.asInstanceOf[PropertySet[AnyRef, AnyRef]]
  }

}
