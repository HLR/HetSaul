package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRelation, ConllRawToken }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationBasicDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationClassifiers._
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationConstraints._

object entityRelationConstraints {

  object orgConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](RelationToOrg, orgClassifier) {
    def subjectTo = Per_Org
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId1
  }

  object PerConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](RelationToPer, personClassifier) {
    def subjectTo = Per_Org
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
  }

  //TODO Where is RelationToLoc getting populated?
  object LocConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](RelationToLoc, locationClassifier) {
    def subjectTo = Per_Org
    //TODO add test unit for this filter
    //    override def filter(t: ConllRawToken,h:ConllRelation): Boolean = t.wordId==h.wordId2
  }

  //TODO Where is RelationToRelation getting populated?
  //TODO Aren't these two classifiers the same?
  object P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](RelationToRelation, worksForClassifier) {
    def subjectTo = Per_Org
  }

  object LiveIn_P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](RelationToRelation, livesInClassifier) {
    def subjectTo = Per_Org
  }
}
