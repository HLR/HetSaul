package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLConstraints._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel.{ pairToFirstArg, pairToSecondArg }
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ Phrase, Relation, Token }
/** Created by parisakordjamshidi on 2/4/17.
  */
object MultiModalConstrainedClassifiers {

  val erSolver = new OJalgoHook

  object TRPairConstraintClassifier extends ConstrainedClassifier[Relation, Relation](TrajectorPairClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    //override val pathToHead = Some()
  }
  object LMPairConstraintClassifier extends ConstrainedClassifier[Relation, Relation](LandmarkPairClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    //override val pathToHead = Some()
  }

  object LMConstraintClassifier extends ConstrainedClassifier[Phrase, Relation](LandmarkRoleClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    override val pathToHead = Some(-pairToFirstArg)
  }

  object TRConstraintClassifier extends ConstrainedClassifier[Phrase, Relation](TrajectorRoleClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    override val pathToHead = Some(-pairToFirstArg)
  }

  object IndicatorConstraintClassifier extends ConstrainedClassifier[Phrase, Relation](IndicatorRoleClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    override val pathToHead = Some(-pairToSecondArg)
  }
}
