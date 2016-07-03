/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.lbjava.learn.LinearThresholdUnit
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

import scala.reflect.ClassTag

/** Created by parisakordjamshidi on 29/01/15.
  */
object JointTrain {
  def testClassifiers(cls: Classifier, oracle: Classifier, ds: List[AnyRef]): Unit = {

    val results = ds.map({
      x =>
        val pri = cls.discreteValue(x)
        val truth = oracle.discreteValue(x)
        (pri, truth)
    })

    val tp = results.count({ case (x, y) => x == y && (x == "true") }) * 1.0
    val fp = results.count({ case (x, y) => x != y && (x == "true") }) * 1.0

    val tn = results.count({ case (x, y) => x == y && (x == "false") }) * 1.0
    val fn = results.count({ case (x, y) => x != y && (x == "false") }) * 1.0

    println(s"tp: $tp fp: $fp tn: $tn fn: $fn ")
    println(s" accuracy    ${(tp + tn) / results.size} ")
    println(s" precision   ${tp / (tp + fp)} ")
    println(s" recall      ${tp / (tp + fn)} ")
    println(s" f1          ${(2.0 * tp) / (2 * tp + fp + fn)} ")

  }

  def apply[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]])(implicit headTag: ClassTag[HEAD]) = {
    train[HEAD](node, cls, 1)
  }

  def apply[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]], it: Int)(implicit headTag: ClassTag[HEAD]) = {
    train[HEAD](node, cls, it)
  }

  @scala.annotation.tailrec
  def train[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]], it: Int)(implicit headTag: ClassTag[HEAD]): Unit = {
    // forall members in collection of the head (dm.t) do

    println("Training iteration: " + it)
    if (it == 0) {
      // Done
    } else {
      val allHeads = node.getTrainingInstances

      allHeads foreach {
        h =>
          {
            cls.foreach {
              case classifier: ConstrainedClassifier[_, HEAD] =>
                val typedC = classifier.asInstanceOf[ConstrainedClassifier[_, HEAD]]
                val oracle = typedC.onClassifier.getLabeler

                typedC.getCandidates(h) foreach {
                  x =>
                    {
                      def trainOnce() = {
                        val result = typedC.classifier.discreteValue(x)
                        val trueLabel = oracle.discreteValue(x)

                        if (result.equals("true") && trueLabel.equals("false")) {
                          val a = typedC.onClassifier.getExampleArray(x)
                          val a0 = a(0).asInstanceOf[Array[Int]]
                          val a1 = a(1).asInstanceOf[Array[Double]]
                          typedC.onClassifier.classifier.asInstanceOf[LinearThresholdUnit].promote(a0, a1, 0.1)
                        } else if (result.equals("false") && trueLabel.equals("true")) {
                          val a = typedC.onClassifier.getExampleArray(x)
                          val a0 = a(0).asInstanceOf[Array[Int]]
                          val a1 = a(1).asInstanceOf[Array[Double]]
                          typedC.onClassifier.classifier.asInstanceOf[LinearThresholdUnit].demote(a0, a1, 0.1)
                        }
                      }
                      trainOnce()
                    }
                }
            }
          }
      }
      train(node, cls, it - 1)
    }

  }

}
