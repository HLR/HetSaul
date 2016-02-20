package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawSentence, ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationDataModel._

object EntityRelationClassifiers {

  object orgClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Org"
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(windowWithIn[ConllRawSentence](-2, 2, List(pos)), word, phrase, containsSubPhraseMent, containsSubPhraseIng,
      containsInPersonList, wordLen, containsInCityList)
  }

  object personClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Peop"
    override def feature = using(windowWithIn[ConllRawSentence](-2, 2, List(pos)), word, phrase, containsSubPhraseMent, containsSubPhraseIng,
      containsInPersonList, wordLen, containsInCityList)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object locationClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Loc"
    override def feature = using(windowWithIn[ConllRawSentence](-2, 2, List(pos)), word, phrase, containsSubPhraseMent, containsSubPhraseIng,
      containsInPersonList, wordLen, containsInCityList)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object worksForClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    def label: Property[ConllRelation] = relationType is "Work_For"
    override def feature = using(relFeature, relPos)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object workForClassifierPipe extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Work_For"
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(
      relFeature, relPos, ePipe
    )
  }
  object livesInClassifierPipe extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Live_In"
    override def feature = using(relFeature, relPos, ePipe)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object livesInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    def label: Property[ConllRelation] = relationType is "Live_In"
    override def feature = using(relFeature, relPos)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object org_baseClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "OrgBased_In"
    override lazy val classifier = new SparseNetworkLearner()
  }
  object locatedInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Located_In"
    override lazy val classifier = new SparseNetworkLearner()
  }

}

