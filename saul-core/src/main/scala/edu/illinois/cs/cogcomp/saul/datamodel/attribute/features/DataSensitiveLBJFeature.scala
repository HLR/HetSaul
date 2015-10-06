package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

/** Created by haowu on 2/10/15.
  */
abstract class DataSensitiveLBJFeature extends ClassifierContainsInLBP {
  var datamodel: DataModel

  def setDM(dm: DataModel) = this.datamodel = dm
}
