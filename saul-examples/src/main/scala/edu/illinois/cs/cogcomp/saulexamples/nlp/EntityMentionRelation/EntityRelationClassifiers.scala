package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawSentence, ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationDataModel._

object EntityRelationClassifiers {

  /** entity classifiers */
  object OrganizationClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Org"
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(windowWithIn[ConllRawSentence](-2, 2, List(pos)), word, phrase, containsSubPhraseMent,
      containsSubPhraseIng, containsInPersonList, wordLen, containsInCityList)
  }

  object PersonClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Peop"
    override def feature = using(windowWithIn[ConllRawSentence](-2, 2, List(pos)), word, phrase, containsSubPhraseMent,
      containsSubPhraseIng, containsInPersonList, wordLen, containsInCityList)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object LocationClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Loc"
    override def feature = using(windowWithIn[ConllRawSentence](-2, 2, List(pos)), word, phrase, containsSubPhraseMent,
      containsSubPhraseIng, containsInPersonList, wordLen, containsInCityList)
    override lazy val classifier = new SparseNetworkLearner()
  }

  /** relation classifiers */
  object WorksForClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    def label: Property[ConllRelation] = relationType is "Work_For"
    override def feature = using(relFeature, relPos)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object WorkForClassifierPipe extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Work_For"
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(relFeature, relPos, ePipe)
  }

  object LivesInClassifierPipe extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Live_In"
    override def feature = using(relFeature, relPos, ePipe)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object LivesInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    def label: Property[ConllRelation] = relationType is "Live_In"
    override def feature = using(relFeature, relPos)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object OrgBasedInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "OrgBased_In"
    override lazy val classifier = new SparseNetworkLearner()
  }

  object LocatedInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Located_In"
    override lazy val classifier = new SparseNetworkLearner()
  }
}

