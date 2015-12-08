package edu.illinois.cs.cogcomp.saul.classifier.trainingParadigms

import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.reflect.ClassTag

object independent_train {
  def apply[T <: AnyRef](c: Learnable[T]*) =
    c.foreach((x: Learnable[T]) => x.learn(10))
}

object independent_test {
  def apply[T <: AnyRef](dm: DataModel, c: Learnable[T]*)(implicit t: ClassTag[T]) = {
    c.foreach {

      case x: Learnable[T] =>
        {
          val typedx = x.asInstanceOf[Learnable[T]]
          typedx.test(dm.getNodeWithType[T].getAllInstances)
        }
    }
  }
}
object forgetAll {
  def apply(c: Learnable[_]*): Unit = {
    c.foreach((x: Learnable[_]) => x.forget())
  }
}