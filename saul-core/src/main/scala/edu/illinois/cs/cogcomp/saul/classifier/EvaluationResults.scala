/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier

/** basic data structure to keep the results */
abstract class AbstractResult() {
  def f1: Double
  def precision: Double
  def recall: Double
}

case class ResultPerLabel(label: String, f1: Double, precision: Double, recall: Double,
  allClasses: Array[String], labeledSize: Int, predictedSize: Int, correctSize: Int) extends AbstractResult

case class OverallResult(f1: Double, precision: Double, recall: Double) extends AbstractResult

case class AverageResult(f1: Double, precision: Double, recall: Double) extends AbstractResult

case class Results(perLabel: Seq[ResultPerLabel], average: AverageResult, overall: OverallResult)