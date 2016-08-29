/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscretePrimitiveStringFeature, Feature, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP

class NamedDiscreteProperty[T <: AnyRef](attName: String, function: T => String) {
  def apply() = {}

  def applyRange(range: List[String]) = {
    {
      new ClassifierContainsInLBP() {
        private var __allowableValues: Array[String] = range.toArray.asInstanceOf[Array[String]]

        this.containingPackage = "LBP_Package"
        this.name = attName

        def getAllowableValues: Array[String] = {
          __allowableValues
        }

        override def allowableValues: Array[String] = {
          __allowableValues
        }

        def classify(__example: AnyRef): FeatureVector = {
          new FeatureVector(featureValue(__example))
        }

        override def featureValue(__example: AnyRef): Feature = {
          val result: String = discreteValue(__example)
          new DiscretePrimitiveStringFeature(containingPackage, this.name, "", result, valueIndexOf(result), allowableValues.length.asInstanceOf[Short])
        }

        override def discreteValue(__example: AnyRef): String = {
          // TODO: catching errors (type checking)
          _discreteValue(__example)
        }

        private def _discreteValue(__example: AnyRef): String = {
          val t: T = __example.asInstanceOf[T]
          function(t)
        }
      }
    }
  }

  val classifier = new ClassifierContainsInLBP {

    this.containingPackage = "LBP_Package"
    this.name = attName

    def classify(instance: AnyRef): FeatureVector = {
      new FeatureVector(featureValue(instance))
    }

    override def classify(instances: Array[AnyRef]): Array[FeatureVector] = {
      super.classify(instances)
    }

    override def featureValue(__example: AnyRef): Feature = {
      val result: String = discreteValue(__example)
      new DiscretePrimitiveStringFeature(containingPackage, this.name, "", result, valueIndexOf(result), allowableValues.length.toShort)
    }

    override def discreteValue(instance: AnyRef): String = {
      val d: T = instance.asInstanceOf[T]
      function(d).toString
    }
  }

}
