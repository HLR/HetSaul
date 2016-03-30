package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.lbjava.learn.{ Learner, LinearThresholdUnit }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.slf4j.{ Logger, LoggerFactory }

import scala.reflect.ClassTag
/** Created by Parisa on 5/22/15.
  */
object JointTrainSparseNetwork {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  var difference = 0
  def apply[HEAD <: AnyRef](
    dm: DataModel,
    cls: List[ConstrainedClassifier[_, HEAD]]
  )(
    implicit
    headTag: ClassTag[HEAD]
  ) = {

    train[HEAD](dm, cls, 1)
  }

  def apply[HEAD <: AnyRef](
    dm: DataModel,
    cls: List[ConstrainedClassifier[_, HEAD]],
    it: Int
  )(
    implicit
    headTag: ClassTag[HEAD]
  ) = {

    train[HEAD](dm, cls, it)
  }

  def train[HEAD <: AnyRef](
    dm: DataModel,
    cls: List[ConstrainedClassifier[_, HEAD]],
    it: Int
  )(
    implicit
    headTag: ClassTag[HEAD]
  ): Unit = {
    // forall members in collection of the head (dm.t) do
    logger.info("Training iteration: " + it)
    if (it == 0) {
      // Done
      println("difference=", difference)
    } else {
      val allHeads = dm.getNodeWithType[HEAD].getTrainingInstances
      difference = 0
      allHeads.zipWithIndex.foreach {
        case (h, idx) =>
          {
            if (idx % 5000 == 0)
              logger.info(s"Training: $idx examples inferred.")

            cls.foreach {
              case c: ConstrainedClassifier[_, HEAD] => {

                type C = c.LEFT
                //              println("-=-=-=-=-")
                //              println(c.tType)
                //              println(c.headType)
                //              println("-=-=-=-=-")

                val typedC = c.asInstanceOf[ConstrainedClassifier[_, HEAD]]

                //              println(Console.RED + typedC + Console.RESET)

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

                        //                        val simpleResult = typedC.onClassifier.discreteValue(x)
                        //                        println("Constrained Result=", result, "Simple Result", simpleResult)
                        //                        if (!simpleResult.equals(result)) {
                        //                          difference = difference + 1
                        //                        }

                        // The idea is that when the prediction is wrong the LTU of the actual class should be promoted
                        // and the LTU of the predicted class should be demoted.
                        if (!result.equals(trueLabel)) //equals("true") && trueLabel.equals("false")   )
                        {

                          val a = typedC.onClassifier.getExampleArray(x, true)
                          val a0 = a(0).asInstanceOf[Array[Int]] //exampleFeatures
                          val a1 = a(1).asInstanceOf[Array[Double]] // exampleValues
                          val exampleLabels = a(2).asInstanceOf[Array[Int]]
                          val labelValues = a(3).asInstanceOf[Array[Double]]
                          val label = exampleLabels(0)
                          var N = ilearner.net.size();

                          if (label >= N || ilearner.net.get(label) == null) {
                            ilearner.iConjuctiveLables = ilearner.iConjuctiveLables | ilearner.getLabelLexicon.lookupKey(label).isConjunctive();

                            val ltu: LinearThresholdUnit = ilearner.getbaseLTU.clone().asInstanceOf[LinearThresholdUnit]
                            ltu.initialize(ilearner.getnumExamples, ilearner.getnumFeatures);
                            ilearner.net.set(label, ltu);
                            N = label + 1;
                          }
                          // test push
                          val ltu_actual: LinearThresholdUnit = ilearner.getLTU(LTU_actual).clone().asInstanceOf[LinearThresholdUnit] //.net.get(i).asInstanceOf[LinearThresholdUnit]
                          val ltu_predicted: LinearThresholdUnit = ilearner.getLTU(LTU_predicted).clone().asInstanceOf[LinearThresholdUnit]

                          if (ltu_actual != null)
                            ltu_actual.promote(a0, a1, 0.1)

                          if (ltu_predicted != null)
                            ltu_predicted.demote(a0, a1, 0.1)

                        } else {

                        }

                      }

                      trainOnce()
                    }
                }
              }
            }
          }
      }
      train(dm, cls, it - 1)
    }

  }
}
