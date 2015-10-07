package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamDataModel._
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.Attribute

/** Created by Parisa on 6/8/15.
  */
object classifers {
  object spamClassifier extends Learnable[Document](spamDataModel) {

    def label: Attribute[Document] = spamLable is "spam"
    override def algorithm = "SparseNetwork"
    //override def feature = using(
    //word,phrase,containsSubPhraseMent,containsSubPhraseIng,
    // containsInPersonList,wordLen,containsInCityList
    // )
  }
}
