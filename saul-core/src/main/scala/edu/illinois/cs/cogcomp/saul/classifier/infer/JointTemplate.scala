package edu.illinois.cs.cogcomp.saul.classifier.infer

import edu.illinois.cs.cogcomp.lbjava.infer.{ ILPSolver, ParameterizedConstraint, ILPInference }
import edu.illinois.cs.cogcomp.lbjava.learn.{ IdentityNormalizer, Normalizer, Learner }

abstract class JointTemplate[T](head: T, solver: ILPSolver) extends ILPInference(head, solver) {
  constraint = this.getSubjectToInstance.makeConstraint(head)

  override def getHeadType: String = {
    "T"
  }

  override def getHeadFinderTypes: Array[String] = {
    Array[String](null, null)
  }

  override def getNormalizer(c: Learner): Normalizer = {
    new IdentityNormalizer
  }

  def getSubjectToInstance: ParameterizedConstraint
}
