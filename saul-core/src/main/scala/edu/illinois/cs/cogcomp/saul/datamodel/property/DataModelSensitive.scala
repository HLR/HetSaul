package edu.illinois.cs.cogcomp.saul.datamodel.property

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

trait DataModelSensitiveProperty[T <: AnyRef] extends Property[T] {

  var dataModel: DataModel

  def setDM(dm: DataModel) = {

    println(s"Setting dm $dm for ${this.name}")
    this.dataModel = dm
  }
}
