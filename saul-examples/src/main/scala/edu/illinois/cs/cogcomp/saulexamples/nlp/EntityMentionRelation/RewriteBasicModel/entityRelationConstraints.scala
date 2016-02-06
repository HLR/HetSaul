package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRelation, ConllRawToken }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationClassifiers._
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationConstraints._

object entityRelationConstraints {

  object orgConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](entityRelationBasicDataModel, orgClassifier) {
    def subjectTo = Per_Org
    override val pathToHead = Some(entityRelationBasicDataModel.RelationToOrg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId1
  }

  object PerConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](entityRelationBasicDataModel, personClassifier) {

    def subjectTo = Per_Org
    //    override val pathToHead = Some(entityRelationBasicDataModel.RelationToPer)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
  }

  object LocConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](entityRelationBasicDataModel, locationClassifier) {

    def subjectTo = Per_Org
    // override val pathToHead = Some('containE2)
    //TODO add test unit for this filter
    //    override def filter(t: ConllRawToken,h:ConllRelation): Boolean = t.wordId==h.wordId2
  }

  object P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](entityRelationBasicDataModel, worksForClassifier) {
    def subjectTo = Per_Org
  }

  object LiveIn_P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](entityRelationBasicDataModel, livesInClassifier) {
    def subjectTo = Per_Org
  }
}
