package edu.illinois.cs.cogcomp.saulexamples.setcover
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

  val cityContainsNeighborhoods = edge[City, Neighborhood]('cityID)
}

object ContainsStationConstraint extends ConstraintClassifier[Neighborhood, City](Data.trainingData, new ContainsStation()) {

  override def subjectTo = Data.containsStationConstrint

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

object Data {

  val trainingData = new SetCoverSolverDataModel
  val cities = new City("./data/SetCover/example.txt")
  val ns = cities.getNeighborhoods.toList

  val containsStationConstrint = ConstraintClassifier.constraintOf[City]({
    x: City =>
      {
        val containStation = new ContainsStation()
        x.getNeighborhoods _forAll {
          n: Neighborhood =>
            {
              (containStation on n isTrue) ||| (
                n.getNeighbors _exists {
                  n2: Neighborhood => containStation on n2 isTrue
                }
              )
            }
        }
      }
  })

  println(containsStationConstrint.createInferenceCondition[Neighborhood](trainingData).subjectTo.evalDiscreteValue(cities))

  def main(args: Array[String]) {
    trainingData populate List(cities)
    trainingData populate ns

    println(trainingData.getFromRelation[City, Neighborhood](cities))
    println(trainingData.getFromRelation[Neighborhood, City](ns.head))

    cities.getNeighborhoods.foreach {
      n =>
        {
          println(n.getNumber + ": " + ContainsStationConstraint.classifier.discreteValue(n))
        }
    }
    println(ContainsStationConstraint.getCandidates(cities))
  }
}