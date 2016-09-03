/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property.features

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier

abstract class ClassifierContainsInLBP extends Classifier {

  this.containingPackage = "LBP_Package"

  override def hashCode(): Int = this.name.hashCode()

  override def equals(obj: scala.Any): Boolean = {
    obj.isInstanceOf[ClassifierContainsInLBP] && this.name.equals(obj.asInstanceOf[ClassifierContainsInLBP].name)
  }
}