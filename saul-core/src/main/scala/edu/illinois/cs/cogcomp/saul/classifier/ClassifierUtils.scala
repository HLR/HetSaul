/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.saul.classifier.infer.InitSparseNetwork
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.util.Logging

/** Utility functions for various operations (e.g. training, testing, saving, etc) on multiple classifiers.
  */
object ClassifierUtils extends Logging {
  val evalSeparator = "==============================================="

  object TrainClassifiers {
    def apply[T <: AnyRef](c: (Learnable[T], Iterable[T])*) = {
      c.foreach {
        case (learner, trainInstances) =>
          logger.info(evalSeparator)
          logger.info("Training " + learner.getClassSimpleNameForClassifier)
          learner.learn(10, trainInstances)
      }
      logger.info(evalSeparator)
    }

    def apply[T <: AnyRef](iter: Integer, c: (Learnable[T], Iterable[T])*) = {
      c.foreach {
        case (learner, trainInstances) =>
          logger.info(evalSeparator)
          logger.info("Training " + learner.getClassSimpleNameForClassifier)
          learner.learn(iter, trainInstances)
      }
      logger.info(evalSeparator)
    }

    def apply[T <: AnyRef](iter: Integer, trainInstances: Iterable[T], c: (Learnable[T])*) = {
      c.foreach { learner =>
        logger.info(evalSeparator)
        logger.info("Training " + learner.getClassSimpleNameForClassifier)
        learner.learn(iter, trainInstances)
      }
      logger.info(evalSeparator)
    }

    def apply(iter: Integer, c: (Learnable[_])*)(implicit d1: DummyImplicit, d2: DummyImplicit) = {
      c.foreach { learner =>
        logger.info(evalSeparator)
        logger.info("Training " + learner.getClassSimpleNameForClassifier)
        learner.learn(iter)
      }
      logger.info(evalSeparator)
    }

    def apply(iter: Integer, c: List[Learnable[_]])(implicit d1: DummyImplicit, d2: DummyImplicit) = {
      c.foreach { learner =>
        logger.info(evalSeparator)
        logger.info("Training " + learner.getClassSimpleNameForClassifier)
        learner.learn(iter)
      }
      logger.info(evalSeparator)
    }
  }

  // TODO: simplify the output type of test
  object TestClassifiers {
    def apply[T <: AnyRef](c: (Learnable[T], Iterable[T])*): Seq[Results] = {
      val testResults = c.map {
        case (learner, testInstances) =>
          logger.info(evalSeparator)
          logger.info("Evaluating " + learner.getClassSimpleNameForClassifier)
          learner.test(testInstances)
      }
      logger.info(evalSeparator)
      testResults
    }

    def apply[T <: AnyRef](testInstances: Iterable[T], c: Learnable[T]*): Seq[Results] = {
      val testResults = c.map { learner =>
        logger.info(evalSeparator)
        logger.info("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test(testInstances)
      }
      logger.info(evalSeparator)
      testResults
    }

    def apply(c: Learnable[_]*)(implicit d1: DummyImplicit, d2: DummyImplicit): Seq[Results] = {
      val testResults = c.map { learner =>
        logger.info(evalSeparator)
        logger.info("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test()
      }
      logger.info(evalSeparator)
      testResults
    }

    def apply(c: List[Learnable[_]])(implicit d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): Seq[Results] = {
      val testResults = c.map { learner =>
        logger.info(evalSeparator)
        logger.info("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test()
      }
      logger.info(evalSeparator)
      testResults
    }

    def apply(c: ConstrainedClassifier[_, _]*)(implicit d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): Seq[Results] = {
      val testResults = c.map { learner =>
        logger.info(evalSeparator)
        logger.info("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test()
      }
      logger.info(evalSeparator)
      testResults
    }

    def apply[T <: AnyRef](testInstances: Iterable[T], c: ConstrainedClassifier[T, _]*)(implicit d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): Seq[Results] = {
      val testResults = c.map { learner =>
        logger.info(evalSeparator)
        logger.info("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test(testInstances)
      }
      logger.info(evalSeparator)
      testResults
    }

    def apply[T <: AnyRef](instanceClassifierPairs: (Iterable[T], ConstrainedClassifier[T, _])*)(implicit d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit, d4: DummyImplicit): Seq[Results] = {
      val testResults = instanceClassifierPairs.map {
        case (testInstances, learner) =>
          logger.info(evalSeparator)
          logger.info("Evaluating " + learner.getClassSimpleNameForClassifier)
          learner.test(testInstances)
      }
      logger.info(evalSeparator)
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

  object InitializeClassifiers {
    def apply[HEAD <: AnyRef](node: Node[HEAD], cl: ConstrainedClassifier[_, HEAD]*) = {
      cl.map {
        constrainedLearner =>
          InitSparseNetwork(node, constrainedLearner)
      }
    }
  }
  /** some utility functions for playing arounds results of classifiers */
  private def resultToList(someResult: AbstractResult): List[Double] = {
    List(someResult.f1, someResult.precision, someResult.recall)
  }

  def getAverageResults(perLabelResults: Seq[ResultPerLabel]): AverageResult = {
    val avgResultList = perLabelResults.toList.map(resultToList).transpose.map(a => a.sum / perLabelResults.length)
    AverageResult(avgResultList(0), avgResultList(1), avgResultList(2))
  }
}

