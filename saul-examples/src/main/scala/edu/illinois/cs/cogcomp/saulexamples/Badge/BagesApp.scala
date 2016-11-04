package edu.illinois.cs.cogcomp.saulexamples.Badge

/** Created by Parisa on 9/13/16.
  */

import edu.illinois.cs.cogcomp.saul.classifier.SL_model.StructuredLearning
import edu.illinois.cs.cogcomp.saul.classifier.{ClassifierUtils, JointTrain, JointTrainSparseNetwork}
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeClassifiers.{BadgeOppositClassifier, BadgeClassifier}
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeConstraintClassifiers.{badgeConstrainedClassifier, badgeConstrainedClassifierMulti, oppositBadgeConstrainedClassifier, oppositBadgeConstrainedClassifierMulti}
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeDataModel._

import scala.collection.JavaConversions._
object BadgesApp {

  val allNamesTrain = new BadgeReader("data/badges/badges.train").badges
  val allNamesTest = new BadgeReader("data/badges/badges.test").badges

  badge.populate(allNamesTrain)
  badge.populate(allNamesTest, false)

  val cls = List(badgeConstrainedClassifierMulti, oppositBadgeConstrainedClassifierMulti)

  object BadgeExperimentType extends Enumeration {
    val JoinTrainSparsePerceptron, JointTrainSparseNetwork, JointTrainSparseNetworkLossAugmented, JoinTrainSL = Value
  }

  def main(args: Array[String]): Unit = {
    /** Choose the experiment you're interested in by changing the following line */
    val testType = BadgeExperimentType.JointTrainSparseNetworkLossAugmented

    testType match {
      case BadgeExperimentType.JoinTrainSparsePerceptron => JoinTrainSparsePerceptron()
      case BadgeExperimentType.JointTrainSparseNetwork => JoinTrainSparseNetwork()
      case BadgeExperimentType.JointTrainSparseNetworkLossAugmented => LossAugmentedJoinTrainSparseNetwork()
      case BadgeExperimentType.JoinTrainSL => JoinTrainSL()
    }
  }

  /*Test the join training with SparsePerceptron*/
  def JoinTrainSparsePerceptron(): Unit = {
    BadgeClassifier.test()
    BadgeOppositClassifier.test()
    JointTrain.train(BadgeDataModel.badge, List(badgeConstrainedClassifier, oppositBadgeConstrainedClassifier), 5)
    oppositBadgeConstrainedClassifier.test()
    badgeConstrainedClassifier.test()
    BadgeClassifier.test()
  }

  /*Test the joinTraining with SparseNetwork*/
  def JoinTrainSparseNetwork(): Unit = {

    JointTrainSparseNetwork.train(badge, cls, 5, init = true)

    badgeConstrainedClassifierMulti.test()
    oppositBadgeConstrainedClassifierMulti.test()
  }

  /*Test the joinTraining with SparseNetwork and doing loss augmented inference*/
  def LossAugmentedJoinTrainSparseNetwork(): Unit = {

   JointTrainSparseNetwork.train(badge, cls, 5, init =  true,lossAugmented = true)

    badgeConstrainedClassifierMulti.test()
    oppositBadgeConstrainedClassifierMulti.test()
  }

  /*Test the joinTraining with Structured output prediction in SL*/
  def JoinTrainSL(): Unit = {

    ClassifierUtils.InitializeClassifiers(badge, cls: _*)
    val m = StructuredLearning(badge, cls, usePreTrained = false)

    badgeConstrainedClassifierMulti.test()
    oppositBadgeConstrainedClassifier.test()

    StructuredLearning.Evaluate(badge, cls, m, "")

  }

}