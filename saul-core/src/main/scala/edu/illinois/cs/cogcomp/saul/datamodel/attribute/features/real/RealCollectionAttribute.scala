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

  override def makeClassifierWithName(name: String): Classifier = new ClassifierContainsInLBP() {
    this.containingPackage = "LBP_Package"

    this.name = name

    override def realValueArray(instance: AnyRef): Array[Double] = {
      classify(instance).realValueArray
    }

    override def classify(instance: Array[AnyRef]): Array[FeatureVector] = {
      super.classify(instance)
    }

    def classify(instance: AnyRef): FeatureVector = {

      val d: T = instance.asInstanceOf[T]
      val values = sensor(d)

      var featureVector = new FeatureVector

      if (ordered) {
        values.zipWithIndex.foreach {
          case (value, idx) =>
            featureVector.addFeature(new RealArrayStringFeature(
              this.containingPackage,
              this.name, "", value, idx, 0
            ))
        }
        // TODO: commented by Daniel. Make sure this does not introduce any bugs
        //(0 to values.size) foreach { x => featureVector.getFeature(x).setArrayLength(values.size) }
      } else {
        values.zipWithIndex.foreach {
          case (value, idx) =>
            featureVector.addFeature(new RealPrimitiveStringFeature(
              this.containingPackage,
              this.name, idx + "", value
            ))
        }
      }

      featureVector
    }
  }

  override def addToFeatureVector(instance: T, featureVector: FeatureVector): FeatureVector = {
    featureVector.addFeatures(this.classifier.classify(instance))
    featureVector
  }

  def addToFeatureVector(instance: T, featureVector: FeatureVector, nameOfClassifier: String): FeatureVector = {
    featureVector.addFeatures(makeClassifierWithName(nameOfClassifier).classify(instance))
    featureVector
  }
}
