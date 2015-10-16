package edu.illinois.cs.cogcomp.saulexamples.nlp.SRL

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.Attribute
import SRLDataModel._

/** Created by Parisa on 10/16/15.
  */
object SRLClassifiers {

  object predicateClassifier extends Learnable[Constituent](SRLDataModel) {

    def label: Attribute[Constituent] = predicateLable
    override def algorithm = "SparseNetwork"
    //    override def feature = using(
    //    word,phrase,containsSubPhraseMent,containsSubPhraseIng,
    //    containsInPersonList,wordLen,containsInCityList
    //    )
  }
}
