package edu.illinois.cs.cogcomp.saul.datamodel.property

import java.util

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscretePrimitiveStringFeature, FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

case class CombinedDiscreteProperty[T <: AnyRef](
  atts: List[Property[T]]
)(implicit val tag: ClassTag[T]) extends TypedProperty[T, List[_]] {

  override val sensor: (T) => List[_] = {
    t: T => atts.map(att => att.sensor(t))
  }

  val name = "combined++" + atts.map(x => { x.name }).mkString("+")

  override def makeClassifierWithName(n: String): Classifier = {
    new ClassifierContainsInLBP {

      this.containingPackage = "LBP_Package"
      this.name = n

      override def getOutputType: String = {
        "mixed%"
      }

      def classify(instance: AnyRef): FeatureVector = {
        val t: T = instance.asInstanceOf[T]
        val featureVector = new FeatureVector()
        atts.foreach(_.addToFeatureVector(t, featureVector))
        featureVector
      }

      override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
        super.classify(examples)
      }

      override def getCompositeChildren: util.LinkedList[_] = {
        val result: util.LinkedList[Classifier] = new util.LinkedList[Classifier]()
        atts.foreach(x => result.add(x.classifier))
        result
      }

      override def discreteValue(example: AnyRef): String = {
        atts.head(example.asInstanceOf[T]).asInstanceOf[String]
      }
    }
  }

  override def addToFeatureVector(instance: T, featureVector: FeatureVector): FeatureVector = {
    atts.foreach(_.addToFeatureVector(instance, featureVector))
    featureVector
  }

  def addToFeatureVector(instance: T, featureVector: FeatureVector, nameOfClassifier: String): FeatureVector = {
    featureVector.addFeatures(makeClassifierWithName(nameOfClassifier).classify(instance))
    featureVector
  }
}
