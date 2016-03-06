package edu.illinois.cs.cogcomp.saulexamples.setcover

import scala.collection.JavaConversions._

object SetCoverApp {
  val cityInstances = new City("saul-examples/src/main/resources/SetCover/example.txt")
  val neighborhoodInstances = cityInstances.getNeighborhoods.toList

  def main(args: Array[String]) {
    SetCoverSolverDataModel.cities populate List(cityInstances)
    SetCoverSolverDataModel.neighborhoods populate neighborhoodInstances
    SetCoverSolverDataModel.cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    /** printing the labels for each nrighborhood (whether they are choosen to be covered by a station, or not) */
    cityInstances.getNeighborhoods.foreach {
      n => println(n.getNumber + ": " + ContainsStationConstraint(n))
    }
  }
}