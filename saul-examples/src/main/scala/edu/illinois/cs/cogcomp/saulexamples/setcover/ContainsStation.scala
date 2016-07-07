/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.setcover

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, ScoreSet }
import edu.illinois.cs.cogcomp.lbjava.learn.Learner

import java.io.PrintStream

/** This is a classifier defined for the setcover problem. Most of the functions are not completely defined, because
  * there is no learning involved in this problem.
  */
class ContainsStation extends Learner("ilp.ContainsStation") {
  override def getInputType: String = { "ilp.Neighborhood" }

  override def allowableValues: Array[String] = { Array[String]("false", "true") }

  override def equals(o: Any): Boolean = { getClass == o.getClass }

  /** The reason for true to be -1 is because the internal optimization by default finds the maximizer, while in this
    * problem we are looking for a minimizer
    */
  override def scores(example: AnyRef): ScoreSet = {
    val result: ScoreSet = new ScoreSet
    result.put("false", 0)
    result.put("true", -1)
    result
  }

  override def write(printStream: PrintStream): Unit = ???

  override def scores(ints: Array[Int], doubles: Array[Double]): ScoreSet = ???

  override def classify(ints: Array[Int], doubles: Array[Double]): FeatureVector = ???

  override def learn(ints: Array[Int], doubles: Array[Double], ints1: Array[Int], doubles1: Array[Double]): Unit = ???
}