package edu.illinois.cs.cogcomp.saulexamples.Badge

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeClassifiers.{ BadgeOppositClassifierMulti, BadgeClassifierMulti, BadgeClassifier, BadgeOppositClassifier }
import edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData.BinaryConstraints
/** Created by Parisa on 11/1/16.
  */
object BadgeConstraintClassifiers {

  val binaryConstraint = ConstrainedClassifier.constraint[String] {
    x: String =>
      (BadgeClassifier on x is "negative") ==> (BadgeOppositClassifier on x is "positive")
  }

  object badgeConstrainedClassifier extends ConstrainedClassifier[String, String](BadgeClassifier) {
    def subjectTo = BinaryConstraints.binaryConstraint
    override val solver = new OJalgoHook
  }

  object oppositBadgeConstrainedClassifier extends ConstrainedClassifier[String, String](BadgeOppositClassifier) {
    def subjectTo = BinaryConstraints.binaryConstraint
    override val solver = new OJalgoHook
  }

  object badgeConstrainedClassifierMulti extends ConstrainedClassifier[String, String](BadgeClassifierMulti) {
    def subjectTo = BinaryConstraints.binaryConstraint
    override val solver = new OJalgoHook
  }

  object oppositBadgeConstrainedClassifierMulti extends ConstrainedClassifier[String, String](BadgeOppositClassifierMulti) {
    def subjectTo = BinaryConstraints.binaryConstraint
    override val solver = new OJalgoHook
  }

}
