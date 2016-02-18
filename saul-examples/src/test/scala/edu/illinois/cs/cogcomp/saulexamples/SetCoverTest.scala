package edu.illinois.cs.cogcomp.saulexamples

import edu.illinois.cs.cogcomp.saulexamples.setcover.{ ContainsStationConstraint, Neighborhood, City, SetCoverSolverDataModel }
import org.scalatest.{ Matchers, FlatSpec }
import scala.collection.JavaConversions._

class SetCoverTest extends FlatSpec with Matchers {

  val prefix = "../saul-examples/src/main/resources/SetCover/"

  "SetCover " should " be solved correctly for example.txt " in {
    SetCoverSolverDataModel.clearInstances
    val citiesInstance = new City(prefix + "example.txt")
    val neighborhoodInstances = citiesInstance.getNeighborhoods.toList

    SetCoverSolverDataModel.cities populate List(citiesInstance)
    SetCoverSolverDataModel.neighborhoods populate neighborhoodInstances
    SetCoverSolverDataModel.cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    val neighborhoodLabels = Map(1 -> true, 2 -> false, 3 -> false, 4 -> false, 5 -> false, 6 -> true,
      7 -> false, 8 -> false, 9 -> false)

    citiesInstance.getNeighborhoods.forall { n =>
      ContainsStationConstraint(n) == neighborhoodLabels(n.getNumber).toString
    } should be(true)
    val neighborhoodOutput = "List(neighborhood #1, neighborhood #2, neighborhood #3, neighborhood #4, neighborhood #5, neighborhood #6, neighborhood #7, neighborhood #8, neighborhood #9)"
    ContainsStationConstraint.getCandidates(citiesInstance).toString should be(neighborhoodOutput)
    SetCoverSolverDataModel.getFromRelation[City, Neighborhood](citiesInstance).toList.toString should be(neighborhoodOutput)
  }

  "SetCover " should " be solved correctly for example2.txt " in {
    SetCoverSolverDataModel.clearInstances
    val citiesInstance = new City(prefix + "example2.txt")
    val neighborhoodInstances = citiesInstance.getNeighborhoods.toList

    SetCoverSolverDataModel.cities populate List(citiesInstance)
    SetCoverSolverDataModel.neighborhoods populate neighborhoodInstances
    SetCoverSolverDataModel.cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    val neighborhoodLabels = Map(1 -> true, 2 -> true, 3 -> false, 4 -> false,
      5 -> false, 6 -> false, 7 -> false, 8 -> false)

    citiesInstance.getNeighborhoods.forall { n =>
      ContainsStationConstraint(n) == neighborhoodLabels(n.getNumber).toString
    } should be(true)
  }

  "SetCover " should " be solved correctly for example3.txt " in {
    SetCoverSolverDataModel.clearInstances
    val citiesInstance = new City(prefix + "example3.txt")
    val neighborhoodInstances = citiesInstance.getNeighborhoods.toList

    SetCoverSolverDataModel.cities populate List(citiesInstance)
    SetCoverSolverDataModel.neighborhoods populate neighborhoodInstances
    SetCoverSolverDataModel.cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    val neighborhoodLabels = Map(1 -> true, 2 -> false, 3 -> false)

    citiesInstance.getNeighborhoods.forall { n =>
      ContainsStationConstraint(n) == neighborhoodLabels(n.getNumber).toString
    } should be(true)
  }
}
