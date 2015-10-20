package edu.illinois.cs.cogcomp.saul.datamodel.property

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, Classifier => FeatureGenerator }
import edu.illinois.cs.cogcomp.saul.lbjrelated.LBJClassifierEquivalent

import scala.reflect.ClassTag

trait Property[T] extends LBJClassifierEquivalent {

  val name: String

  val tag: ClassTag[T]
  type S

  val sensor: T => S

  def apply(instance: T): S = {
    sensor(instance)
  }

  val classifier = makeClassifierWithName(name)

  def addToFeatureVector(instance: T, featureVector: FeatureVector): FeatureVector

  def addToFeatureVector(instance: T, featureVector: FeatureVector, nameOfClassifier: String): FeatureVector

  def makeClassifierWithName(n: String): FeatureGenerator

}

object Property {

  /** Transfer a list of properties to a lbj classifier. */
  def entitiesToLBJFeature[T](atts: Property[T]): FeatureGenerator = {
    atts.classifier
  }
}