/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.lbjava.infer.{ FirstOrderConstant, FirstOrderConstraint }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.data.XuPalmerCandidateGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLApps.srlDataModelObject._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.{ argumentTypeLearner, argumentXuIdentifierGivenApredicate, predicateClassifier }

import scala.collection.JavaConversions._
/** Created by Parisa on 12/23/15.
  */
object SRLConstraints {
  val noOverlap = ConstrainedClassifier.constraint[TextAnnotation] {
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
                    a = a and contains.toList._atmost(1)({ p: Relation => (argumentTypeLearner on p).is("candidate") })
                  }
              }
            }
        }
      }
      a
    }
  } //end of NoOverlap constraint

  val arg_IdentifierClassifier_Constraint = ConstrainedClassifier.constraint[Relation] {
    x: Relation =>
      {
        (argumentXuIdentifierGivenApredicate on x isNotTrue) ==>
          (argumentTypeLearner on x is "candidate")
      }
  }

  val predArg_IdentifierClassifier_Constraint = ConstrainedClassifier.constraint[Relation] {
    x: Relation =>
      {
        (predicateClassifier on x.getSource isTrue) and (argumentXuIdentifierGivenApredicate on x isTrue) ==>
          (argumentTypeLearner on x isNot "candidate")
      }
  }

  val r_arg_Constraint = ConstrainedClassifier.constraint[TextAnnotation] {
    var a: FirstOrderConstraint = null
    x: TextAnnotation => {
      a = new FirstOrderConstant(true)
      val values = Array("R-A1", "R-A2", "R-A3", "R-A4", "R-A5", "R-AA", "R-AM-ADV", "R-AM-CAU", "R-AM-EXT", "R-AM-LOC", "R-AM-MNR", "R-AM-PNC")
      (sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
        y =>
          {
            val argCandList = (predicates(y) ~> -relationsToPredicates).toList
            argCandList.foreach {
              t: Relation =>
                {
                  for (i <- 0 until values.length)
                    a = a and ((argumentTypeLearner on t) is values(i)) ==>
                      argCandList.filterNot(x => x.equals(t))._exists {
                        k: Relation => (argumentTypeLearner on k) is values(i).substring(2)
                      }
                }
                a
            }
          }
      }
    }
    a
  } // end r-arg constraint

  val c_arg_Constraint = ConstrainedClassifier.constraint[TextAnnotation] {
    var a: FirstOrderConstraint = null
    x: TextAnnotation => {
      a = new FirstOrderConstant(true)
      val values = Array("C-A1", "C-A2", "C-A3", "C-A4", "C-A5", "C-AA", "C-AM-DIR", "C-AM-LOC", "C-AM-MNR", "C-AM-NEG", "C-AM-PNC")
      (sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
        y =>
          {
            val argCandList = (predicates(y) ~> -relationsToPredicates).toList
            val sortedCandidates = argCandList.sortBy(x => x.getTarget.getStartSpan)
            sortedCandidates.zipWithIndex.foreach {
              case (t, ind) =>
                {
                  if (ind > 0)
                    for (i <- 0 until values.length)
                      a = a and ((argumentTypeLearner on t) is values(i)) ==>
                        sortedCandidates.subList(0, ind)._exists {
                          k: Relation => (argumentTypeLearner on k) is values(i).substring(2)
                        }
                }
            }
          }
      }
    }
    a
  }

  val legal_arguments_Constraint = ConstrainedClassifier.constraint[TextAnnotation] { x: TextAnnotation =>
    val constraints = for {
      y <- sentences(x) ~> sentencesToRelations ~> relationsToPredicates
      argCandList = (predicates(y) ~> -relationsToPredicates).toList
      argLegalList = legalArguments(y)
      z <- argCandList
    } yield argLegalList._exists { t: String => argumentTypeLearner on z is t } or
      (argumentTypeLearner on z is "candidate")
    constraints.toSeq._forall(a => a)
  }

  val noDuplicate = ConstrainedClassifier.constraint[TextAnnotation] {
    // Predicates have at most one argument of each type i.e. there shouldn't be any two arguments with the same type for each predicate
    val values = Array("A0", "A1", "A2", "A3", "A4", "A5", "AA")
    var a: FirstOrderConstraint = null
    x: TextAnnotation => {
      a = new FirstOrderConstant(true)
      (sentences(x) ~> sentencesToRelations ~> relationsToPredicates).foreach {
        y =>
          {
            val argCandList = (predicates(y) ~> -relationsToPredicates).toList
            for (t1 <- 0 until argCandList.size - 1) {
              for (t2 <- t1 + 1 until argCandList.size) {
                a = a and (((argumentTypeLearner on argCandList.get(t1)) in values) ==> (((argumentTypeLearner on argCandList.get(t1)) isNot (argumentTypeLearner on argCandList.get(t2)))))
              }

            }
          }
      }
      a
    }
  }

  val r_and_c_args = ConstrainedClassifier.constraint[TextAnnotation] {
    x =>
      r_arg_Constraint(x) and c_arg_Constraint(x) and legal_arguments_Constraint(x) and noDuplicate(x)
  }

} // end srlConstraints

