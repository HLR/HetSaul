/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.{ argumentTypeLearner, argumentXuIdentifierGivenApredicate }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLConstraints._

/** Created by Parisa on 12/27/15.
  */
object SRLConstrainedClassifiers {
  import SRLApps.srlDataModelObject._
  val erSolver = new OJalgoHook

  object argTypeConstraintClassifier extends ConstrainedClassifier[Relation, TextAnnotation](argumentTypeLearner) {
    def subjectTo = r_and_c_args
    override val solver = erSolver
    override val pathToHead = Some(-sentencesToRelations)
  }

  object arg_Is_TypeConstraintClassifier extends ConstrainedClassifier[Relation, Relation](argumentTypeLearner) {
    def subjectTo = arg_IdentifierClassifier_Constraint
    override val solver = erSolver
  }

  object arg_IdentifyConstraintClassifier extends ConstrainedClassifier[Relation, Relation](argumentXuIdentifierGivenApredicate) {
    def subjectTo = arg_IdentifierClassifier_Constraint
    override val solver = erSolver
  }

}

