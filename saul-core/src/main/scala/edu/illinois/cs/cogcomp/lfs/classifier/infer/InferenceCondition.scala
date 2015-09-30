package edu.illinois.cs.cogcomp.lfs.classifier.infer

import edu.illinois.cs.cogcomp.lbjava.infer.ParameterizedConstraint
import edu.illinois.cs.cogcomp.lfs.data_model.DataModel

import edu.illinois.cs.cogcomp.lfs.{JointERTemplate}
import edu.illinois.cs.cogcomp.lfs.constraint.LfsConstraint

import scala.reflect.ClassTag

/**
 * Created by haowu on 1/29/15.
 */
abstract class InferenceCondition[INPUT <: AnyRef,HEAD <: AnyRef](val dm : DataModel) (
  implicit val inputTag : ClassTag[INPUT],
  val headTag : ClassTag[HEAD]
  )  {
  def subjectTo : LfsConstraint[HEAD]


  def transfer(t : HEAD) : JointERTemplate[HEAD] = {
    new JointERTemplate[HEAD](t){
      // TODO: Define this function
      override def getSubjectToInstance : ParameterizedConstraint = {
        subjectTo.transfer
      }
      // TODO: override other functions that needed here
    }
  }

  def apply(head : HEAD) : JointERTemplate[HEAD]  = {
    this.transfer(head)
  }

  val outer = this
  def convertToType[T <: AnyRef](implicit tag : ClassTag[T]) : InferenceCondition[T,HEAD] = this.asInstanceOf[InferenceCondition[T,HEAD]]
//    new InferenceCondition[T,HEAD](outer.dm) {
//    override def subjectTo: LfsConstraint[HEAD] = outer.subjectTo
//  }

}
