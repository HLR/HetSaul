package edu.illinois.cs.cogcomp.lfs.data_model.attribute

import edu.illinois.cs.cogcomp.lbjava.classify.{DiscretePrimitiveStringFeature, Feature, FeatureVector}
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.ClassifierContainsInLBP

/**
 * Created by haowu on 3/4/15.
 */
class NamedDiscreteAttribute[T <: AnyRef](
                            val attName : String,
                            val function : T => String
                            ) {
		def apply() = {}

	  def applyRange( range : List[String] ) = {
		  {
			  new ClassifierContainsInLBP() {
				  private var __allowableValues: Array[String] =  range.toArray.asInstanceOf[Array[String]]

				  this.containingPackage = "LBP_Package"
				  this.name = attName

				  def getAllowableValues: Array[String] = {
					  return __allowableValues
				  }

				  override def allowableValues: Array[String] = {
					  return __allowableValues
				  }

				  def classify(__example: AnyRef): FeatureVector = {
					  return new FeatureVector(featureValue(__example))
				  }

				  override def featureValue(__example: AnyRef): Feature = {
					  val result: String = discreteValue(__example)
					  return new DiscretePrimitiveStringFeature(containingPackage, this.name, "", result, valueIndexOf(result), allowableValues.length.asInstanceOf[Short])
				  }

				  override def discreteValue(__example: AnyRef): String = {
					  // TODO: catching errors (type checking)
					  return _discreteValue(__example)
				  }

				  private def _discreteValue(__example: AnyRef): String = {
					  val t: T = __example.asInstanceOf[T]
					  function(t)
				  }
			  }

		  }



	  }

	  val classifier = new ClassifierContainsInLBP{


		  this.containingPackage = "LBP_Package"
		  this.name = attName


		  def classify(__example: AnyRef): FeatureVector = {
			  new FeatureVector(featureValue(__example))
		  }

		  override def featureValue(__example: AnyRef): Feature = {
			  val result: String = discreteValue(__example)
			  new DiscretePrimitiveStringFeature(containingPackage, this.name, "", result, valueIndexOf(result), allowableValues.length.toShort)
		  }

		  override def discreteValue(__example: AnyRef): String = {
			  val d: T = __example.asInstanceOf[T]
			  function(d).toString
		  }

		  override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
			  super.classify(examples)
		  }



	  }


}
