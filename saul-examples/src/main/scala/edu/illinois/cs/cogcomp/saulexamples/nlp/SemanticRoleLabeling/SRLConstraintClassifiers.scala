package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.{ argumentXuIdentifierGivenApredicate, argumentTypeLearner }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.sRLConstraints._

/** Created by Parisa on 12/27/15.
  */
object SRLConstraintClassifiers {

  object argTypeConstraintClassifier extends ConstrainedClassifier[Relation, TextAnnotation](SRLDataModel, argumentTypeLearner) {
    def subjectTo = noOverlap

  }

  object arg_TypeConstraintClassifier extends ConstrainedClassifier[Relation, Relation](SRLDataModel, argumentTypeLearner) {
    def subjectTo = arg_IdentifierClassifier_Constraint
  }

  object arg_IdentifyConstraintClassifier extends ConstrainedClassifier[Relation, Relation](SRLDataModel, argumentXuIdentifierGivenApredicate) {
    def subjectTo = arg_IdentifierClassifier_Constraint
  }

}

