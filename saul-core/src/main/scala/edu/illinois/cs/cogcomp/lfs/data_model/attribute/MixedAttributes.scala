package edu.illinois.cs.cogcomp.lfs.data_model.attribute

import java.util

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.lfs.data_model.DataModel
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

/** Created by haowu on 2/10/15.
  */
//class MixedAttribute[T <: AnyRef,CHILD <: AnyRef](
//	                                 val dm: DataModel,
//	                                 val before: Int,
//	                                 val after: Int,
//	                                 val filters: List[Symbol],
//	                                 val atts: List[Attribute[T]] // T => (String | Double | List[String] |
//	                                 // List[Double])
//                                 )(implicit val tag: ClassTag[T]) extends TypedAttribute[T, List[_]] {
//
//	override val mapping: (T) => List[_] = _
//	override def addToFeatureVector(t: T, fv: FeatureVector): FeatureVector = ???
//	override val name: String = _
//
//
//
//	override val classifier: ClassifierContainsInLBP = {
//		new ClassifierContainsInLBP{
//
//			this.containingPackage = "LBP_Package"
//			this.name = fdt.name
//
//			override def getOutputType: String = {
//				return "mixed%"
//			}
//
//
//			def classify(__example: AnyRef): FeatureVector = {
//
//				val t: T = __example.asInstanceOf[T]
//
//				val __result: FeatureVector = new FeatureVector()
//
//
//
//				atts.foreach(_.addToFeatureVector(t,__result))
//
//				__result
//			}
//
//
//
//			override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
//				super.classify(examples)
//			}
//
//
//			override def getCompositeChildren: util.LinkedList[_] = {
//				val result: util.LinkedList[Classifier] = new util.LinkedList[Classifier]()
//				atts.foreach(x => result.add(x.classifier))
//				result
//			}
//
//
//
//		}
//	}
//
//	override def makeClassifierWithName(n: String): Classifier = {
//		???
//	}
//}
