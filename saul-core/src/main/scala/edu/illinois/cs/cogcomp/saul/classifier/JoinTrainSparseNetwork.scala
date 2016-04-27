package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.lbjava.learn.{ Learner, LinearThresholdUnit }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

import scala.reflect.ClassTag
/** Created by Parisa on 5/22/15.
  */
object JoinTrainSparseNetwork {

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
              case c: ConstrainedClassifier[_, HEAD] =>
                type C = c.LEFT
                val typedC = c.asInstanceOf[ConstrainedClassifier[_, HEAD]]

                val oracle = typedC.onClassifier.getLabeler

                typedC.getCandidates(h) foreach {
                  x =>
                    {
                      def trainOnce() = {
                        val result = typedC.classifier.discreteValue(x)
                        val trueLabel = oracle.discreteValue(x)
                        val ilearner = typedC.onClassifier.asInstanceOf[Learner].asInstanceOf[SparseNetworkLBP]
                        val lLexicon = typedC.onClassifier.getLabelLexicon
                        var LTU_actual: Int = 0
                        var LTU_predicted: Int = 0
                        for (i <- 0 until lLexicon.size()) {
                          if (lLexicon.lookupKey(i).valueEquals(result))
                            LTU_predicted = i
                          if (lLexicon.lookupKey(i).valueEquals(trueLabel))
                            LTU_actual = i
                        }
                        // The idea is that when the prediction is wrong the LTU of the actual class should be promoted
                        // and the LTU of the predicted class should be demoted.
                        if (!result.equals(trueLabel)) //equals("true") && trueLabel.equals("false")   )
                        {
                          val a = typedC.onClassifier.getExampleArray(x)
                          val a0 = a(0).asInstanceOf[Array[Int]] //exampleFeatures
                          val a1 = a(1).asInstanceOf[Array[Double]] // exampleValues
                          val exampleLabels = a(2).asInstanceOf[Array[Int]]
                          val label = exampleLabels(0)
                          var N = ilearner.net.size()

                          if (label >= N || ilearner.net.get(label) == null) {
                            ilearner.iConjuctiveLables = ilearner.iConjuctiveLables | ilearner.getLabelLexicon.lookupKey(label).isConjunctive

                            val ltu: LinearThresholdUnit = ilearner.getbaseLTU
                            ltu.initialize(ilearner.getnumExamples, ilearner.getnumFeatures)
                            ilearner.net.set(label, ltu)
                            N = label + 1

                          }
                          // test push
                          val ltu_actual: LinearThresholdUnit = ilearner.getLTU(LTU_actual) //.net.get(i).asInstanceOf[LinearThresholdUnit]
                          val ltu_predicted: LinearThresholdUnit = ilearner.getLTU(LTU_predicted)
                          if (ltu_actual != null)
                            ltu_actual.promote(a0, a1, 0.1)
                          if (ltu_predicted != null)
                            ltu_predicted.demote(a0, a1, 0.1)
                        } else {}
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
