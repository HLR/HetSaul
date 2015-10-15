package edu.illinois.cs.cogcomp.saul.datamodel.attribute

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

trait DataModelSensitiveAttribute[T <: AnyRef] extends Attribute[T] {

  var dataModel: DataModel

  def setDM(dm: DataModel) = {

    println(s"Setting dm $dm for ${this.name}")
    this.dataModel = dm
  }
}
