/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.setcover

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._

object SetCoverSolverDataModel extends DataModel {

  val cities = node[City]

  val neighborhoods = node[Neighborhood]

  val cityContainsNeighborhoods = edge(cities, neighborhoods)

  cityContainsNeighborhoods.populateWith((c, n) => c == n.getParentCity)

  /** definition of the constraints */
  val containStation = new ContainsStation()

  def atLeastANeighborOfNeighborhoodIsCovered = { n: Neighborhood =>
    n.getNeighbors._exists { neighbor: Neighborhood => containStation on neighbor isTrue }
  }

  def neighborhoodContainsStation = { n: Neighborhood =>
    containStation on n isTrue
  }

  def allCityNeiborhoodsAreCovered = { x: City =>
    x.getNeighborhoods._forall { n: Neighborhood =>
      neighborhoodContainsStation(n) or atLeastANeighborOfNeighborhoodIsCovered(n)
    }
  }

  def someCityNeiborhoodsAreCovered = { x: City =>
    x.getNeighborhoods._atleast(2) { n: Neighborhood =>
      neighborhoodContainsStation(n) //or atLeastANeighborOfNeighborhoodIsCovered(n)
    }
  }

  val containsStationConstraint = ConstrainedClassifier.constraint[City] { x: City => allCityNeiborhoodsAreCovered(x) }
}

import SetCoverSolverDataModel._
object ContainsStationConstraint extends ConstrainedClassifier[Neighborhood, City](new ContainsStation()) {
  override val pathToHead = Some(-cityContainsNeighborhoods)
  override def subjectTo = containsStationConstraint
  override val solver = new OJalgoHook
}
