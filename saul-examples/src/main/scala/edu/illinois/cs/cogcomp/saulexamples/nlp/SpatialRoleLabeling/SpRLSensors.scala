/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
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
}
