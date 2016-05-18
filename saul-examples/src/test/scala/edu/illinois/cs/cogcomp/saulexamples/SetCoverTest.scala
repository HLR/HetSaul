package edu.illinois.cs.cogcomp.saulexamples

import edu.illinois.cs.cogcomp.saulexamples.setcover.{ City, ContainsStationConstraint, SetCoverSolverDataModel }
import org.scalatest.{ FlatSpec, Matchers }

import scala.collection.JavaConversions._

class SetCoverTest extends FlatSpec with Matchers {
  import SetCoverSolverDataModel._

  val prefix = "../saul-examples/src/main/resources/SetCover/"

  "SetCover " should " be solved correctly for example.txt " in {
    clearInstances
    val citiesInstance = new City(prefix + "example.txt")
    val neighborhoodInstances = citiesInstance.getNeighborhoods.toList

    cities populate List(citiesInstance)
    neighborhoods populate neighborhoodInstances
    cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    val neighborhoodLabels = Map(1 -> true, 2 -> false, 3 -> false, 4 -> false, 5 -> false, 6 -> true,
      7 -> false, 8 -> false, 9 -> false)

    citiesInstance.getNeighborhoods.forall { n =>
      ContainsStationConstraint(n) == neighborhoodLabels(n.getNumber).toString
    } should be(true)
    val neighborhoodOutput = "List(neighborhood #1, neighborhood #2, neighborhood #3, neighborhood #4, neighborhood #5, neighborhood #6, neighborhood #7, neighborhood #8, neighborhood #9)"
    ContainsStationConstraint.getCandidates(citiesInstance).toList.toString should be(neighborhoodOutput)
    cityContainsNeighborhoods(citiesInstance).toList.sorted.toString should be(neighborhoodOutput)
  }

  "SetCover " should " be solved correctly for example2.txt " in {
    clearInstances
    val citiesInstance = new City(prefix + "example2.txt")
    val neighborhoodInstances = citiesInstance.getNeighborhoods.toList

    cities populate List(citiesInstance)
    neighborhoods populate neighborhoodInstances
    cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    val neighborhoodLabels = Map(1 -> true, 2 -> true, 3 -> false, 4 -> false,
      5 -> false, 6 -> false, 7 -> false, 8 -> false)

    citiesInstance.getNeighborhoods.forall { n =>
      ContainsStationConstraint(n) == neighborhoodLabels(n.getNumber).toString
    } should be(true)
  }

  "SetCover " should " be solved correctly for example3.txt " in {
    clearInstances
    val citiesInstance = new City(prefix + "example3.txt")
    val neighborhoodInstances = citiesInstance.getNeighborhoods.toList

    cities populate List(citiesInstance)
    neighborhoods populate neighborhoodInstances
    cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    val neighborhoodLabels = Map(1 -> true, 2 -> false, 3 -> false)

    citiesInstance.getNeighborhoods.forall { n =>
      ContainsStationConstraint(n) == neighborhoodLabels(n.getNumber).toString
    } should be(true)
  }
}
