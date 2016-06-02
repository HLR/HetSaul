package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ argumentTypeLearner, argumentXuIdentifierGivenApredicate }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlConstraints._

/** Created by Parisa on 12/27/15.
  */
object srlConstraintClassifiers {
  import srlApp._
  object argTypeConstraintClassifier extends ConstrainedClassifier[Relation, TextAnnotation](argumentTypeLearner) {
    def subjectTo = r_and_c_args
    override val pathToHead = Some(-srlGraphs.sentencesToRelations)
  }

  object arg_Is_TypeConstraintClassifier extends ConstrainedClassifier[Relation, Relation](argumentTypeLearner) {
    def subjectTo = arg_IdentifierClassifier_Constraint
  }

  object arg_IdentifyConstraintClassifier extends ConstrainedClassifier[Relation, Relation](argumentXuIdentifierGivenApredicate) {
    def subjectTo = arg_IdentifierClassifier_Constraint
  }

}

