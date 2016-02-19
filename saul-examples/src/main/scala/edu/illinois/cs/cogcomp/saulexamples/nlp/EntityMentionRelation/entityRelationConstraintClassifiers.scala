package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationConstraints._

object EntityRelationConstraintClassifiers {

  object orgConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](EntityRelationDataModel, orgClassifier) {
    def subjectTo = Per_Org
    override val pathToHead = Some(-EntityRelationDataModel.pairTo2ndArg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId1
  }

  object PerConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](EntityRelationDataModel, personClassifier) {

    def subjectTo = Per_Org
    override val pathToHead = Some(-EntityRelationDataModel.pairTo1stArg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
  }

  object LocConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](EntityRelationDataModel, locationClassifier) {

    def subjectTo = Per_Org
    override val pathToHead = Some(-EntityRelationDataModel.pairTo2ndArg)
    //TODO add test unit for this filter
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
  }

  object P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](EntityRelationDataModel, worksForClassifier) {
    def subjectTo = Per_Org
  }

  object LiveIn_P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](EntityRelationDataModel, livesInClassifier) {
    def subjectTo = Per_Org
  }
}
