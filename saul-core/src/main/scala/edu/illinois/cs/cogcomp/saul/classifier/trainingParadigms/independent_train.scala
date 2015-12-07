package edu.illinois.cs.cogcomp.saul.classifier.trainingParadigms

import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.reflect.ClassTag

object independent_train {
  def apply[T <: AnyRef](c: List[Learnable[T]]) =
    c.foreach((x: Learnable[T]) => x.learn(10))
}

object independent_test {
  def apply[T <: AnyRef](c: List[Learnable[T]], dm : DataModel)(implicit t:ClassTag[T]) =
    c.foreach((x: Learnable[T]) => x.test(dm.getNodeWithType[T].getAllInstances))
}
object forgetAll {
  def apply[T <: AnyRef](c: List[Learnable[T]]): Unit ={
    c.foreach((x: Learnable[T]) => x.forget())
  }
}