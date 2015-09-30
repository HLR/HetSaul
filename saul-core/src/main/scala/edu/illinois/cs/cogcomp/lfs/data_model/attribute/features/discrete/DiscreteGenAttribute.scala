package edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.discrete

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, DiscretePrimitiveStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.TypedAttribute
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.ClassifierContainsInLBP
//import tutorial_related.Document

import scala.reflect.ClassTag

/** Created by haowu on 2/5/15.
  */
class DiscreteGenAttribute[T <: AnyRef](
  val name: String,
  val mapping: T => List[String]
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, List[String]] {

  override def makeClassifierWithName(n: String): Classifier = new ClassifierContainsInLBP() {

    this.containingPackage = "LBP_Package"
    this.name = n

    def classify(__example: AnyRef): FeatureVector = {

      val t: T = __example.asInstanceOf[T]
      var __result: FeatureVector = null
      __result = new FeatureVector
      var __id: String = null
      var __value: String = null

      val __ids = mapping(t)

      __ids foreach (
        x => {
          val __id = x
          val __value = "true"
          __result.addFeature(new DiscretePrimitiveStringFeature(this.containingPackage, this.name, __id, __value, valueIndexOf(__value), 0.toShort))
        }
      )

      return __result
    }

    override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
      return super.classify(examples)
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
