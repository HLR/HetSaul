package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ RealArrayStringFeature, FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.TypedAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

/** Created by haowu on 2/5/15.
  */
case class RealArrayAttribute[T <: AnyRef](
  val name: String,
  val mapping: T => List[Double]
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, List[Double]] {

  val ra = this

  override def makeClassifierWithName(n: String): Classifier = new ClassifierContainsInLBP() {

    this.containingPackage = "LBP_Package"
    this.name = ra.name

    override def realValueArray(__example: AnyRef): Array[Double] = {
      classify(__example).realValueArray
    }

    override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
      super.classify(examples)
    }

    def classify(__example: AnyRef): FeatureVector = {

      val d: T = __example.asInstanceOf[T]
      val values = mapping(d)

      var __result: FeatureVector = null
      __result = new FeatureVector

      values.zipWithIndex.map {
        case (__value, __featureIndex) => __result.addFeature(new RealArrayStringFeature(this.containingPackage, this.name, "", __value, __featureIndex, 0))
      }

      (0 to values.size) foreach { x => __result.getFeature(x).setArrayLength(values.size) }

      __result
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
