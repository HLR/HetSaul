package edu.illinois.cs.cogcomp.lfs.data_model.attribute

import edu.illinois.cs.cogcomp.lfs.data_model.DataModel

/** Created by haowu on 2/10/15.
  */
trait DataModelSensitiveAttribute[T <: AnyRef] extends Attribute[T] {

  var dataModel: DataModel

  def setDM(dm: DataModel) = {

    println(s"Setting dm ${dm} for ${this.name}")
    this.dataModel = dm
  }
}
