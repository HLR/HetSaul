/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator
import edu.illinois.cs.cogcomp.lbjava.infer.{ FirstOrderConstant, FirstOrderConstraint }
import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner
import edu.illinois.cs.cogcomp.saul.classifier.{ ConstrainedClassifier, Learnable }
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers.argumentTypeLearner
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLSensors._
import org.scalatest.{ FlatSpec, Matchers }
import scala.collection.JavaConversions._

class ConstraintsTest extends FlatSpec with Matchers {
  object TestTextAnnotation extends DataModel {
    val predicates = node[Constituent]((x: Constituent) => x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSpan)

    val arguments = node[Constituent]((x: Constituent) => x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSpan)

    val relations = node[Relation]((x: Relation) => "S" + x.getSource.getTextAnnotation.getCorpusId + ":" + x.getSource.getTextAnnotation.getId + ":" + x.getSource.getSpan +
      "D" + x.getTarget.getTextAnnotation.getCorpusId + ":" + x.getTarget.getTextAnnotation.getId + ":" + x.getTarget.getSpan)

    val sentences = node[TextAnnotation]((x: TextAnnotation) => x.getCorpusId + ":" + x.getId)

    val trees = node[Tree[Constituent]]

    val stringTree = node[Tree[String]]

    val tokens = node[Constituent]((x: Constituent) => x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSpan)

    val sentencesToTrees = edge(sentences, trees)
    val sentencesToStringTree = edge(sentences, stringTree)
    val sentencesToTokens = edge(sentences, tokens)
    val sentencesToRelations = edge(sentences, relations)
    val relationsToPredicates = edge(relations, predicates)
    val relationsToArguments = edge(relations, arguments)

    sentencesToRelations.addSensor(textAnnotationToRelation _)
    sentencesToRelations.addSensor(textAnnotationToRelationMatch _)
    relationsToArguments.addSensor(relToArgument _)
    relationsToPredicates.addSensor(relToPredicate _)
    sentencesToStringTree.addSensor(textAnnotationToStringTree _)
    val posTag = property(predicates, "posC") {
      x: Constituent => getPosTag(x)
    }
    val argumentLabelGold = property(relations, "l") {
      r: Relation => r.getRelationName
    }
  }

  import TestTextAnnotation._

  object ArgumentTypeLearner extends Learnable[Relation](relations) {
    def label = argumentLabelGold

    override lazy val classifier = new SparseNetworkLearner()
  }

  object TestConstraints {

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
                      a = a and new FirstOrderConstant((argumentTypeLearner.classifier.getLabeler.discreteValue(t).equals(values(i)))) ==>
                        argCandList.filterNot(x => x.equals(t))._exists {
                          k: Relation => new FirstOrderConstant((argumentTypeLearner.classifier.getLabeler.discreteValue(k)).equals(values(i).substring(2)))
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
                case (t, ind) => {
                  if (ind > 0)
                    for (i <- 0 until values.length)
                      a = a and new FirstOrderConstant((argumentTypeLearner.classifier.getLabeler.discreteValue(t).equals(values(i)))) ==>
                        sortedCandidates.subList(0, ind)._exists {
                          k: Relation => new FirstOrderConstant((argumentTypeLearner.classifier.getLabeler.discreteValue(k)).equals(values(i).substring(2)))
                        }
                }
              }
            }
        }
      }
      a
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
                  a = a and (new FirstOrderConstant(values.contains(argumentTypeLearner.classifier.getLabeler.discreteValue(argCandList.get(t1)))) ==> new FirstOrderConstant(argumentTypeLearner.classifier.getLabeler.discreteValue(argCandList.get(t1)).ne(argumentTypeLearner.classifier.getLabeler.discreteValue(argCandList.get(t2)))))
                }
              }
            }
        }
        a
      }
    }
  }

  val viewsToAdd = Array(ViewNames.LEMMA, ViewNames.POS, ViewNames.SHALLOW_PARSE, ViewNames.PARSE_GOLD, ViewNames.SRL_VERB)
  val ta: TextAnnotation = DummyTextAnnotationGenerator.generateAnnotatedTextAnnotation(viewsToAdd, true, 1)

  import TestConstraints._
  import TestTextAnnotation._
  sentencesToTokens.addSensor(textAnnotationToTokens _)
  sentences.populate(Seq(ta))
  val predicateTrainCandidates = tokens.getTrainingInstances.filter((x: Constituent) => posTag(x).startsWith("IN"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
  predicates.populate(predicateTrainCandidates)
  val XuPalmerCandidateArgsTraining = predicates.getTrainingInstances.flatMap(x => xuPalmerCandidate(x, (sentences(x.getTextAnnotation) ~> sentencesToStringTree).head))
  sentencesToRelations.addSensor(textAnnotationToRelationMatch _)
  relations.populate(XuPalmerCandidateArgsTraining)

  "manually defined has codes" should "avoid duplications in edges and reverse edges" in {
    predicates().size should be((relations() ~> relationsToPredicates).size)
    (predicates() ~> -relationsToPredicates).size should be(relations().size)
    (predicates(predicates().head) ~> -relationsToPredicates).size should be(4)
  }

  "the no duplicate constraint" should "be true" in {
    noDuplicate(ta).evaluate() should be(true)
  }
  "the r-arg constraint" should "be true" in {
    r_arg_Constraint(ta).evaluate() should be(true)
  }

  "the c-arg constraint" should "be true" in {
    c_arg_Constraint(ta).evaluate() should be(true)
  }
}