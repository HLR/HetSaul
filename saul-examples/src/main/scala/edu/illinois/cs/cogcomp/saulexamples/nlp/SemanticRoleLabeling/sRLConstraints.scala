package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.lbjava.infer.{ FirstOrderConstant, FirstOrderConstraint }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.data.XuPalmerCandidateGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.argumentTypeLearner
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._

import scala.collection.JavaConversions._
/** Created by Parisa on 12/23/15.
  */
object sRLConstraints {
  val noOverlap = ConstrainedClassifier.constraintOf[TextAnnotation]( // here this constituent is a sentence

    {
      var a: FirstOrderConstraint = new FirstOrderConstant(true)
      val t = new XuPalmerCandidateGenerator(null)
      x: TextAnnotation => {
        //using TextAnnotation
        x.getView(ViewNames.SRL_VERB).asInstanceOf[PredicateArgumentView].getPredicates.foreach {
          //using the graph
          //(sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
          y =>
            {
              val argCandList = (t.generateSaulCandidates(y, (sentences(y.getTextAnnotation) ~> sentencesTostringTree).head)).
                map(y => new Relation("candidate", y.cloneForNewView(y.getViewName), y.cloneForNewView(y.getViewName), 0.0))
              //Xucandidates(y)

              x.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituents.toList.foreach {
                t: Constituent =>
                  val contains = argCandList.filter(x => x.getTarget.doesConstituentCover(t))
                  a = a &&& (contains.toList._atMost(1)({ p: Relation => (argumentTypeLearner on p).isNot("candidate") }))
              }
            }
        }
      }
      a
    }
  )
}

