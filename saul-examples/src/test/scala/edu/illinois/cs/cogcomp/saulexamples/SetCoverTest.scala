package edu.illinois.cs.cogcomp.saulexamples

import edu.illinois.cs.cogcomp.saulexamples.setcover.{ containsStationConstraint, Neighborhood, City, SetCoverSolverDataModel }
import org.scalatest.{ Matchers, FlatSpec }
import scala.collection.JavaConversions._

class SetCoverTest extends FlatSpec with Matchers {

  "SetCover " should " be solved correctly " in {
    val citiesInstance = new City("./data/SetCover/example.txt")
    val neighborhoodInstances = citiesInstance.getNeighborhoods.toList

    SetCoverSolverDataModel.cities populate List(citiesInstance)
    SetCoverSolverDataModel.neighborhoods populate neighborhoodInstances
    SetCoverSolverDataModel.cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

    val neighborhoodLabels = Map(1 -> false, 2 -> true, 3 -> false, 4 -> false, 5 -> false, 6 -> false,
      7 -> true, 8 -> false, 9 -> false, 10 -> false, 11 -> true)

    //      citiesInstance.getNeighborhoods.foreach { n =>
    //        println(containsStationConstraint.classifier.discreteValue(n).toString + "  " +  neighborhoodLabels(n.getNumber) )
    //      }

    citiesInstance.getNeighborhoods.forall { n =>
      containsStationConstraint.classifier.discreteValue(n) == neighborhoodLabels(n.getNumber).toString
    } should be(true)

    val neighborhoodOutput = "List(neighborhood #1, neighborhood #2, neighborhood #3, neighborhood #4, neighborhood #5, neighborhood #6, neighborhood #7, neighborhood #8, neighborhood #9, neighborhood #10, neighborhood #11)"
    containsStationConstraint.getCandidates(citiesInstance).toString should be(neighborhoodOutput)
    SetCoverSolverDataModel.getFromRelation[City, Neighborhood](citiesInstance).toList.toString should be(neighborhoodOutput)
  }
}
