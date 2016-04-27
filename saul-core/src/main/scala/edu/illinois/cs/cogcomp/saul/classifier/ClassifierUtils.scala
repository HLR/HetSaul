package edu.illinois.cs.cogcomp.saul.classifier

/** Utility functions for various operations (e.g. training, testing, saving, etc) on multiple classifiers.
  */
object ClassifierUtils {
  val evalSeparator = "==============================================="

  object TrainClassifiers {
    def apply[T <: AnyRef](c: (Learnable[T], Iterable[T])*) = {
      c.foreach {
        case (learner, trainInstances) =>
          println(evalSeparator)
          println("Training " + learner.getClassSimpleNameForClassifier)
          learner.learn(10, trainInstances)
      }
      println(evalSeparator)
    }

    def apply[T <: AnyRef](iter: Integer, c: (Learnable[T], Iterable[T])*) = {
      c.foreach {
        case (learner, trainInstances) =>
          println(evalSeparator)
          println("Training " + learner.getClassSimpleNameForClassifier)
          learner.learn(iter, trainInstances)
      }
      println(evalSeparator)
    }

    def apply[T <: AnyRef](iter: Integer, trainInstances: Iterable[T], c: (Learnable[T])*) = {
      c.foreach { learner =>
        println(evalSeparator)
        println("Training " + learner.getClassSimpleNameForClassifier)
        learner.learn(iter, trainInstances)
      }
      println(evalSeparator)
    }

    def apply(iter: Integer, c: (Learnable[_])*)(implicit d1: DummyImplicit, d2: DummyImplicit) = {
      c.foreach { learner =>
        println(evalSeparator)
        println("Training " + learner.getClassSimpleNameForClassifier)
        learner.learn(iter)
      }
      println(evalSeparator)
    }
  }

  // TODO: simplify the output type of test
  object TestClassifiers {
    def apply[T <: AnyRef](c: (Learnable[T], Iterable[T])*): Seq[List[(String, (Double, Double, Double))]] = {
      val testResults = c.map {
        case (learner, testInstances) =>
          println(evalSeparator)
          println("Evaluating " + learner.getClassSimpleNameForClassifier)
          learner.test(testInstances)
      }
      println(evalSeparator)
      testResults
    }

    def apply[T <: AnyRef](testInstances: Iterable[T], c: Learnable[T]*): Seq[List[(String, (Double, Double, Double))]] = {
      val testResults = c.map { learner =>
        println(evalSeparator)
        println("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test(testInstances)
      }
      println(evalSeparator)
      testResults
    }

    def apply(c: Learnable[_]*)(implicit d1: DummyImplicit, d2: DummyImplicit): Seq[List[(String, (Double, Double, Double))]] = {
      val testResults = c.map { learner =>
        println(evalSeparator)
        println("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test()
      }
      println(evalSeparator)
      testResults
    }

    def apply(c: ConstrainedClassifier[_, _]*)(implicit d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): Seq[List[(String, (Double, Double, Double))]] = {
      val testResults = c.map { learner =>
        println(evalSeparator)
        println("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test()
      }
      println(evalSeparator)
      testResults
    }
  }

  object ForgetAll {
    def apply(c: Learnable[_]*): Unit = {
      c.foreach((x: Learnable[_]) => x.forget())
    }
  }

  object SaveClassifiers {
    def apply(c: Learnable[_]*): Unit = {
      c.foreach((x: Learnable[_]) => x.save())
    }
  }

  object LoadClassifier {
    def apply(c: Learnable[_]*): Unit = {
      c.foreach { x =>
        val prefix = x.getClassNameForClassifier
        x.load(prefix + ".lc", prefix + ".lex")
      }
    }

    def apply(modelPath: String, c: Learnable[_]*)(implicit d1: DummyImplicit): Unit = {
      c.foreach { x =>
        val prefix = modelPath + x.getClassNameForClassifier
        x.load(prefix + ".lc", prefix + ".lex")
      }
    }
  }
}
