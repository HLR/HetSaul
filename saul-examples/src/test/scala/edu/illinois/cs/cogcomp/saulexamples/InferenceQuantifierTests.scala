package edu.illinois.cs.cogcomp.saulexamples

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.setcover.{ City, ContainsStation, Neighborhood }
import org.scalatest.{ Matchers, FlatSpec }

import scala.collection.JavaConversions._

class InferenceQuantifierTests extends FlatSpec with Matchers {

  object SomeDM extends DataModel {

    val cities = node[City]

    val neighborhoods = node[Neighborhood]

    val cityContainsNeighborhoods = edge(cities, neighborhoods)

    cityContainsNeighborhoods.populateWith((c, n) => c == n.getParentCity)

    /** definition of the constraints */
    val containStation = new ContainsStation()

    def neighborhoodContainsStation = { n: Neighborhood =>
      containStation on n isTrue
    }

    val atLeastSomeNeighborsAreCoveredConstraint = ConstrainedClassifier.constraint[City] { x: City =>
      x.getNeighborhoods._atleast(2) { n: Neighborhood => neighborhoodContainsStation(n) }
    }

    val atLeastSomeNeighborsAreCoveredConstraintUsingAtMost = ConstrainedClassifier.constraint[City] { x: City =>
      !x.getNeighborhoods._atmost(2) { n: Neighborhood => neighborhoodContainsStation(n) }
    }

    val allNeighborsAreCoveredConstraint = ConstrainedClassifier.constraint[City] { x: City =>
      x.getNeighborhoods._forall { n: Neighborhood => neighborhoodContainsStation(n) }
    }

    val singleNeighborsAreCoveredConstraint = ConstrainedClassifier.constraint[City] { x: City =>
      x.getNeighborhoods._exists { n: Neighborhood => neighborhoodContainsStation(n) }
    }
  }

  object atLeastSomeNeighborhoods extends ConstrainedClassifier[Neighborhood, City](SomeDM, new ContainsStation()) {
    override def subjectTo = SomeDM.atLeastSomeNeighborsAreCoveredConstraint
  }

  object atLeastSomeNeighborhoodsUsingAtMost extends ConstrainedClassifier[Neighborhood, City](SomeDM, new ContainsStation()) {
    override def subjectTo = SomeDM.atLeastSomeNeighborsAreCoveredConstraintUsingAtMost
  }

  object allNeighborhoods extends ConstrainedClassifier[Neighborhood, City](SomeDM, new ContainsStation()) {
    override def subjectTo = SomeDM.allNeighborsAreCoveredConstraint
  }

  object aSingleNeighborhood extends ConstrainedClassifier[Neighborhood, City](SomeDM, new ContainsStation()) {
    override def subjectTo = SomeDM.singleNeighborsAreCoveredConstraint
  }

  val cityInstances = new City("../saul-examples/src/main/resources/SetCover/example.txt")
  val neighborhoodInstances = cityInstances.getNeighborhoods.toList

  SomeDM.cities populate List(cityInstances)
  SomeDM.neighborhoods populate neighborhoodInstances
  SomeDM.cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

  "Quantifier atleast " should " work " in {
    cityInstances.getNeighborhoods.count(n => atLeastSomeNeighborhoods(n) == "true") should be(2)
  }

  // negation of atmost(2) is equivalent to atleast(2)
  "Quantifier atmost " should " work " in {
    cityInstances.getNeighborhoods.count(n => atLeastSomeNeighborhoodsUsingAtMost(n) == "true") should be(3)
  }

  "Quantifier forall " should " work " in {
    cityInstances.getNeighborhoods.count(n => allNeighborhoods(n) == "true") should be(9)
  }

  "Quantifier exists " should " work " in {
    cityInstances.getNeighborhoods.count(n => aSingleNeighborhood(n) == "true") should be(1)
  }
}
