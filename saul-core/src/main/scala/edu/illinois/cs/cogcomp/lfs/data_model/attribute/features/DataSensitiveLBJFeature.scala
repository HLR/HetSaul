package edu.illinois.cs.cogcomp.lfs.data_model.attribute.features

import edu.illinois.cs.cogcomp.lfs.data_model.DataModel

/** Created by haowu on 2/10/15.
  */
abstract class DataSensitiveLBJFeature extends ClassifierContainsInLBP {
  var datamodel: DataModel

  def setDM(dm: DataModel) = this.datamodel = dm

}
