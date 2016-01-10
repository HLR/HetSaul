package edu.illinois.cs.cogcomp.saulexamples.setcover

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._

import scala.collection.JavaConversions._

object setCoverApp {
  val cities = new City("./data/SetCover/example.txt")
  val ns = cities.getNeighborhoods.toList

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

  println(containsStationConstrint.createInferenceCondition[Neighborhood](setCoverSolverDataModel).subjectTo.evalDiscreteValue(cities))

  def main(args: Array[String]) {
    setCoverSolverDataModel.cities populate List(cities)
    setCoverSolverDataModel.neighborhoods populate ns
    setCoverSolverDataModel.cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    println(setCoverSolverDataModel.getFromRelation[City, Neighborhood](cities))
    println(setCoverSolverDataModel.getFromRelation[Neighborhood, City](ns.head))

    cities.getNeighborhoods.foreach {
      n => println(n.getNumber + ": " + containsStationConstraint.classifier.discreteValue(n))
    }
    println(containsStationConstraint.getCandidates(cities))
  }
}