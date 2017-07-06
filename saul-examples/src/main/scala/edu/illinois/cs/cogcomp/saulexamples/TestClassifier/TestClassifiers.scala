/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.TestClassifier

import edu.illinois.cs.cogcomp.lbjava.learn.{ SparseNetworkLearner, SparsePerceptron, SupportVectorMachine }
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import TestClassifierDataModel._

/** Created by Umar Manzoor on 02/02/17.
  */

object TestClassifiers {

  private val mixFeatures = List(realFeatures, boolValue, intValue)
  /*a binary classifier that predicts the label*/
  object TestClassifierSVM extends Learnable(tcData) {
    def label = dataLabel
    override lazy val classifier = new SupportVectorMachine()
    override def feature = using(mixFeatures)
  }

  object TestClassifierSN extends Learnable[TestClassifierData](tcData) {
    def label = dataLabel
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(mixFeatures)
  }

  object TestClassifierSN2 extends Learnable[TestClassifierData](tcData) {
    def label = dataLabel
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(realFeatures)
  }
}