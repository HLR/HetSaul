/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, DiscreteArrayStringFeature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP

//import tutorial_related.Document

import scala.reflect.ClassTag

case class DiscreteArrayProperty[T <: AnyRef](name: String, sensor: T => List[String])(implicit val tag: ClassTag[T]) extends TypedProperty[T, List[String]] {

  override def makeClassifierWithName(n: String): Classifier = new ClassifierContainsInLBP() {
    this.containingPackage = "LBP_Package"
    this.name = n

    def classify(__example: AnyRef): FeatureVector = {
      val d: T = __example.asInstanceOf[T]
      val values = sensor(d)

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
