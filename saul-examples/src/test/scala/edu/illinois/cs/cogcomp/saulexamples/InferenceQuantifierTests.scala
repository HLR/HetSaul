/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
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

  import SomeDM._
  object AtLeastSomeNeighborhoods extends ConstrainedClassifier[Neighborhood, City](new ContainsStation()) {
    override val pathToHead = Some(-cityContainsNeighborhoods)
    override def subjectTo = atLeastSomeNeighborsAreCoveredConstraint
    override val solver = new OJalgoHook
  }

  object AtLeastSomeNeighborhoodsUsingAtMost extends ConstrainedClassifier[Neighborhood, City](new ContainsStation()) {
    override val pathToHead = Some(-cityContainsNeighborhoods)
    override def subjectTo = atLeastSomeNeighborsAreCoveredConstraintUsingAtMost
    override val solver = new OJalgoHook
  }

  object AllNeighborhoods extends ConstrainedClassifier[Neighborhood, City](new ContainsStation()) {
    override val pathToHead = Some(-cityContainsNeighborhoods)
    override def subjectTo = allNeighborsAreCoveredConstraint
    override val solver = new OJalgoHook
  }

  object ASingleNeighborhood extends ConstrainedClassifier[Neighborhood, City](new ContainsStation()) {
    override val pathToHead = Some(-cityContainsNeighborhoods)
    override def subjectTo = singleNeighborsAreCoveredConstraint
    override val solver = new OJalgoHook
  }

  val cityInstances = new City("../saul-examples/src/test/resources/SetCover/example.txt")
  val neighborhoodInstances = cityInstances.getNeighborhoods.toList

  SomeDM.cities populate List(cityInstances)
  SomeDM.neighborhoods populate neighborhoodInstances
  SomeDM.cityContainsNeighborhoods.populateWith(_ == _.getParentCity)

  "Quantifier atleast " should " work " in {
    cityInstances.getNeighborhoods.count(n => AtLeastSomeNeighborhoods(n) == "true") should be(2)
  }

  // negation of atmost(2) is equivalent to atleast(2)
  "Quantifier atmost " should " work " in {
    cityInstances.getNeighborhoods.count(n => AtLeastSomeNeighborhoodsUsingAtMost(n) == "true") should be(3)
  }

  "Quantifier forall " should " work " in {
    cityInstances.getNeighborhoods.count(n => AllNeighborhoods(n) == "true") should be(9)
  }

  "Quantifier exists " should " work " in {
    cityInstances.getNeighborhoods.count(n => ASingleNeighborhood(n) == "true") should be(1)
  }
}
