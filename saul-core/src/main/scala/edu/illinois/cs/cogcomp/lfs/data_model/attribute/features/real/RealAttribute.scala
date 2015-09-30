package edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ RealPrimitiveStringFeature, Feature, FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.TypedAttribute
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

/** Created by haowu on 2/5/15.
  */
case class RealAttribute[T <: AnyRef](
  val name: String,
  val mapping: T => Double
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, Double] {
  override def makeClassifierWithName(n: String): Classifier =

    {

      println(Console.BLUE + n)
      new ClassifierContainsInLBP() {

        this.containingPackage = "LBP_Package"
        this.name = n

        def classify(__example: AnyRef): FeatureVector = {
          return new FeatureVector(featureValue(__example))
        }

        override def featureValue(__example: AnyRef): Feature = {
          val result: Double = realValue(__example)
          return new RealPrimitiveStringFeature(containingPackage, name, "", result)
        }

        override def realValue(__example: AnyRef): Double = {
          val d: T = __example.asInstanceOf[T]
          return mapping(d)
        }

        override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
          super.classify(examples)
        }

      }

    }

  override def addToFeatureVector(t: T, fv: FeatureVector): FeatureVector = {
    fv.addFeature(this.classifier.featureValue(t))
    fv
  }
  def addToFeatureVector(t: T, fv: FeatureVector, nameOfClassifier: String): FeatureVector = {
    fv.addFeature(makeClassifierWithName(nameOfClassifier).featureValue(t))
    fv
  }

}
