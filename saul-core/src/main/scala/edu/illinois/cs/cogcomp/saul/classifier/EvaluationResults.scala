package edu.illinois.cs.cogcomp.saul.classifier

/** basic data structure to keep the results */
abstract class AbsractResult() {
  def f1: Double
  def precision: Double
  def recall: Double
}

case class ResultPerLabel(label: String, f1: Double, precision: Double, recall: Double,
  allClasses: Array[String], labeledSize: Int, predictedSize: Int, correctSize: Int) extends AbsractResult

case class OverallResult(f1: Double, precision: Double, recall: Double) extends AbsractResult

case class AverageResult(f1: Double, precision: Double, recall: Double) extends AbsractResult

case class Results(perLabel: Seq[ResultPerLabel], average: AverageResult, overall: OverallResult)