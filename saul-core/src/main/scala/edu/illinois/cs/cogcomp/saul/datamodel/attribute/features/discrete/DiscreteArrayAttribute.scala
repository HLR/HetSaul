package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, DiscreteArrayStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.TypedAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.ClassifierContainsInLBP

//import tutorial_related.Document

import scala.reflect.ClassTag

/** Created by haowu on 2/5/15.
  */
case class DiscreteArrayAttribute[T <: AnyRef](
  name: String,
  mapping: T => List[String],
  range: Option[List[String]]
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, List[String]] with DiscreteAttributeCollection[T] {

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

        var __result: FeatureVector = null
        __result = new FeatureVector

        values.zipWithIndex.foreach {
          case (__value, __featureIndex) => __result.addFeature(new DiscreteArrayStringFeature(this.containingPackage, this.name, "", __value, valueIndexOf(__value), 0.toShort, __featureIndex, 0))
        }

        (0 to values.size) foreach { x => __result.getFeature(x).setArrayLength(values.size) }
        __result

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
