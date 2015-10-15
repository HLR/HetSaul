package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscretePrimitiveStringFeature, Classifier, DiscreteArrayStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.TypedAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.ClassifierContainsInLBP

//import tutorial_related.Document

import scala.reflect.ClassTag

case class DiscreteCollectionAttribute[T <: AnyRef](
  name: String,
  mapping: T => List[String],
  ordered: Boolean
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, List[String]] {

  override def addToFeatureVector(t: T, fv: FeatureVector): FeatureVector = {
    fv.addFeatures(this.classifier.classify(t))
    fv
  }

  override def makeClassifierWithName(n: String): Classifier = {
    new ClassifierContainsInLBP() {

      this.name = n

      def classify(__example: AnyRef): FeatureVector = {
        val d: T = __example.asInstanceOf[T]
        val values = mapping(d)

        var result = new FeatureVector

        if (ordered) {
          values.zipWithIndex.foreach {
            case (__value, __featureIndex) => result.addFeature(new DiscreteArrayStringFeature(this.containingPackage, this.name, "", __value, valueIndexOf(__value), 0.toShort, __featureIndex, 0))
          }
          // Daniel commented this line
          //(0 to values.size) foreach { x => result.getFeature(x).setArrayLength(values.size) }
        } else {
          values foreach (
            x => {
              val __id = x
              result.addFeature(new DiscretePrimitiveStringFeature(this.containingPackage, this.name, __id, x, valueIndexOf(x), 0.toShort))
            }
          )
        }
        result
      }

      override def discreteValueArray(__example: AnyRef): Array[String] = {
        classify(__example).discreteValueArray
      }
    }
  }

  def addToFeatureVector(t: T, fv: FeatureVector, nameOfClassifier: String): FeatureVector = {
    fv.addFeatures(makeClassifierWithName(nameOfClassifier).classify(t))
    fv
  }
}
