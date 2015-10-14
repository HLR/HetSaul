package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ RealArrayStringFeature, FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.TypedAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

case class RealArrayAttribute[T <: AnyRef](
  name: String,
  mapping: T => List[Double]
)(implicit val tag: ClassTag[T]) extends RealAttributeCollection[T] with TypedAttribute[T, List[Double]] {

  val ra = this // shouldn't this be this.name?

  override def makeClassifierWithName(n: String): Classifier = new ClassifierContainsInLBP() {
    this.containingPackage = "LBP_Package"

    this.name = n

    override def realValueArray(example: AnyRef): Array[Double] = {
      classify(example).realValueArray
    }

    override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
      super.classify(examples)
    }

    def classify(example: AnyRef): FeatureVector = {

      val d: T = example.asInstanceOf[T]
      val values = mapping(d)

      var featureVector = new FeatureVector

      values.zipWithIndex.foreach {
        case (value, idx) => featureVector.addFeature(new RealArrayStringFeature(this.containingPackage, this.name, "", value, idx, 0))
      }

      (0 to values.size) foreach { x => featureVector.getFeature(x).setArrayLength(values.size) }

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
