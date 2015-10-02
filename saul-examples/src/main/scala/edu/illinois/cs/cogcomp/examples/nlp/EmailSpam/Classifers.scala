package edu.illinois.cs.cogcomp.examples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.examples.nlp.EmailSpam.spamDataModel._
import edu.illinois.cs.cogcomp.lfs.classifier.Learnable
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.Attribute
import edu.illinois.cs.cogcomp.tutorial_related.Document
/**
 * Created by Parisa on 6/8/15.
 */
object Classifers {
  object spamClassifier extends Learnable[Document](spamDataModel) {

    def label: Attribute[Document] = spamLable is "spam"
    override def algorithm = "SparseNetwork"
    //override def feature = using(
    //word,phrase,containsSubPhraseMent,containsSubPhraseIng,
    // containsInPersonList,wordLen,containsInCityList
    // )
  }

}
