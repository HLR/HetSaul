package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamDataModel._

object spamClassifiers {
  object spamClassifier extends Learnable[Document](spamDataModel) {

    def label = spamLabel
    override def algorithm = "SparseNetwork"
    override def feature = using(wordFeature)
    //word,phrase,containsSubPhraseMent,containsSubPhraseIng,
    // containsInPersonList,wordLen,containsInCityList
    // )
  }

  object spamClassifierWithCache extends Learnable[Document](spamDataModel) {

    def label = spamLabel
    override def algorithm = "SparseNetwork"
    override def feature = using(wordFeature)
    //word,phrase,containsSubPhraseMent,containsSubPhraseIng,
    // containsInPersonList,wordLen,containsInCityList
    // )

    override val useCache = true
  }
}
