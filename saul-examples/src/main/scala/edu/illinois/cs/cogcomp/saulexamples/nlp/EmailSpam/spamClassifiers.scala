package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.lbjava.learn.SparseAveragedPerceptron
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamDataModel._
import edu.illinois.cs.cogcomp.saul.classifier.Learnable

object spamClassifiers {
  val parameters = new SparseAveragedPerceptron.Parameters()
  parameters.modelDir = "model"
  object spamClassifier extends Learnable[Document](spamDataModel, parameters) {

    def label: Property[Document] = spamLabel
    override def algorithm = "SparseNetwork"
    //override def feature = using(
    //word,phrase,containsSubPhraseMent,containsSubPhraseIng,
    // containsInPersonList,wordLen,containsInCityList
    // )
  }
}
