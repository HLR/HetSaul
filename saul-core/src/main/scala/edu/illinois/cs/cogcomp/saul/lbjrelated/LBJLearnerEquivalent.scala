/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.lbjrelated

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.lbjava.learn.{ Lexicon, Learner }
import edu.illinois.cs.cogcomp.saul.constraint.LHSFirstOrderEqualityWithValueLBP

/** Encapsulates an instance of LBJava's [[Learner]] class.
  */
trait LBJLearnerEquivalent extends LBJClassifierEquivalent {

  val classifier: Learner

  def on(t: AnyRef): LHSFirstOrderEqualityWithValueLBP = new LHSFirstOrderEqualityWithValueLBP(this.classifier, t)

  def getLabeler: Classifier = classifier.getLabeler

  def getExampleArray(example: Any): Array[AnyRef] = classifier.getExampleArray(example)

  def getLabelLexicon: Lexicon = classifier.getLabelLexicon
}
