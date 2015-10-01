package edu.illinois.cs.cogcomp.lfs.data_model.attribute

import java.util

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscretePrimitiveStringFeature, FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.lfs.data_model.DataModel
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

/** Created by haowu on 1/27/15.
  */
case class CombinedDiscreteAttribute[T <: AnyRef](
  val atts: List[Attribute[T]] // T => (String | Double | List[String] | List[Double])
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, List[_]] {

  override val mapping: (T) => List[_] = {
    t: T =>
      {
        atts.map(att => att.mapping(t))
      }
  }

  val name = "combined++" + atts.map(x => { x.name }).mkString("+")

  override def makeClassifierWithName(n: String): Classifier = {
    new ClassifierContainsInLBP {

      this.containingPackage = "LBP_Package"
      this.name = n

      override def getOutputType: String = {
        return "mixed%"
      }

      def classify(__example: AnyRef): FeatureVector = {

        val t: T = __example.asInstanceOf[T]
        val __result: FeatureVector = new FeatureVector()
        //          println(atts)
        atts.foreach(_.addToFeatureVector(t, __result))

        __result
      }

      override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
        super.classify(examples)
      }

      override def getCompositeChildren: util.LinkedList[_] = {
        val result: util.LinkedList[Classifier] = new util.LinkedList[Classifier]()
        atts.foreach(x => result.add(x.classifier))
        result
      }
    }
  }

  override def addToFeatureVector(t: T, fv: FeatureVector): FeatureVector = {
    atts.foreach(_.addToFeatureVector(t, fv))
    fv
  }

  def addToFeatureVector(t: T, fv: FeatureVector, nameOfClassifier: String): FeatureVector = {
    fv.addFeatures(makeClassifierWithName(nameOfClassifier).classify(t))
    fv
  }

  //  def setDMforAll (dm : DataModel) = {\

}
