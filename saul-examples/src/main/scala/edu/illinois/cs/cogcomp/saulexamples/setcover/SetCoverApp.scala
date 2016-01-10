package edu.illinois.cs.cogcomp.saulexamples.setcover

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._

import scala.collection.JavaConversions._

object SetCoverApp {
  val citiesData = new City("./data/SetCover/example.txt")
  val ns = citiesData.getNeighborhoods.toList

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

  println(containsStationConstrint.createInferenceCondition[Neighborhood](SetCoverSolverDataModel).subjectTo.evalDiscreteValue(citiesData))

  import SetCoverSolverDataModel._

  def main(args: Array[String]) {
    cities populate List(citiesData)
    neighborhoods populate ns
    cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    println(SetCoverSolverDataModel.getFromRelation[City, Neighborhood](citiesData))
    println(SetCoverSolverDataModel.getFromRelation[Neighborhood, City](ns.head))

    citiesData.getNeighborhoods.foreach {
      n => println(n.getNumber + ": " + containsStationConstraint.classifier.discreteValue(n))
    }
    println(containsStationConstraint.getCandidates(citiesData))
  }
}