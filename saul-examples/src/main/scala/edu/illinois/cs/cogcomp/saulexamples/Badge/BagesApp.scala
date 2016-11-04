/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.Badge

/** Created by Parisa on 9/13/16.
  */

import edu.illinois.cs.cogcomp.saul.classifier.{ JointTrain, JointTrainSparseNetwork }
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeClassifiers.{ BadgeOppositClassifier, BadgeClassifier }
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeConstraintClassifiers.{ badgeConstrainedClassifier, badgeConstrainedClassifierMulti, oppositBadgeConstrainedClassifier, oppositBadgeConstrainedClassifierMulti }
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeDataModel._

import scala.collection.JavaConversions._
object BadgesApp {

  val allNamesTrain = new BadgeReader("data/badges/badges.train").badges
  val allNamesTest = new BadgeReader("data/badges/badges.test").badges

  badge.populate(allNamesTrain)
  badge.populate(allNamesTest, false)

  val cls = List(badgeConstrainedClassifierMulti, oppositBadgeConstrainedClassifierMulti)

  object BadgeExperimentType extends Enumeration {
    val JoinTrainSparsePerceptron, JointTrainSparseNetwork, JointTrainSparseNetworkLossAugmented = Value
  }

  def main(args: Array[String]): Unit = {

    /** Choose the experiment you're interested in by changing the following line */
    val testType = BadgeExperimentType.JointTrainSparseNetworkLossAugmented

    testType match {
      case BadgeExperimentType.JoinTrainSparsePerceptron => JoinTrainSparsePerceptron()
      case BadgeExperimentType.JointTrainSparseNetwork => JoinTrainSparseNetwork()
      case BadgeExperimentType.JointTrainSparseNetworkLossAugmented => LossAugmentedJoinTrainSparseNetwork()
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

    JointTrainSparseNetwork.train(badge, cls, 5, init = true, lossAugmented = true)

    badgeConstrainedClassifierMulti.test()
    oppositBadgeConstrainedClassifierMulti.test()
  }

}