package edu.illinois.cs.cogcomp.saul.classifier.infer

import edu.illinois.cs.cogcomp.lbjava.infer.{ ILPSolver, ParameterizedConstraint }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.constraint.LfsConstraint

import scala.reflect.ClassTag

abstract class InferenceCondition[INPUT <: AnyRef, HEAD <: AnyRef](val dm: DataModel, solver: ILPSolver)(
  implicit
  val inputTag: ClassTag[INPUT],
  val headTag: ClassTag[HEAD]
) {
  def subjectTo: LfsConstraint[HEAD]

  def transfer(t: HEAD): JointTemplate[HEAD] = {
    new JointTemplate[HEAD](t, solver) {
      // TODO: Define this function
      override def getSubjectToInstance: ParameterizedConstraint = {
        subjectTo.transfer
      }
      // TODO: override other functions that needed here
    }
  }

  def apply(head: HEAD): JointTemplate[HEAD] = {
    this.transfer(head)
  }

  val outer = this
  def convertToType[T <: AnyRef](implicit tag: ClassTag[T]): InferenceCondition[T, HEAD] = this.asInstanceOf[InferenceCondition[T, HEAD]]
  //    new InferenceCondition[T,HEAD](outer.dm) {
  //    override def subjectTo: LfsConstraint[HEAD] = outer.subjectTo
  //  }

}
