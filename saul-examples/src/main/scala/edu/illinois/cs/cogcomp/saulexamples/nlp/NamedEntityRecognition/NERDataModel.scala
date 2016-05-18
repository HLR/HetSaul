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
