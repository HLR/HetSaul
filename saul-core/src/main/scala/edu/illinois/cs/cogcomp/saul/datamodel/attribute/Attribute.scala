package edu.illinois.cs.cogcomp.saul.datamodel.attribute

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, Classifier => FeatureGenerator }
import edu.illinois.cs.cogcomp.saul.lbjrelated.LBJClassifierEquivalent

import scala.reflect.ClassTag

trait Attribute[T] extends LBJClassifierEquivalent {

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

object Attribute {

  /** Transfer a list of attributes to a lbj classifier. */
  def entitiesToLBJFeature[T](atts: Attribute[T]): FeatureGenerator = {
    atts.classifier
  }
}