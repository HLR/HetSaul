/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation }
import edu.illinois.cs.cogcomp.edison.features.Feature
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors

/** Created by taher on 7/28/16.
  */
object SpRLSensors {

  // helper methods
  def isCandidate(token: Constituent): Boolean = {

    if (Dictionaries.isPreposition(token.toString))
      return true

    val pos = CommonSensors.getPosTag(token)
    if (pos == "IN" || pos == "TO")
      return true

    return false
  }

  def getConstituentId(x: Constituent): String =
    x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSentenceId + ":" + x.getSpan

  def getUniqueSentenceId(x: Constituent): String =
    x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSentenceId

  def getPairFeatures(source: Constituent, target: Constituent,
    func: (Constituent) => java.util.Set[Feature]): java.util.Set[Feature] = {

    val r = new Relation("r", source, target, 0.1)
    val result = func(target)
    source.getOutgoingRelations.remove(r)
    result
  }

}
