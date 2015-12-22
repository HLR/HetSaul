package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.relationClassifier

/** Created by Parisa on 12/21/15.
  */
object argClassifierAppGivenBoundaries extends App {
  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)
  relationClassifier.learn(10)
  relationClassifier.test()
}
