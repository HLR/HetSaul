package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ RealPrimitiveStringFeature, Feature, FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.TypedAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

case class RealAttribute[T <: AnyRef](
  name: String,
  sensor: T => Double
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, Double] {
  override def makeClassifierWithName(name: String): Classifier =

    {
      new ClassifierContainsInLBP() {

        this.containingPackage = "LBP_Package"
        this.name = name

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
