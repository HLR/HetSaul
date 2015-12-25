package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.argumentTypeLearner

/** Created by Parisa on 12/21/15.
  */
object argClassifierAppGivenBoundaries extends App {
  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)

  argumentTypeLearner.learn(5)
  print("finished")
  argumentTypeLearner.test()
}
