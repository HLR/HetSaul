/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, FeatureVector, RealPrimitiveStringFeature }
import edu.illinois.cs.cogcomp.saul.datamodel.property.{ Property, TypedProperty }
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

trait RealPropertyCollection[T <: AnyRef] extends Property[T]

case class RealGenProperty[T <: AnyRef](name: String, sensor: T => List[Double])(implicit val tag: ClassTag[T]) extends RealPropertyCollection[T] with TypedProperty[T, List[Double]] {

  val ra = this.name

  override def makeClassifierWithName(n: String): Classifier = new ClassifierContainsInLBP() {
    this.containingPackage = "LBP_Package"

    this.name = n
    // this.name = name // Parisa: I am not sure why this was the ra.name that made nullpoiterExceptions.

    def classify(example: AnyRef): FeatureVector = {
      val d: T = example.asInstanceOf[T]
      val values = sensor(d)

      val featureVector = new FeatureVector

      values.zipWithIndex.foreach {
        case (value, idx) => featureVector.addFeature(new RealPrimitiveStringFeature(this.containingPackage, this.name, idx + "", value))
      }

      featureVector
    }
  }

  override def addToFeatureVector(t: T, fv: FeatureVector): FeatureVector = {
    fv.addFeatures(this.classifier.classify(t))
    fv
  }

  def addToFeatureVector(t: T, fv: FeatureVector, nameOfClassifier: String): FeatureVector = {
    fv.addFeatures(makeClassifierWithName(nameOfClassifier).classify(t))
    fv
  }
}
