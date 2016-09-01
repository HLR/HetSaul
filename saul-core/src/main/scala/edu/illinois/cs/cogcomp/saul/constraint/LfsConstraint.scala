/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.constraint

import edu.illinois.cs.cogcomp.infer.ilp.ILPSolver
import edu.illinois.cs.cogcomp.lbjava.infer.{ ParameterizedConstraint, FirstOrderConstraint }
import edu.illinois.cs.cogcomp.saul.classifier.infer.InferenceCondition

import scala.reflect.ClassTag

abstract class LfsConstraint[T <: AnyRef] {

  def makeConstrainDef(x: T): FirstOrderConstraint

  def evalDiscreteValue(t: T): String = {
    this.makeConstrainDef(t).evaluate().toString
  }

  def apply(t: T) = makeConstrainDef(t)

  def transfer: ParameterizedConstraint = {
    new ParameterizedConstraint() {
      override def makeConstraint(__example: AnyRef): FirstOrderConstraint = {
        val t: T = __example.asInstanceOf[T]
        makeConstrainDef(t)
      }

      override def discreteValue(__example: AnyRef): String =
        {
          val t: T = __example.asInstanceOf[T]
          evalDiscreteValue(t)
          //Todo type check error catch
        }
    }
  }

  val lc = this

  def createInferenceCondition[C <: AnyRef](solver: ILPSolver): InferenceCondition[C, T] = {
    new InferenceCondition[C, T](solver) {
      override def subjectTo: LfsConstraint[T] = lc
    }
  }
}
