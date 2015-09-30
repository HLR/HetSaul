package edu.illinois.cs.cogcomp.lfs.data_model.attribute.features

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier

/**
 * Created by haowu on 2/6/15.
 */
abstract class ClassifierContainsInLBP extends Classifier{


  this.containingPackage = "LBP_Package"

  override def hashCode(): Int = this.name.hashCode()

  override def equals(obj: scala.Any): Boolean = {
     obj.isInstanceOf[ClassifierContainsInLBP] && this.name.equals(obj.asInstanceOf[ClassifierContainsInLBP].name)
  }
}