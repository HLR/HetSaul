package edu.illinois.cs.cogcomp.saulexamples.setcover

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.setcover.setCoverSolverDataModel._

import scala.collection.JavaConversions._

object setCoverApp {
  val cityInst = new City("./data/SetCover/example.txt")
  val ns = cityInst.getNeighborhoods.toList

  val containsStationConstrint = ConstrainedClassifier.constraintOf[City]({
    x: City =>
      val containStation = new ContainsStation()
      x.getNeighborhoods _forAll {
        n: Neighborhood =>
          (containStation on n isTrue) ||| (
            n.getNeighbors _exists {
              n2: Neighborhood => containStation on n2 isTrue
            }
          )
      }
  })

  println(containsStationConstrint.createInferenceCondition[Neighborhood].subjectTo.evalDiscreteValue(cityInst))

  def main(args: Array[String]) {
    cities populate List(cityInst)
    neighborhoods populate ns
    cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    println(cities(cityInst) ~> cityContainsNeighborhoods)
    println(neighborhoods(ns.head) ~> -cityContainsNeighborhoods)

    cityInst.getNeighborhoods.foreach {
      n => println(n.getNumber + ": " + containsStationConstraint.classifier.discreteValue(n))
    }
    println(containsStationConstraint.getCandidates(cityInst))
  }
}