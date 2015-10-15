package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ RealPrimitiveStringFeature, RealArrayStringFeature, FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.TypedAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

case class RealCollectionAttribute[T <: AnyRef](
  name: String,
  sensor: T => List[Double],
  ordered: Boolean
)(implicit val tag: ClassTag[T]) extends RealAttributeCollection[T] with TypedAttribute[T, List[Double]] {

  val ra = this.name

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
      val values = sensor(d)

      var featureVector = new FeatureVector

      if (ordered) {
        values.zipWithIndex.foreach {
          case (value, idx) => featureVector.addFeature(new RealArrayStringFeature(this.containingPackage, this.name, "", value, idx, 0))
        }
        // commented by Daniel
        //(0 to values.size) foreach { x => featureVector.getFeature(x).setArrayLength(values.size) }
      } else {
        values.zipWithIndex.foreach {
          case (value, idx) => featureVector.addFeature(new RealPrimitiveStringFeature(this.containingPackage, this.name, idx + "", value))
        }
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
