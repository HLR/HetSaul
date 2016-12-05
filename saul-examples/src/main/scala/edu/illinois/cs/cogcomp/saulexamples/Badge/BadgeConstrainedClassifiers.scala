/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.Badge

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeClassifiers.{ BadgeOppositClassifierMulti, BadgeClassifierMulti, BadgeClassifier, BadgeOppositClassifier }

/** Created by Parisa on 11/1/16.
  */
object BadgeConstrainedClassifiers {

  val binaryConstraint = ConstrainedClassifier.constraint[String] {
    x: String =>
      (BadgeClassifier on x is "negative") ==> (BadgeOppositClassifier on x is "positive")
  }

  val binaryConstraintOverMultiClassifiers = ConstrainedClassifier.constraint[String] {
    x: String =>
      (BadgeClassifierMulti on x is "negative") ==> (BadgeOppositClassifierMulti on x is "positive")
  }
  object badgeConstrainedClassifier extends ConstrainedClassifier[String, String](BadgeClassifier) {
    def subjectTo = binaryConstraint
    override val solver = new OJalgoHook
  }

  object oppositBadgeConstrainedClassifier extends ConstrainedClassifier[String, String](BadgeOppositClassifier) {
    def subjectTo = binaryConstraint
    override val solver = new OJalgoHook
  }

  object badgeConstrainedClassifierMulti extends ConstrainedClassifier[String, String](BadgeClassifierMulti) {
    def subjectTo = binaryConstraintOverMultiClassifiers
    override val solver = new OJalgoHook
  }

  object oppositBadgeConstrainedClassifierMulti extends ConstrainedClassifier[String, String](BadgeOppositClassifierMulti) {
    def subjectTo = binaryConstraintOverMultiClassifiers
    override val solver = new OJalgoHook
  }

}
