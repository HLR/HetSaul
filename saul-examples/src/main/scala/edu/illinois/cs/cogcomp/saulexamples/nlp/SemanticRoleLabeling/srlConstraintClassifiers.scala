package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Relation, TextAnnotation}
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{argumentTypeLearner, argumentXuIdentifierGivenApredicate}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlConstraints._
/** Created by Parisa on 12/27/15.
  */
object srlConstraintClassifiers {

  object argTypeConstraintClassifier extends ConstrainedClassifier[Relation, TextAnnotation](srlDataModel, argumentTypeLearner) {
    def subjectTo = r_and_c_args
  }

  object arg_Is_TypeConstraintClassifier extends ConstrainedClassifier[Relation, Relation](srlDataModel, argumentTypeLearner) {
    def subjectTo = arg_IdentifierClassifier_Constraint
  }

  object arg_IdentifyConstraintClassifier extends ConstrainedClassifier[Relation, Relation](srlDataModel, argumentXuIdentifierGivenApredicate) {
    def subjectTo = arg_IdentifierClassifier_Constraint
  }

}

