/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property

import java.util

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, FeatureVector }

private[property] class LBPClassifier[T](val property: Property[T]) extends Classifier {
  name = property.name
  containingPackage = property.containingPackage

  override def hashCode(): Int = this.name.hashCode()

  override def equals(obj: scala.Any): Boolean = {
    obj.isInstanceOf[LBPClassifier[T]] && this.name.equals(obj.asInstanceOf[LBPClassifier[T]].name)
  }

  override def allowableValues(): Array[String] = property.allowableValues

  override def getOutputType: String = property.outputType

  override def classify(o: scala.Any): FeatureVector = property.featureVector(o.asInstanceOf[T])

  override def discreteValue(o: scala.Any): String = property.featureVector(o.asInstanceOf[T]).discreteValueArray().head

  override def realValue(o: scala.Any): Double = property.featureVector(o.asInstanceOf[T]).realValueArray().head

  override def discreteValueArray(o: scala.Any): Array[String] = property.featureVector(o.asInstanceOf[T]).discreteValueArray()

  override def realValueArray(o: scala.Any): Array[Double] = property.featureVector(o.asInstanceOf[T]).realValueArray()

  override def getCompositeChildren: util.LinkedList[_] = property.compositeChildren.orNull
}
