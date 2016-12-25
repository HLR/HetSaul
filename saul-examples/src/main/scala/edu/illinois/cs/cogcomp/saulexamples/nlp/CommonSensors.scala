/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp

import edu.illinois.cs.cogcomp.annotation.AnnotatorService
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Sentence, TextAnnotation }
import edu.illinois.cs.cogcomp.curator.CuratorFactory
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory
import edu.illinois.cs.cogcomp.edison.features.{ FeatureExtractor, FeatureUtilities }
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.data.Document

import scala.collection.JavaConversions._

/** an object containing many popular sensors used in examples */
object CommonSensors extends Logging {

  def textCollection(x: List[Document]) = {
    x.map(documentContent)
  }

  def getSentences(x: TextAnnotation): List[Sentence] = {
    x.sentences().toList
  }

  def textAnnotationSentenceAlignment(ta: TextAnnotation, sentence: Sentence): Boolean = {
    ta.getId == sentence.getSentenceConstituent.getTextAnnotation.getId
  }

  def textAnnotationConstituentAlignment(ta: TextAnnotation, cons: Constituent): Boolean = {
    cons.getTextAnnotation.getId == ta.getId
  }

  def sentenceConstituentAlignment(sentence: Sentence, cons: Constituent): Boolean = {
    cons.getSentenceId == sentence.getSentenceId
  }

  def getConstituents(x: TextAnnotation): List[Constituent] = {
    x.getView(ViewNames.POS).getConstituents.toList
  }

  def textAnnotationToTokens(ta: TextAnnotation): List[Constituent] = {
    ta.getView(ViewNames.TOKENS).getConstituents.toList
  }

  def sentenceToTokens(s: Sentence): List[Constituent] = {
    s.getView(ViewNames.TOKENS).getConstituents.toList
  }

  def getPosTag(x: Constituent): String = {
    WordFeatureExtractorFactory.pos.getFeatures(x).mkString
  }

  def getLemma(x: Constituent): String = {
    WordFeatureExtractorFactory.lemma.getFeatures(x).mkString
  }

  def getFeature(x: Constituent, fex: FeatureExtractor): String = {
    FeatureUtilities.getFeatureSet(fex, x).mkString(",")
  }

  def annotateWithCurator(document: Document): TextAnnotation = {
    val content = documentContent(document)
    annotateRawWithCurator(content, document.getGUID)
  }

  def documentContent(x: Document): String = {
    x.getWords.mkString(" ")
  }

  def annotateRawWithCurator(content: String, id: String): TextAnnotation = {
    val annotatorService = CuratorFactory.buildCuratorClient()
    processDocumentWith(annotatorService, "corpus", id, content)
  }

  /** Annotation services */
  def processDocumentWith(annotatorService: AnnotatorService, cid: String, did: String, text: String, services: String*): TextAnnotation = {
    val ta = annotatorService.createBasicTextAnnotation(cid, did, text)
    logger.debug("populated views " + ta.getAvailableViews)
    ta
  }

  def annotateWithPipeline(content: String, id: String): TextAnnotation = {
    val annotatorService = IllinoisPipelineFactory.buildPipeline()
    processDocumentWith(annotatorService, "corpus", id, content)
  }

}

