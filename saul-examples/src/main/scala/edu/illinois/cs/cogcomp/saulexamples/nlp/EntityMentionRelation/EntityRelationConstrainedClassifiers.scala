package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationConstraints._

object EntityRelationConstrainedClassifiers {
  object OrgConstrainedClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](EntityRelationDataModel, OrganizationClassifier) {
    def subjectTo = relationArgumentConstraints
    override val pathToHead = Some(-EntityRelationDataModel.pairTo2ndArg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId1
  }

  object PerConstrainedClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](EntityRelationDataModel, PersonClassifier) {
    def subjectTo = relationArgumentConstraints
    override val pathToHead = Some(-EntityRelationDataModel.pairTo1stArg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
  }

  object LocConstrainedClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](EntityRelationDataModel, LocatedInClassifier) {
    def subjectTo = relationArgumentConstraints
    override val pathToHead = Some(-EntityRelationDataModel.pairTo2ndArg)
    //TODO add test unit for this filter
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
  }

  object Work_P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](EntityRelationDataModel, WorksForClassifier) {
    def subjectTo = relationArgumentConstraints
  }

  object LiveIn_P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](EntityRelationDataModel, LivesInClassifier) {
    def subjectTo = relationArgumentConstraints
  }
}
