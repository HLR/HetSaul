package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Relation

/** Created by parisakordjamshidi on 2/4/17.
  */
object MultiModalSpRLConstraints {

  val integrityTR = ConstrainedClassifier.constraint[Relation] {
    x: Relation =>
      ((TrajectorPairClassifier on x) is "TR-SP") ==>
        (TrajectorRoleClassifier on (pairs(x) ~> pairToFirstArg).head is "Trajector") and
        (IndicatorRoleClassifier on (pairs(x) ~> pairToSecondArg).head is "Indicator")
  }

  val integrityLM = ConstrainedClassifier.constraint[Relation] {
    x: Relation =>
      ((LandmarkPairClassifier on x) is "LM-SP") ==>
        (LandmarkRoleClassifier on (pairs(x) ~> pairToFirstArg).head is "Landmark") and
        (IndicatorRoleClassifier on (pairs(x) ~> pairToSecondArg).head is "Indicator")
  }

  val multiLablePair = ConstrainedClassifier.constraint[Relation] {
    x: Relation =>
      ((LandmarkPairClassifier on x) is "LM-SP") ==> ((TrajectorPairClassifier on x) isNot "TR-SP")
  }

  val allConstraints = ConstrainedClassifier.constraint[Relation] {
    x: Relation => integrityLM(x) and integrityTR(x) and multiLablePair(x)
  }

}
