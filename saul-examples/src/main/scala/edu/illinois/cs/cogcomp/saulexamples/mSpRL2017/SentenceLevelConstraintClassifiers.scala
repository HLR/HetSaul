package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.SentenceLevelConstraints._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ Relation, Sentence, Phrase }

/** Created by parisakordjamshidi on 2/9/17.
  */
object SentenceLevelConstraintClassifiers {

  val erSolver = new OJalgoHook

  object TRPairConstraintClassifier extends ConstrainedClassifier[Relation, Sentence](TrajectorPairClassifier) {
    def subjectTo = allConstraints

    override val solver = erSolver
    override val pathToHead = Some(-sentenceToPairs)
  }

  object LMPairConstraintClassifier extends ConstrainedClassifier[Relation, Sentence](LandmarkPairClassifier) {
    def subjectTo = allConstraints

    override val solver = erSolver
    override val pathToHead = Some(-sentenceToPairs)
  }

  object LMConstraintClassifier extends ConstrainedClassifier[Phrase, Sentence](LandmarkRoleClassifier) {
    def subjectTo = allConstraints

    override val solver = erSolver
    override val pathToHead = Some(-sentenceToPhrase)
  }

  object TRConstraintClassifier extends ConstrainedClassifier[Phrase, Sentence](TrajectorRoleClassifier) {
    def subjectTo = allConstraints

    override val solver = erSolver
    override val pathToHead = Some(-sentenceToPhrase)
  }

  object IndicatorConstraintClassifier extends ConstrainedClassifier[Phrase, Sentence](IndicatorRoleClassifier) {
    def subjectTo = allConstraints

    override val solver = erSolver
    override val pathToHead = Some(-sentenceToPhrase)
  }

}
