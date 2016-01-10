package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

object POSDataModel extends DataModel {

  val tokens = node[Constituent]

  val posLabel = property(tokens, "label") {
    x: Constituent => x.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x).get(0).getLabel
  }

  val wordFormFeature = property(tokens, "wordForm") {
    x: Constituent =>
      val wordForm = x.getTextAnnotation.getView(ViewNames.TOKENS).getConstituentsCovering(x).get(0).getLabel

      if (wordForm.length == 1 && "([{".indexOf(wordForm) != -1) {
        "-LRB-"
      } else if (wordForm.length == 1 && ")]}".indexOf(wordForm) != -1) {
        "-RRB-"
      } else {
        wordForm
      }
  }
}
