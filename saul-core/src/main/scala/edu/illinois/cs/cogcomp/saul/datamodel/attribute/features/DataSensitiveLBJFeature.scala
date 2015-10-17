package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

abstract class DataSensitiveLBJFeature extends ClassifierContainsInLBP {
  var datamodel: DataModel

  def setDM(dm: DataModel) = this.datamodel = dm
}
