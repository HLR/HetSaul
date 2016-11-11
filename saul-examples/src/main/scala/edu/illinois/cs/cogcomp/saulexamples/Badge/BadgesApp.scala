/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.Badge

/** Created by Parisa on 9/13/16.
  */

import edu.illinois.cs.cogcomp.saul.classifier.{ JointTrainSparseNetwork, JointTrainSparsePerceptron }
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeConstrainedClassifiers.{ badgeConstrainedClassifier, badgeConstrainedClassifierMulti, oppositBadgeConstrainedClassifier, oppositBadgeConstrainedClassifierMulti }
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeDataModel._

import scala.collection.JavaConversions._
object BadgesApp {

  val allNamesTrain = new BadgeReader("data/badges/badges.train").badges
  val allNamesTest = new BadgeReader("data/badges/badges.test").badges

  badge.populate(allNamesTrain)
  badge.populate(allNamesTest, false)

  val cls = List(badgeConstrainedClassifierMulti, oppositBadgeConstrainedClassifierMulti)

  object BadgeExperimentType extends Enumeration {
    val JoinTrainSparsePerceptron, JointTrainSparseNetwork, JointTrainSparseNetworkLossAugmented, Pipeline = Value
  }

  def main(args: Array[String]): Unit = {

    /** Choose the experiment you're interested in by changing the following line */
    val testType = BadgeExperimentType.Pipeline

    testType match {
      case BadgeExperimentType.JoinTrainSparsePerceptron => JoinTrainSparsePerceptron()
      case BadgeExperimentType.JointTrainSparseNetwork => JoinTrainSparseNetwork()
      case BadgeExperimentType.JointTrainSparseNetworkLossAugmented => LossAugmentedJoinTrainSparseNetwork()
      case BadgeExperimentType.Pipeline => Pipeline()
    }
  }

  /*Test the join training with SparsePerceptron*/
  def JoinTrainSparsePerceptron(): Unit = {
    BadgeClassifier.test()
    BadgeOppositClassifier.test()
    JointTrainSparsePerceptron.train(BadgeDataModel.badge, List(badgeConstrainedClassifier, oppositBadgeConstrainedClassifier), 5)
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

    JointTrainSparseNetwork.train(badge, cls, 5, init = true, lossAugmented = true)

    badgeConstrainedClassifierMulti.test()
    oppositBadgeConstrainedClassifierMulti.test()
  }

  /* This model trains the BadgeClassifier and then it takes the prediction of the BadgeClassifier as the only input
  feature and trains a pipeline function to predict the opposite label*/
  def Pipeline(): Unit = {
    BadgeClassifier.learn(5)
    BadgeClassifier.test()
    BadgeOppositPipeline.learn(5)
    BadgeOppositPipeline.test()
  }

}