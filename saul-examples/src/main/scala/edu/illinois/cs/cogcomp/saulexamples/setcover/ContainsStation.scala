package edu.illinois.cs.cogcomp.saulexamples.setcover

import edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet

class ContainsStation extends DumbLearner("ilp.ContainsStation") {
  override def getInputType: String = {
    "ilp.Neighborhood"
  }

  override def allowableValues: Array[String] = {
    Array[String]("false", "true")
  }

  override def scores(example: AnyRef): ScoreSet = {
    val result: ScoreSet = new ScoreSet
    result.put("false", 0)
    result.put("true", -1)
    result
  }
}