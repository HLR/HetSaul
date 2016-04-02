package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.lbjava.infer.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstraints._

object EntityRelationConstrainedClassifiers {
  val erSolver = new OJalgoHook

  object OrgConstrainedClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](EntityRelationDataModel, OrganizationClassifier) {
    def subjectTo = relationArgumentConstraints
    override val pathToHead = Some(-EntityRelationDataModel.pairTo2ndArg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId1
    override val solver = erSolver
  }

  object PerConstrainedClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](EntityRelationDataModel, PersonClassifier) {
    def subjectTo = relationArgumentConstraints
    override val pathToHead = Some(-EntityRelationDataModel.pairTo1stArg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
    override val solver = erSolver
  }

  object LocConstrainedClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](EntityRelationDataModel, LocationClassifier) {
    def subjectTo = relationArgumentConstraints
    override val pathToHead = Some(-EntityRelationDataModel.pairTo2ndArg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
    override val solver = erSolver
  }

  object WorksFor_PerOrg_ConstrainedClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](EntityRelationDataModel, WorksForClassifier) {
    def subjectTo = relationArgumentConstraints
    override val solver = new OJalgoHook
  }

  object LivesIn_PerOrg_relationConstrainedClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](EntityRelationDataModel, LivesInClassifier) {
    def subjectTo = relationArgumentConstraints
    override val solver = erSolver
  }
}
