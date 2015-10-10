package edu.illinois.cs.cogcomp.saulexamples.setCover

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.collection.mutable.{ Map => MutableMap }

class SetCoverSolverDataModel extends DataModel {

  val cities = node[City]

  val neighborhoods = node[Neighborhood]

  val cityContainsNeighborhoods = edge(cities, neighborhoods, 'cityID)

  cityContainsNeighborhoods.populateWith((c, n) => c == n.getParentCity)
}

object containsStationConstraint extends ConstrainedClassifier[Neighborhood, City](setCoverApp.trainingData, new ContainsStation()) {

  override def subjectTo = setCoverApp.containsStationConstrint

  //    ConstraintClassifier.constraintOf[City]({
  //    x: City => {
  //      val containStation = new ContainsStation()
  //      x.getNeighborhoods _forAll {
  //        n: Neighborhood => {
  //          (containStation on n isTrue) ||| (
  //            n.getNeighbors _exists {
  //              n2: Neighborhood => containStation on n2 isTrue
  //            })
  //        }
  //      }
  //    }
  //  })
}
