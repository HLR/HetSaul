package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, FeatureVector, RealPrimitiveStringFeature }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.{ Attribute, TypedAttribute }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

trait RealAttributeCollection[T <: AnyRef] extends Attribute[T]

case class RealGenAttribute[T <: AnyRef](
  name: String,
  mapping: T => List[Double]
)(implicit val tag: ClassTag[T]) extends RealAttributeCollection[T] with TypedAttribute[T, List[Double]] {

  val ra = this.name

  override def makeClassifierWithName(n: String): Classifier = new ClassifierContainsInLBP() {
    this.containingPackage = "LBP_Package"

    this.name = n
    // this.name = name // Parisa: I am not sure why this was the ra.name that made nullpoiterExceptions.

    def classify(example: AnyRef): FeatureVector = {
      val d: T = example.asInstanceOf[T]
      val values = mapping(d)

      var featureVector = new FeatureVector

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
