package edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, DiscretePrimitiveStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.TypedAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.ClassifierContainsInLBP
//import tutorial_related.Document

import scala.reflect.ClassTag

class DiscreteGenAttribute[T <: AnyRef](
  val name: String,
  val sensor: T => List[String]
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, List[String]] {

  override def makeClassifierWithName(n: String): Classifier = new ClassifierContainsInLBP() {

    this.containingPackage = "LBP_Package"
    this.name = n

    def classify(__example: AnyRef): FeatureVector = {
      val d: T = __example.asInstanceOf[T]
      val values = sensor(d)

      var __result: FeatureVector = null
      __result = new FeatureVector

      values foreach (
        x => {
          val __id = x
          __result.addFeature(new DiscretePrimitiveStringFeature(this.containingPackage, this.name, __id, x, valueIndexOf(x), 0.toShort))
        }
      )

      __result
    }

    override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
      super.classify(examples)
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
