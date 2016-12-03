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
import edu.illinois.cs.cogcomp.edison.features.factory._
import edu.illinois.cs.cogcomp.nlp.corpusreaders.AbstractSRLAnnotationReader
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.property.PairwiseConjunction
import edu.illinois.cs.cogcomp.saulexamples.data.SRLFrameManager
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLConstrainedClassifiers._

import scala.collection.JavaConversions._

class SRLMultiGraphDataModel(parseViewName: String = null, frameManager: SRLFrameManager = null) extends DataModel {

  val predicates = node[Constituent]((x: Constituent) => x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSpan)

  val arguments = node[Constituent]((x: Constituent) => x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSpan)

  val relations = node[Relation]((x: Relation) => "S" + x.getSource.getTextAnnotation.getCorpusId + ":" + x.getSource.getTextAnnotation.getId + ":" + x.getSource.getSpan +
    "D" + x.getTarget.getTextAnnotation.getCorpusId + ":" + x.getTarget.getTextAnnotation.getId + ":" + x.getTarget.getSpan)

  //val onTheFlyRelationNode= join(predicates, arguments)

  val sentences = node[TextAnnotation]((x: TextAnnotation) => x.getCorpusId + ":" + x.getId)

  val trees = node[Tree[Constituent]]

  val stringTree = node[Tree[String]]

  val tokens = node[Constituent]((x: Constituent) => x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSpan)

  val pairs = join(relations, relations)(_.getSource.getSentenceId == _.getSource.getSentenceId)
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

  /** This can be applied to both predicates and arguments */
  val address = property(predicates) {
    x: Constituent => x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSpan
  }

  // Classification labels
  val isPredicateGold = property(predicates) {
    x: Constituent => x.getLabel.equals("Predicate")
  }
  val predicateSenseGold = property(predicates) {
    x: Constituent => x.getAttribute(AbstractSRLAnnotationReader.SenseIdentifier)
  }

  val isArgumentXuGold = property(relations) {
    x: Relation => !x.getRelationName.equals("candidate")
  }
  val argumentLabelGold = property(relations) {
    r: Relation => r.getRelationName
  }

  // Features properties
  val posTag = property(predicates) {
    x: Constituent => getPOS(x)
  }

  val subcategorization = property(predicates) {
    x: Constituent => fexFeatureExtractor(x, new SubcategorizationFrame(parseViewName))
  }

  val phraseType = property(predicates) {
    x: Constituent => fexFeatureExtractor(x, new ParsePhraseType(parseViewName))
  }

  val headword = property(predicates) {
    x: Constituent => fexFeatureExtractor(x, new ParseHeadWordPOS(parseViewName))
  }

  val syntacticFrame = property(predicates) {
    x: Constituent => fexFeatureExtractor(x, new SyntacticFrame(parseViewName))
  }
  val path = property(predicates) {
    x: Constituent => fexFeatureExtractor(x, new ParsePath(parseViewName))
  }

  //  val subcategorizationRelation = property(relations, "subcat") {
  //    x: Relation => fexFeatureExtractor(x.getTarget, new SubcategorizationFrame(parseViewName))
  //  }

  val phraseTypeRelation = property(relations) {
    x: Relation => fexFeatureExtractor(x.getTarget, new ParsePhraseType(parseViewName))
  }

  val headwordRelation = property(relations) {
    x: Relation => fexFeatureExtractor(x.getTarget, new ParseHeadWordPOS(parseViewName))
  }

  val syntacticFrameRelation = property(relations) {
    x: Relation => fexFeatureExtractor(x.getTarget, new SyntacticFrame(parseViewName))
  }

  val pathRelation = property(relations) {
    x: Relation => fexFeatureExtractor(x.getTarget, new ParsePath(parseViewName))
  }

  val predPosTag = property(relations) {
    x: Relation => getPOS(x.getSource)
  }

  val predLemmaR = property(relations) {
    x: Relation => getLemma(x.getSource)
  }

  val predLemmaP = property(predicates) {
    x: Constituent => getLemma(x)
  }

  val linearPosition = property(relations) {
    x: Relation => fexFeatureExtractor(x.getTarget, new LinearPosition())
  }

  val voice = property(predicates) {
    x: Constituent => fexFeatureExtractor(x, new VerbVoiceIndicator(parseViewName))
  }

  val predWordWindow = property(predicates) {
    x: Constituent => fexContextFeats(x, WordFeatureExtractorFactory.word)
  }

  val predPOSWindow = property(predicates) {
    x: Constituent => fexContextFeats(x, WordFeatureExtractorFactory.pos)
  }

  val argWordWindow = property(relations) {
    rel: Relation => fexContextFeats(rel.getTarget, WordFeatureExtractorFactory.word)
  }

  val argPOSWindow = property(relations) {
    rel: Relation => fexContextFeats(rel.getTarget, WordFeatureExtractorFactory.pos)
  }

  val verbClass = property(predicates) {
    x: Constituent => frameManager.getAllClasses(getLemma(x)).toList
  }

  val constituentLength = property(relations) {
    rel: Relation => rel.getTarget.getEndSpan - rel.getTarget.getStartSpan
  }

  val chunkLength = property(relations) {
    rel: Relation => rel.getTarget.getTextAnnotation.getView(ViewNames.SHALLOW_PARSE).getConstituentsCovering(rel.getTarget).length
  }

  val chunkEmbedding = property(relations) {
    rel: Relation => fexFeatureExtractor(rel.getTarget, new ChunkEmbedding(ViewNames.SHALLOW_PARSE))
  }

  val chunkPathPattern = property(relations) {
    rel: Relation => fexFeatureExtractor(rel.getTarget, new ChunkPathPattern(ViewNames.SHALLOW_PARSE))
  }

  /** Combines clause relative position and clause coverage */
  val clauseFeatures = property(relations) {
    rel: Relation =>
      val clauseViewName = if (parseViewName.equals(ViewNames.PARSE_GOLD)) "CLAUSES_GOLD" else ViewNames.CLAUSES_STANFORD
      fexFeatureExtractor(rel.getTarget, new ClauseFeatureExtractor(parseViewName, clauseViewName))
  }

  val containsNEG = property(relations) {
    rel: Relation => fexFeatureExtractor(rel.getTarget, ChunkPropertyFeatureFactory.isNegated)
  }

  val containsMOD = property(relations) {
    rel: Relation => fexFeatureExtractor(rel.getTarget, ChunkPropertyFeatureFactory.hasModalVerb)
  }

  // Frame properties
  val legalSenses = property(relations) {
    x: Relation => frameManager.getLegalSenses(predLemmaR(x)).toList

  }

  val legalArguments = property(predicates) {
    x: Constituent => frameManager.getLegalArguments(predLemmaP(x)).toList
  }

  // Classifiers as properties
  val isPredicatePrediction = property(predicates) {
    x: Constituent => predicateClassifier(x)
  }

  val isArgumentPrediction = property(relations) {
    x: Relation => argumentXuIdentifierGivenApredicate(x)
  }

  val isArgumentPipePrediction = property(relations) {
    x: Relation =>
      predicateClassifier(x.getSource) match {
        case "false" => "false"
        case _ => argumentXuIdentifierGivenApredicate(x)

      }
  }

  val typeArgumentPrediction = property(relations) {
    x: Relation =>
      argumentTypeLearner(x)
  }

  val typeArgumentPipePrediction = property(relations) {
    x: Relation =>
      val a: String = predicateClassifier(x.getSource) match {
        case "false" => "false"
        case _ => argumentXuIdentifierGivenApredicate(x)
      }
      val b = a match {
        case "false" => "false"
        case _ => argumentTypeLearner(x)
      }
      b
  }
  val typeArgumentPipeGivenGoldPredicate = property(relations) {
    x: Relation =>
      val a: String = argumentXuIdentifierGivenApredicate(x) match {
        case "false" => "candidate"
        case _ => argumentTypeLearner(x)
      }
      a
  }
  val typeArgumentPipeGivenGoldPredicateConstrained = property(relations) {
    x: Relation =>
      val a: String = argumentXuIdentifierGivenApredicate(x) match {
        case "false" => "candidate"
        case _ => argTypeConstraintClassifier(x)
      }
      a
  }
  val propertyConjunction = property(relations) {
    x: Relation =>
      PairwiseConjunction(List(containsMOD, containsNEG), x)
  }
}
