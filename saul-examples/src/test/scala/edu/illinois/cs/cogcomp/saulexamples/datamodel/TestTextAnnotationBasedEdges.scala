/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLSensors._
import org.scalatest.{ FlatSpec, Matchers }

class TestTextAnnotationBasedEdges extends FlatSpec with Matchers {

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
  }

  val viewsToAdd = Array(ViewNames.LEMMA, ViewNames.POS, ViewNames.SHALLOW_PARSE, ViewNames.PARSE_GOLD, ViewNames.SRL_VERB)
  val ta: TextAnnotation = DummyTextAnnotationGenerator.generateAnnotatedTextAnnotation(viewsToAdd, false, 1)
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
}