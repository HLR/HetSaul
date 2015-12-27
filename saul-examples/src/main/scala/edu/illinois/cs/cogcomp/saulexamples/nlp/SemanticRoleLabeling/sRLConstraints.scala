package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, TokenLabelView, Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.lbjava.infer.{ FirstOrderConstant, FirstOrderConstraint }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.argumentTypeLearner
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.relationAppXuPalmerCandidates._
import scala.collection.JavaConversions._
/** Created by Parisa on 12/23/15.
  */
object sRLConstraints {
  val noOverlap = ConstrainedClassifier.constraintOf[TextAnnotation]( // here this constituent is a sentence

    {
      var a: FirstOrderConstraint = new FirstOrderConstant(true)
      x: TextAnnotation => {
        (sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
          y =>
            {
              val argCandList = Xucandidates(y)

              x.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituents.toList.foreach {
                t: Constituent =>
                  val contains = argCandList.filter(x => x.getTarget.doesConstituentCover(t))
                  a = a &&& (contains.toList._atMost(1)({ p: Relation => (argumentTypeLearner on p).is("none")unary_! }))
              }
            }
        }
      }
      a
    }
  )
}

