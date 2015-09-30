package edu.illinois.cs.cogcomp.lfs.lbj_related

import edu.illinois.cs.cogcomp.lbjava.learn.Learner
import edu.illinois.cs.cogcomp.lfs.constraint.LHSFirstOrderEqualityWithValueLBP

/** Created by haowu on 1/28/15.
  */
trait LBJLearnerEquivalent extends LBJClassifierEquivalent {
  val classifier: Learner
  def on(t: AnyRef): LHSFirstOrderEqualityWithValueLBP = new LHSFirstOrderEqualityWithValueLBP(this.classifier, t)
}
