/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscretePrimitiveStringFeature, Feature, FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

case class DiscreteProperty[T <: AnyRef](name: String, sensor: T => String, range: Option[List[String]])(implicit val tag: ClassTag[T])
  extends TypedProperty[T, String] {

  override def makeClassifierWithName(__name: String): Classifier = range match {
    case Some(r) =>
      new ClassifierContainsInLBP() {
        private val __allowableValues: Array[String] = r.toArray

        this.containingPackage = "LBP_Package"
        this.name = __name

        def getAllowableValues: Array[String] = {
          __allowableValues
        }

        override def allowableValues: Array[String] = {
          __allowableValues
        }

        def classify(instance: AnyRef): FeatureVector = {
          new FeatureVector(featureValue(instance))
        }

        override def featureValue(instance: AnyRef): Feature = {
          val result: String = discreteValue(instance)
          new DiscretePrimitiveStringFeature(containingPackage, this.name, "", result,
            valueIndexOf(result), allowableValues.length.asInstanceOf[Short])
        }

        override def discreteValue(instance: AnyRef): String = {
          // TODO: catching errors (type checking)
          _discreteValue(instance)
        }

        private def _discreteValue(__example: AnyRef): String = {
          val t: T = __example.asInstanceOf[T]
          self.sensor(t).mkString("")
        }
      }
    case _ => new ClassifierContainsInLBP {

      this.containingPackage = "LBP_Package"
      this.name = __name

      def classify(instance: AnyRef): FeatureVector = {
        new FeatureVector(featureValue(instance))
      }

      override def featureValue(instance: AnyRef): Feature = {
        val result: String = discreteValue(instance)
        new DiscretePrimitiveStringFeature(containingPackage, this.name, "", result,
          valueIndexOf(result), allowableValues.length.toShort)
      }

      override def discreteValue(__example: AnyRef): String = {
        val d: T = __example.asInstanceOf[T]
        sensor(d)
      }

      override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
        super.classify(examples)
      }
    }
  }

  override def addToFeatureVector(t: T, featureVector: FeatureVector): FeatureVector = {
    featureVector.addFeature(this.classifier.featureValue(t))
    featureVector
  }

  def addToFeatureVector(t: T, featureVector: FeatureVector, nameOfClassifier: String): FeatureVector = {
    featureVector.addFeature(makeClassifierWithName(nameOfClassifier).featureValue(t))
    featureVector
  }
}
