/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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

