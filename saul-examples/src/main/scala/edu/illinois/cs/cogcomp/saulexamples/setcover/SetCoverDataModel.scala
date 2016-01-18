package edu.illinois.cs.cogcomp.saulexamples.setcover

import java.io.PrintStream

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscretePrimitiveStringFeature, ScoreSet, FeatureVector }
import edu.illinois.cs.cogcomp.lbjava.learn.Learner
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

  val containsStationConstrint = ConstrainedClassifier.constraint[City] { x: City => allCityNeiborhoodsAreCovered(x) }
}

object containsStationConstraint extends ConstrainedClassifier[Neighborhood, City](SetCoverSolverDataModel, new ContainsStation()) {
  override def subjectTo = SetCoverSolverDataModel.containsStationConstrint
}
