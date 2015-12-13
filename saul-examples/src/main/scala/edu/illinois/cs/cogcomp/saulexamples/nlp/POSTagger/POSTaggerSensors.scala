package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent

import scala.collection.JavaConversions._

object POSTTaggerSensors {
  def getConstituentAfter(x: Constituent): Constituent = {
    val consAfter = x.getView.getConstituents.toList.filter(cons => cons.getStartSpan >= x.getEndSpan)
    if (!consAfter.isEmpty) consAfter.minBy(_.getEndSpan)
    else x
  }

  def getConstituentBefore(x: Constituent): Constituent = {
    val consBefore = x.getView.getConstituents.toList.filter(cons => cons.getEndSpan <= x.getStartSpan)
    if (!consBefore.isEmpty) consBefore.maxBy(_.getEndSpan)
    else x
  }

  def getConstituentTwoAfter(x: Constituent): Constituent = {
    val consAfter = x.getView.getConstituents.toList.filter(cons => cons.getStartSpan >= x.getEndSpan)
    if (consAfter.size >= 2) consAfter.minBy(_.getEndSpan)
    else if (consAfter.size == 1) consAfter.head
    else x
  }

  def getConstituentTwoBefore(x: Constituent): Constituent = {
    val consBefore = x.getView.getConstituents.toList.filter(cons => cons.getEndSpan <= x.getStartSpan)
    if (consBefore.size >= 2) consBefore.maxBy(_.getEndSpan)
    else if (consBefore.size == 1) consBefore.head
    else x
  }

}
