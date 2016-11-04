package edu.illinois.cs.cogcomp.saulexamples.Badge

import edu.illinois.cs.cogcomp.lbjava.learn.{ SparseNetworkLearner, SparsePerceptron }

/** Created by Parisa on 9/13/16.
  */

object BadgeClassifiers {
  import BadgeDataModel._
  import edu.illinois.cs.cogcomp.saul.classifier.Learnable
  object BadgeClassifier extends Learnable[String](badge) {
    def label = BadgeLabel
    override lazy val classifier = new SparsePerceptron()
    override def feature = using(BadgeFeature1)
  }
  object BadgeOppositClassifier extends Learnable[String](badge) {
    def label = BadgeOppositLabel
    override lazy val classifier = new SparsePerceptron()
    override def feature = using(BadgeFeature1)
  }

  object BadgeClassifierMulti extends Learnable[String](badge) {
    def label = BadgeLabel
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(BadgeFeature1)
  }

  object BadgeOppositClassifierMulti extends Learnable[String](badge) {
    def label = BadgeOppositLabel
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(BadgeFeature1)
  }
}