/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstraints._

object EntityRelationConstrainedClassifiers {
  val erSolver = new OJalgoHook

  object OrgConstrainedClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](OrganizationClassifier) {
    def subjectTo = relationArgumentConstraints
    override val pathToHead = Some(-EntityRelationDataModel.pairTo2ndArg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
    override val solver = erSolver
  }

  object PerConstrainedClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](PersonClassifier) {
    def subjectTo = relationArgumentConstraints
    override val pathToHead = Some(-EntityRelationDataModel.pairTo1stArg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId1
    override val solver = erSolver
  }

  object LocConstrainedClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](LocationClassifier) {
    def subjectTo = relationArgumentConstraints
    override val pathToHead = Some(-EntityRelationDataModel.pairTo2ndArg)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
    override val solver = erSolver
  }

  object WorksFor_PerOrg_ConstrainedClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](WorksForClassifier) {
    def subjectTo = relationArgumentConstraints
    override val solver = new OJalgoHook
  }

  object LivesIn_PerOrg_relationConstrainedClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](LivesInClassifier) {
    def subjectTo = relationArgumentConstraints
    override val solver = erSolver
  }
}
