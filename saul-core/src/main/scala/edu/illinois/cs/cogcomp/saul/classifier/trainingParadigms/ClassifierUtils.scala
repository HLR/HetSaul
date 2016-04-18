package edu.illinois.cs.cogcomp.saul.classifier.trainingParadigms

import edu.illinois.cs.cogcomp.saul.classifier.Learnable

object ClassifierUtils {
  object TrainClassifiers {
    //Todo we need to make it work wihtout having T
    def apply[T <: AnyRef](c: (Learnable[T], Iterable[T])*) = {
      c.foreach { case (learner: Learnable[T], trainInstances: Iterable[T]) => learner.learn(10, trainInstances) }
    }

    def apply[T <: AnyRef](iter: Integer, c: (Learnable[T], Iterable[T])*) = {
      c.foreach { case (learner: Learnable[T], trainInstances: Iterable[T]) => learner.learn(iter, trainInstances) }
    }

    def apply[T <: AnyRef](iter: Integer, trainInstances: Iterable[T], c: (Learnable[T])*) = {
      c.foreach { learner: Learnable[T] => learner.learn(iter, trainInstances) }
    }
  }

  object TestClassifiers {
    //Todo we need to make it work wihtout having T
    def apply[T <: AnyRef](c: (Learnable[T], Iterable[T])*) = {
      c.foreach {
        case (learner: Learnable[T], testInstances: Iterable[T]) => learner.test(testInstances)
      }
    }

    def apply[T <: AnyRef](c: Learnable[T]*, testInstances: Iterable[T]) = {
      c.foreach { learner: Learnable[T] => learner.test(testInstances) }
    }
  }

  object ForgetAll {
    def apply(c: Learnable[_]*): Unit = {
      c.foreach((x: Learnable[_]) => x.forget())
    }
  }
}