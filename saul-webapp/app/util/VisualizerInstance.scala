/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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
