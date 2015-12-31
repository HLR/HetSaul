package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiersForExperiment.{ argumentTypeLearner1, argumentXuIdentifierGivenApredicate1 }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.sRLConstraints._
/** Created by Parisa on 12/30/15.
  */
object SRLConstraintClassifiersForExperiments {

  object argTypeConstraintClassifier1 extends ConstrainedClassifier[Relation, TextAnnotation](SRLDataModel, argumentTypeLearner1) {
    def subjectTo = noOverlap
  }

  object arg_TypeConstraintClassifier1 extends ConstrainedClassifier[Relation, Relation](SRLDataModel, argumentTypeLearner1) {
    def subjectTo = arg_IdentifierClassifier_Constraint
  }

  object arg_IdentifyConstraintClassifier1 extends ConstrainedClassifier[Relation, Relation](SRLDataModel, argumentXuIdentifierGivenApredicate1) {
    def subjectTo = arg_IdentifierClassifier_Constraint
  }
}
