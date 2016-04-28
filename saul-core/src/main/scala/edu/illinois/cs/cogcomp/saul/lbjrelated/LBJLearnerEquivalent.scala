package edu.illinois.cs.cogcomp.saul.lbjrelated

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.lbjava.learn.{ Lexicon, Learner }
import edu.illinois.cs.cogcomp.saul.constraint.LHSFirstOrderEqualityWithValueLBP

trait LBJLearnerEquivalent extends LBJClassifierEquivalent {
  val classifier: Learner
  def on(t: AnyRef): LHSFirstOrderEqualityWithValueLBP = new LHSFirstOrderEqualityWithValueLBP(this.classifier, t)
  def getLabeler: Classifier = classifier.getLabeler
  def getExampleArray(example: Any): Array[AnyRef] = classifier.getExampleArray(example)
  def getLabelLexicon: Lexicon = classifier.getLabelLexicon
}
