package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{TextAnnotation, Relation}
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.argumentTypeLearner
import sRLConstraints._
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._

/**
 * Created by Parisa on 12/27/15.
 */
object SRLConstraintClassifiers {
  object argTypeConstraintClassifier extends ConstrainedClassifier[Relation, TextAnnotation](SRLDataModel, argumentTypeLearner) {
    def subjectTo = noOverlap

  }
}

