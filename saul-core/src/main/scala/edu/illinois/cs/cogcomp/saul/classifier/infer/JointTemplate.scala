/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.infer

import edu.illinois.cs.cogcomp.infer.ilp.ILPSolver
import edu.illinois.cs.cogcomp.lbjava.infer.{ ParameterizedConstraint, ILPInference }
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
