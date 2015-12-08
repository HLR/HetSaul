package edu.illinois.cs.cogcomp.saul.classifier.trainingParadigms

import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.reflect.ClassTag

object independent_train {
  def apply[T <: AnyRef](c: Learnable[T]*) =
    c.foreach((x: Learnable[T]) => x.learn(10))
}

object independent_test {
  def apply(dm : DataModel, c: Learnable[_]*)(implicit t:ClassTag[_]*) =

    c.foreach( (x: Learnable[_]) => (x: Learnable[_]) => x.test(dm.getNodeWithType[_].getAllInstances))
}
object forgetAll {
  def apply(c: Learnable[_]*): Unit ={
    c.foreach((x: Learnable[_]) => x.forget())
  }
}