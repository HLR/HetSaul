package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, TextAnnotation }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

object POSDataModel extends DataModel {

  val tokens = node[Constituent]

  val posLabel = property[Constituent]("label") {
    x: Constituent => x.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x).get(0).getLabel
  }

  val wordFormFeature = property[Constituent]("wordForm") {
    x: Constituent =>
      var wordForm = x.getTextAnnotation.getView(ViewNames.TOKENS).getConstituentsCovering(x).get(0).getLabel
      if (wordForm.length() == 1) {
        if ("([{".indexOf(wordForm) != -1) {
          wordForm = "-LRB-"
        } else if (")]}".indexOf(wordForm) != -1) {
          wordForm = "-RRB-"
        }
      }

      wordForm
  }
}
