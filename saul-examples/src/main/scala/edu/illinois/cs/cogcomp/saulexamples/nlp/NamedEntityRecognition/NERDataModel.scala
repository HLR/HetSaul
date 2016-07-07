/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.NamedEntityRecognition

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

/** Created by Parisa on 5/17/16.
  */
object NERDataModel extends DataModel {

  val word = node[Constituent]

  val NERLabel = property(word) {
    x: Constituent => x.getLabel
  }
  val surface = property(word) {
    x: Constituent => x.getSurfaceForm
  }

}
