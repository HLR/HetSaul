package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.lbjava.infer.{ FirstOrderConstant, FirstOrderConstraint }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.data.XuPalmerCandidateGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ argumentTypeLearner, argumentXuIdentifierGivenApredicate, predicateClassifier }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlSensors._

import scala.collection.JavaConversions._
/** Created by Parisa on 12/23/15.
  */
object srlConstraints {
  val noOverlap = ConstrainedClassifier.constraintOf[TextAnnotation] {
    // here this constituent is a sentence
    {
      var a: FirstOrderConstraint = new FirstOrderConstant(true)
      x: TextAnnotation => {
        //using TextAnnotation
        x.getView(ViewNames.SRL_VERB).asInstanceOf[PredicateArgumentView].getPredicates.foreach {
          //using the graph
          //(sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
          y =>
            {
              val argCandList = XuPalmerCandidateGenerator.generateCandidates(y, (sentences(y.getTextAnnotation) ~> sentencesToStringTree).head).
                map(y => new Relation("candidate", y.cloneForNewView(y.getViewName), y.cloneForNewView(y.getViewName), 0.0))

              x.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituents.toList.foreach {
                t: Constituent =>
                  {
                    val contains = argCandList.filter(z => z.getTarget.doesConstituentCover(t))
                    a = a &&& contains.toList._atMost(1)({ p: Relation => (argumentTypeLearner on p).is("candidate") })
                  }
              }
            }
        }
      }
      a
    }
  } //end of NoOverlap constraint

  val noDuplicate = ConstrainedClassifier.constraintOf[TextAnnotation] {
    // Predicates have atmost one argument of each type i.e. there is no two arguments of the same type for each predicate
    var a: FirstOrderConstraint = new FirstOrderConstant(true)
    x: TextAnnotation => {
      //using TextAnnotation
      x.getView(ViewNames.SRL_VERB).asInstanceOf[PredicateArgumentView].getPredicates.foreach {
        //using the graph
        //(sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
        y =>
          {
            val argCandList = xuPalmerCandidate(y, (sentences(y.getTextAnnotation) ~> sentencesToStringTree).head)
            for (t1 <- 0 until argCandList.size - 1)
              for (t2 <- t1 + 1 until argCandList.size) {
                a = a &&& (((argumentTypeLearner on argCandList.get(t1)) is (argumentTypeLearner on argCandList.get(t2))) unary_!)
              }
          }
      }
      a
    }
  }
  val arg_IdentifierClassifier_Constraint = ConstrainedClassifier.constraintOf[Relation] {

    x: Relation =>
      {
        (argumentXuIdentifierGivenApredicate on x isNotTrue) ==>
          (argumentTypeLearner on x is "candidate")
      }
  }

  val predArg_IdentifierClassifier_Constraint = ConstrainedClassifier.constraintOf[Relation] {
    x: Relation =>
      {
        (predicateClassifier on x.getSource isTrue) &&& (argumentXuIdentifierGivenApredicate on x isTrue) ==>
          (argumentTypeLearner on x isNot "candidate")
      }
  }

  val legal_arguments_Constraint = ConstrainedClassifier.constraintOf[TextAnnotation] {
    var a: FirstOrderConstraint = new FirstOrderConstant(true)
    x: TextAnnotation => {

      x.getView(ViewNames.SRL_VERB).asInstanceOf[PredicateArgumentView].getPredicates.foreach {
        y =>
          {
            val argCandList = xuPalmerCandidate(y, (sentences(y.getTextAnnotation) ~> sentencesToStringTree).head)
            val argLegalList = legalArguments(y)
            argCandList.foreach {
              z =>
                a = a &&& argLegalList._exists {
                  t: String => argumentTypeLearner on z is t
                }
            }
          }
      }
    }
    a
  }
  val r_arg_Constraint = ConstrainedClassifier.constraintOf[TextAnnotation] {
    var a: FirstOrderConstraint = new FirstOrderConstant(true)
    x: TextAnnotation => {
      val values = Array("R-A1", "R-A2", "R-A3", "R-A4", "R-A5")
      x.getView(ViewNames.SRL_VERB).asInstanceOf[PredicateArgumentView].getPredicates.foreach {
        y =>
          {
            val argCandList = xuPalmerCandidate(y, (sentences(y.getTextAnnotation) ~> sentencesToStringTree).head)
            argCandList.foreach {
              t: Relation =>
                {
                  for (i <- 0 until values.length - 1)
                    a = a &&& ((argumentTypeLearner on t) is values(i)) ==>
                      argCandList._exists {
                        k: Relation => (argumentTypeLearner on k) is values(0).substring(2)
                      }
                }
            }
          }
      }
    }
    a
  } // end r-arg constraint
  val c_arg_Constraint = ConstrainedClassifier.constraintOf[TextAnnotation] {
    var a: FirstOrderConstraint = new FirstOrderConstant(true)
    x: TextAnnotation => {
      val values = Array("C-A1", "C-A2", "C-A3", "C-A4", "C-A5")
      x.getView(ViewNames.SRL_VERB).asInstanceOf[PredicateArgumentView].getPredicates.foreach {
        y =>
          {
            val argCandList = xuPalmerCandidate(y, (sentences(y.getTextAnnotation) ~> sentencesToStringTree).head)
            argCandList.foreach {
              t: Relation =>
                {
                  for (i <- 0 until values.length - 1)
                    a = a &&& ((argumentTypeLearner on t) is values(i)) ==>
                      argCandList._exists {
                        k: Relation => (argumentTypeLearner on k) is values(0).substring(2)
                      }
                }
            }
          }
      }
    }
    a
  } // end r-arg constraint
} // end srlConstainrs