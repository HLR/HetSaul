package edu.illinois.cs.cogcomp.lfs.data_model.attribute

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.lfs.lbj_related.LBJClassifierEquivalent

import scala.reflect.ClassTag

/** Created by haowu on 1/27/15.
  */
trait Attribute[T] extends LBJClassifierEquivalent {

  val name: String

  val tag: ClassTag[T]
  type S

  val mapping: T => S

  def apply(t: T): S =
    {
      mapping(t)
    }

  val classifier: Classifier = makeClassifierWithName(name)

  def addToFeatureVector(t: T, fv: FeatureVector): FeatureVector

  def addToFeatureVector(t: T, fv: FeatureVector, nameOfClassifier: String): FeatureVector

  def makeClassifierWithName(n: String): Classifier

}

object Attribute {

  /** Transfer a list of attributes to a lbj classifier.
    * @param atts
    * @return
    */
  def entitiesToLBJFeature[T](atts: List[Attribute[T]]): Classifier = {
    null
  }

  def entitiesToLBJFeature[T](atts: Attribute[T]): Classifier = {
    atts.classifier
  }
}