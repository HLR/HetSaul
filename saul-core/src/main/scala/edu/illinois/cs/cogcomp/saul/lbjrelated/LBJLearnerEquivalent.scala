package edu.illinois.cs.cogcomp.saul.lbjrelated

import edu.illinois.cs.cogcomp.lbjava.learn.Learner
import edu.illinois.cs.cogcomp.saul.constraint.LHSFirstOrderEqualityWithValueLBP

trait LBJLearnerEquivalent extends LBJClassifierEquivalent {
  val classifier: Learner
  def on(t: AnyRef): LHSFirstOrderEqualityWithValueLBP = new LHSFirstOrderEqualityWithValueLBP(this.classifier, t)
}
