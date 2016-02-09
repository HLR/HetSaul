package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.lbjava.infer.{ FirstOrderConstant, FirstOrderConstraint }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.data.XuPalmerCandidateGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.liApp.srlGraphs._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlClassifiers.{ argumentTypeLearner, argumentXuIdentifierGivenApredicate, predicateClassifier }

import scala.collection.JavaConversions._
/** Created by Parisa on 12/23/15.
  */
object srlConstraints {
  val noOverlap = ConstrainedClassifier.constraintOf[TextAnnotation] {
    {
      var a: FirstOrderConstraint = null
      x: TextAnnotation => {
        a = new FirstOrderConstant(true)
        (sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
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

  val r_arg_Constraint = ConstrainedClassifier.constraintOf[TextAnnotation] {

    var a: FirstOrderConstraint = null

    x: TextAnnotation => {
      a = new FirstOrderConstant(true)
      val values = Array("R-A1", "R-A2", "R-A3", "R-A4", "R-A5", "R-AA")
      (sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
        y =>
          {
            val argCandList = (predicates(y) ~> -relationsToPredicates).toList
            argCandList.foreach {
              t: Relation =>
                {
                  for (i <- 0 until values.length - 1)
                    a = a &&& ((argumentTypeLearner on t) is values(i)) ==>
                      argCandList._exists {
                        k: Relation => (argumentTypeLearner on k) is values(i).substring(2)
                      }
                }
            }
          }
      }
    }
    a
  } // end r-arg constraint

  val c_arg_Constraint = ConstrainedClassifier.constraintOf[TextAnnotation] {
    var a: FirstOrderConstraint = null
    x: TextAnnotation => {
      a = new FirstOrderConstant(true)
      val values = Array("C-A1", "C-A2", "C-A3", "C-A4", "C-A5", "C-AA")
      (sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
        y =>
          {
            val argCandList = (predicates(y) ~> -relationsToPredicates).toList
            val sortedCandidates = argCandList.sortBy(x => x.getTarget.getStartSpan)
            sortedCandidates.zipWithIndex.foreach {
              case (t, ind) =>
                {
                  if (ind > 0)
                    for (i <- 0 until values.length - 1)
                      a = a &&& ((argumentTypeLearner on t) is values(i)) ==>
                        argCandList.subList(0, ind)._exists {
                          k: Relation => (argumentTypeLearner on k) is values(i).substring(2)
                        }
                }
            }
          }
      }
    }
    a
  }

  val legal_arguments_Constraint = ConstrainedClassifier.constraintOf[TextAnnotation] {
    var a: FirstOrderConstraint = null
    x: TextAnnotation => {
      a = new FirstOrderConstant(true)
      (sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
        y =>
          {
            val argCandList = (predicates(y) ~> -relationsToPredicates).toList
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

  val noDuplicate = ConstrainedClassifier.constraintOf[TextAnnotation] {
    // Predicates have atmost one argument of each type i.e. there is no two arguments of the same type for each predicate
    val values = Array("A0", "A1", "A2", "A3", "A4", "A5", "AA")
    var a: FirstOrderConstraint = null
    x: TextAnnotation => {
      a = new FirstOrderConstant(true)
      (sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
        y =>
          {
            val argCandList = (predicates(y) ~> -relationsToPredicates).toList
            for (t1 <- 0 until argCandList.size - 1)
              for (t2 <- t1 + 1 until argCandList.size) {
                a = a &&& (((argumentTypeLearner on argCandList.get(t1)) in values) ==> (((argumentTypeLearner on argCandList.get(t1)) isNot (argumentTypeLearner on argCandList.get(t2)))))
              }
          }
      }
      a
    }
  }

  val r_and_c_args = ConstrainedClassifier.constraintOf[TextAnnotation] {
    x =>
      r_arg_Constraint(x) &&& c_arg_Constraint(x) &&& legal_arguments_Constraint(x) &&& noDuplicate(x)
  }

} // end srlConstainrs

