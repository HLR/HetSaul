package edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscretePrimitiveStringFeature, Classifier, DiscreteArrayStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

case class DiscreteCollectionProperty[T <: AnyRef](
  name: String,
  sensor: T => List[String],
  ordered: Boolean
)(implicit val tag: ClassTag[T]) extends TypedProperty[T, List[String]] {

  override def makeClassifierWithName(__name: String): Classifier = {
    new ClassifierContainsInLBP() {

      this.name = __name

      def classify(instance: AnyRef): FeatureVector = {
        val d: T = instance.asInstanceOf[T]
        val values = sensor(d)

        var featureVector = new FeatureVector

        if (ordered) {
          values.zipWithIndex.foreach {
            case (value, idx) =>
              featureVector.addFeature(
                new DiscreteArrayStringFeature(this.containingPackage, this.name, "", value,
                  valueIndexOf(value), 0.toShort, idx, 0)
              )
          }
          // TODO: Daniel commented this line. Make sure this does not introduce a bug 
          //(0 to values.size) foreach { x => featureVector.getFeature(x).setArrayLength(values.size) }
        } else {
          values.foreach {
            value =>
              val id = value
              featureVector.addFeature(new DiscretePrimitiveStringFeature(
                this.containingPackage,
                this.name, id, value, valueIndexOf(value), 0.toShort
              ))
          }
        }
        featureVector
      }

      override def discreteValueArray(instance: AnyRef): Array[String] = {
        classify(instance).discreteValueArray
      }
    }
  }

  override def addToFeatureVector(t: T, featureVector: FeatureVector): FeatureVector = {
    featureVector.addFeatures(this.classifier.classify(t))
    featureVector
  }

  def addToFeatureVector(t: T, featureVector: FeatureVector, classifierName: String): FeatureVector = {
    featureVector.addFeatures(makeClassifierWithName(classifierName).classify(t))
    featureVector
  }
}
