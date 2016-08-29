/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features.real

import edu.illinois.cs.cogcomp.lbjava.classify.{ RealArrayStringFeature, FeatureVector, Classifier }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

/** Represents real valued array attributes.
  *
  * @param name Name of the property
  * @param sensor Sensor function used to generate attributes from nodes.
  * @param tag ClassTag for the type of data stored by the attribute node
  * @tparam T Type of the node that this property is associated with.
  */
case class RealArrayProperty[T <: AnyRef](name: String, sensor: T => List[Double])(implicit val tag: ClassTag[T])
  extends RealPropertyCollection[T] with TypedProperty[T, List[Double]] {

  // TODO: shouldn't this be this.name?
  val ra = this

  override def makeClassifierWithName(__name: String): Classifier = new ClassifierContainsInLBP() {
    this.containingPackage = "LBP_Package"

    this.name = __name

    override def realValueArray(instance: AnyRef): Array[Double] = {
      classify(instance).realValueArray
    }

    override def classify(instance: Array[AnyRef]): Array[FeatureVector] = {
      super.classify(instance)
    }

    def classify(example: AnyRef): FeatureVector = {

      val d: T = example.asInstanceOf[T]
      val values = sensor(d)

      val featureVector = new FeatureVector

      values.zipWithIndex.foreach {
        case (value, idx) => featureVector.addFeature(new RealArrayStringFeature(this.containingPackage, this.name, "", value, idx, 0))
      }

      // TODO: Commented by Daniel. Make sure this does not create any bugs
      //(0 to values.size) foreach { x => featureVector.getFeature(x).setArrayLength(values.size) }

      featureVector
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
