/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent

import scala.collection.JavaConversions._

object POSTaggerSensors {
  def getConstituentAfter(x: Constituent): Constituent = {
    val consAfter = x.getView.getConstituents.toList.filter(cons => cons.getStartSpan >= x.getEndSpan)
    if (consAfter.nonEmpty) consAfter.minBy(_.getEndSpan)
    else x
  }

  def getConstituentBefore(x: Constituent): Constituent = {
    val consBefore = x.getView.getConstituents.toList.filter(cons => cons.getEndSpan <= x.getStartSpan)
    if (consBefore.nonEmpty) consBefore.maxBy(_.getEndSpan)
    else x
  }

  def getConstituentTwoAfter(x: Constituent): Constituent = {
    val consAfter = x.getView.getConstituents.toList.filter(cons => cons.getStartSpan >= x.getEndSpan)
    if (consAfter.size >= 2) consAfter.sortBy(_.getEndSpan).get(1)
    else x
  }

  def getConstituentTwoBefore(x: Constituent): Constituent = {
    val consBefore = x.getView.getConstituents.toList.filter(cons => cons.getEndSpan <= x.getStartSpan)
    if (consBefore.size >= 2) consBefore.sortBy(-_.getEndSpan).get(1)
    else x
  }
}