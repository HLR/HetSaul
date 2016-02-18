package edu.illinois.cs.cogcomp.saul.constraint

import edu.illinois.cs.cogcomp.lbjava.infer.{ FirstOrderConstraint, ParameterizedConstraint }
import edu.illinois.cs.cogcomp.saul.classifier.infer.InferenceCondition

import scala.reflect.ClassTag

/** Created by haowu on 1/29/15.
  */
abstract class LfsConstraint[T <: AnyRef](implicit val tag: ClassTag[T]) {

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

  def createInferenceCondition[C <: AnyRef]()(implicit cTag: ClassTag[C]): InferenceCondition[C, T] = {
    new InferenceCondition[C, T]() {
      override def subjectTo: LfsConstraint[T] = lc
    }
  }
}
