/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.lbjava.learn.{ LinearThresholdUnit, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import org.slf4j.{ Logger, LoggerFactory }

import scala.reflect.ClassTag

/** Created by Parisa on 5/22/15.
  */
object JointTrainSparseNetwork {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  var difference = 0
  def apply[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]], init: Boolean)(implicit headTag: ClassTag[HEAD]) = {
    train[HEAD](node, cls, 1, init)
  }

  def apply[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]], it: Int, init: Boolean)(implicit headTag: ClassTag[HEAD]) = {
    train[HEAD](node, cls, it, init)
  }

  @scala.annotation.tailrec
  def train[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]], it: Int, init: Boolean)(implicit headTag: ClassTag[HEAD]): Unit = {
    // forall members in collection of the head (dm.t) do
    logger.info("Training iteration: " + it)
    if (init) ClassifierUtils.InitializeClassifiers(node, cls: _*)
    if (it == 0) {
      // Done
      println("difference=", difference)
    } else {
      val allHeads = node.getTrainingInstances
      difference = 0
      allHeads.zipWithIndex.foreach {
        case (h, idx) =>
          {
            if (idx % 5000 == 0)
              logger.info(s"Training: $idx examples inferred.")

            cls.foreach {
              case classifier: ConstrainedClassifier[_, HEAD] =>
                val typedClassifier = classifier.asInstanceOf[ConstrainedClassifier[_, HEAD]]
                val oracle = typedClassifier.onClassifier.getLabeler

                typedClassifier.getCandidates(h) foreach {
                  candidate =>
                    {
                      def trainOnce() = {
                        val result = typedClassifier.classifier.discreteValue(candidate)
                        val trueLabel = oracle.discreteValue(candidate)
                        val ilearner = typedClassifier.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
                        val lLexicon = typedClassifier.onClassifier.getLabelLexicon
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
                          val a = typedClassifier.onClassifier.getExampleArray(candidate)
                          val a0 = a(0).asInstanceOf[Array[Int]] //exampleFeatures
                          val a1 = a(1).asInstanceOf[Array[Double]] // exampleValues
                          val exampleLabels = a(2).asInstanceOf[Array[Int]]
                          val label = exampleLabels(0)
                          var N = ilearner.getNetwork.size

                          if (label >= N || ilearner.getNetwork.get(label) == null) {
                            val conjugateLabels = ilearner.isUsingConjunctiveLabels | ilearner.getLabelLexicon.lookupKey(label).isConjunctive
                            ilearner.setConjunctiveLabels(conjugateLabels)

                            val ltu: LinearThresholdUnit = ilearner.getBaseLTU
                            ltu.initialize(ilearner.getNumExamples, ilearner.getNumFeatures)
                            ilearner.getNetwork.set(label, ltu)
                            N = label + 1
                          }

                          // test push
                          val ltu_actual = ilearner.getLTU(LTU_actual).asInstanceOf[LinearThresholdUnit]
                          val ltu_predicted = ilearner.getLTU(LTU_predicted).asInstanceOf[LinearThresholdUnit]

                          if (ltu_actual != null)
                            ltu_actual.promote(a0, a1, 0.1)
                          if (ltu_predicted != null)
                            ltu_predicted.demote(a0, a1, 0.1)
                        }
                      }

                      trainOnce()
                    }
                }
            }
          }
      }
      train(node, cls, it - 1, false)
    }
  }
}
