package edu.illinois.cs.cogcomp.lfs.data_model.attribute

import edu.illinois.cs.cogcomp.lbjava.classify.{ DiscretePrimitiveStringFeature, Feature, FeatureVector, Classifier }

import scala.reflect.ClassTag

/** Created by haowu on 1/27/15.
  */
//case class DiscreteAttribute[T](
//                    val mapping: T => String,
//                    val range : Option[List[String]]
//                            )(implicit val tag : ClassTag[T]) extends TypedAttribute[T,String]{
//
//  val fdt = this
//  val name = "todo"
//
//
//  override val classifier: Classifier = range match {
//    case Some(r) => {
//      new Classifier() {
//        private var __allowableValues: Array[String] =  r.toArray
//
//        containingPackage = ""
//        name = fdt.getClass.getName
//
//        def getAllowableValues: Array[String] = {
//          return __allowableValues
//        }
//
//        override def allowableValues: Array[String] = {
//          return __allowableValues
//        }
//
//        def classify(__example: AnyRef): FeatureVector = {
//          return new FeatureVector(featureValue(__example))
//        }
//
//        override def featureValue(__example: AnyRef): Feature = {
//          val result: String = discreteValue(__example)
//          return new DiscretePrimitiveStringFeature(containingPackage, name, "", result, valueIndexOf(result), allowableValues.length.asInstanceOf[Short])
//        }
//
//        override def discreteValue(__example: AnyRef): String = {
//          // TODO: catching errors (type checking)
//          _discreteValue(__example)
//        }
//
//        private def _discreteValue(__example: AnyRef): String = {
//          val t: T = __example.asInstanceOf[T]
//          fdt.mapping(t).mkString("")
//        }
//      }
//
//    }
//    case _ => null
//
//  }
//
//  override def addToFeatureVector(t: T, fv: FeatureVector): FeatureVector = ???
//}
