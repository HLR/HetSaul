
package edu.illinois.cs.cogcomp.saulexamples.TestClassifier

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

object TestClassifierDataModel extends DataModel {

  val tcData = node[TestClassifierData]

  val dataLabel = property(tcData) {
    x: TestClassifierData => x.label
  }

  val realFeatures = property(tcData) {
    x: TestClassifierData => x.features.split(" ").toList.map(_.toDouble)
  }
  val boolValue = property(tcData)("true", "false") {
    x: TestClassifierData =>
      {
        if (x.bVal.equals("1"))
          "true"
        else
          "false"
      }
  }

  val intValue = property(tcData) {
    x: TestClassifierData => x.iVal.toInt
  }
}
