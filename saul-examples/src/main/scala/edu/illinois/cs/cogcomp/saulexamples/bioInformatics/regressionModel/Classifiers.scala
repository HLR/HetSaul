package edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel

import edu.illinois.cs.cogcomp.lbjava.learn.StochasticGradientDescent
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.PatientDrug
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel.knowEngDataModel._
/** Created by Parisa on 6/25/15.
  */
object Classifiers {
  object dResponseClassifier extends Learnable[PatientDrug](knowEngDataModel) {
    def label = drugResponse
    override lazy val classifier = new StochasticGradientDescent
  }
}

