/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ RealPrimitiveStringFeature, Feature, FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

case class RealProperty[T <: AnyRef](name: String, sensor: T => Double)(implicit val tag: ClassTag[T]) extends TypedProperty[T, Double] {

  override def makeClassifierWithName(__name: String): Classifier =
    {
      new ClassifierContainsInLBP() {

        this.containingPackage = "LBP_Package"
        this.name = __name

        def classify(instance: AnyRef): FeatureVector = {
          new FeatureVector(featureValue(instance))
        }

        override def featureValue(instance: AnyRef): Feature = {
          val result: Double = realValue(instance)
          new RealPrimitiveStringFeature(containingPackage, name, "", result)
        }

        override def realValue(instance: AnyRef): Double = {
          val d: T = instance.asInstanceOf[T]
          sensor(d)
        }

        override def classify(instance: Array[AnyRef]): Array[FeatureVector] = {
          super.classify(instance)
        }
      }
    }

  override def addToFeatureVector(instance: T, featureVector: FeatureVector): FeatureVector = {
    featureVector.addFeature(this.classifier.featureValue(instance))
    featureVector
  }
  def addToFeatureVector(instance: T, featureVector: FeatureVector, nameOfClassifier: String): FeatureVector = {
    featureVector.addFeature(makeClassifierWithName(nameOfClassifier).featureValue(instance))
    featureVector
  }
}
