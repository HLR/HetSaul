package edu.illinois.cs.cogcomp.saul.classifier.trainingParadigms

import edu.illinois.cs.cogcomp.saul.classifier.Learnable

import scala.reflect.ClassTag

object independent_train {
  //Todo we need to make it work wihtout having T
  def apply[T <: AnyRef](c: (Learnable[T], Iterable[T])*)(implicit t: ClassTag[_]) = {
   c.foreach(x => (x._1.learn(10, x._2)))
 }
}

object independent_test {
  //Todo we need to make it work wihtout having T
  def apply [T <: AnyRef](c: (Learnable[T], Iterable[T])*) = {
    c.foreach
    {
      case x: (Learnable[T], Iterable[T]) => {
      x._1.test(x._2)
    }
    }
  }
}

object forgetAll {
  def apply(c: Learnable[_]*): Unit = {
    c.foreach((x: Learnable[_]) => x.forget())
  }
}