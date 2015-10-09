package edu.illinois.cs.cogcomp.saulexamples.setCover
import edu.illinois.cs.cogcomp.saul.classifier.ConstraintClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.collection.JavaConversions._
import scala.collection.mutable.{ Map => MutableMap }

/** Created by haowu on 1/29/15.
  */

class SetCoverSolverDataModel extends DataModel {

  val cities = node[City](
    PrimaryKey = {
    t: City => String.valueOf(t.hashCode())
  }
  )

  val neighborhoods = node[Neighborhood](
    PrimaryKey = {
    t: Neighborhood => String.valueOf(t.getNumber)
  },
    SecondaryKeyMap = MutableMap('cityID -> ((t: Neighborhood) => String.valueOf(t.getParentCity.hashCode())))
  )

  val cityContainsNeighborhoods = edge(cities, neighborhoods, 'cityID)
}

object containsStationConstraint extends ConstraintClassifier[Neighborhood, City](setCoverApp.trainingData, new ContainsStation()) {

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
  override def filter(t: Neighborhood, head: City): Boolean = {
    true
  }
}
