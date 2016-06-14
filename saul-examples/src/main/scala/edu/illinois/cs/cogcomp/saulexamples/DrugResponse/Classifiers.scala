package edu.illinois.cs.cogcomp.saulexamples.DrugResponse

import edu.illinois.cs.cogcomp.lbjava.learn.StochasticGradientDescent
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.KnowEngDataModel._
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.PatientDrug
/** Created by Parisa on 6/25/15.
  */
object Classifiers {
  object dResponseClassifier extends Learnable[PatientDrug](patientDrug) {
    def label = drugResponse
    override def feature = using(cP1)
    override lazy val classifier = new StochasticGradientDescent
  }
  class DrugResponseRegressor(pathway: String) extends Learnable[PatientDrug](patientDrug) {
    def label = drugResponse
    override def feature = using(pathWayGExpression(pathway))
    override lazy val classifier = new StochasticGradientDescent
  }
}

