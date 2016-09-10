/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp

import java.util.Properties

import edu.illinois.cs.cogcomp.annotation.AnnotatorService
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ TextAnnotation, TokenLabelView }
import edu.illinois.cs.cogcomp.core.utilities.configuration.{ Configurator, ResourceManager }
import edu.illinois.cs.cogcomp.curator.CuratorConfigurator
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory

/** Created by taher on 7/30/16.
  */
object TextAnnotationFactory {

  val settings = new Properties()
  settings.setProperty(PipelineConfigurator.USE_POS.key, Configurator.TRUE)
  settings.setProperty(PipelineConfigurator.USE_NER_CONLL.key, Configurator.FALSE)
  settings.setProperty(PipelineConfigurator.USE_NER_ONTONOTES.key, Configurator.FALSE)
  settings.setProperty(PipelineConfigurator.USE_SRL_VERB.key, Configurator.FALSE)
  settings.setProperty(PipelineConfigurator.USE_SRL_NOM.key, Configurator.FALSE)
  settings.setProperty(PipelineConfigurator.USE_STANFORD_DEP.key, Configurator.TRUE)
  settings.setProperty(PipelineConfigurator.USE_SHALLOW_PARSE.key, Configurator.TRUE)
  settings.setProperty(PipelineConfigurator.USE_STANFORD_PARSE.key, Configurator.TRUE)
  settings.setProperty(PipelineConfigurator.STFRD_MAX_SENTENCE_LENGTH.key, "10000")
  settings.setProperty(PipelineConfigurator.STFRD_TIME_PER_SENTENCE.key, "100000")

  var annotatorService: AnnotatorService = null

  def createTextAnnotation(corpusId: String, textId: String, text: String, views: String*): TextAnnotation = {
    if (annotatorService == null)
      applySettings()
    val ta = annotatorService.createAnnotatedTextAnnotation(corpusId, textId, text)
    views.foreach(v => ta.addView(v, new TokenLabelView(v, ta)))
    ta
  }

  def applySettings() = {
    val config = new CuratorConfigurator().getConfig(new ResourceManager(settings))
    annotatorService = IllinoisPipelineFactory.buildPipeline(config)
  }

  def createBasicTextAnnotation(corpusId: String, textId: String, text: String): TextAnnotation = {
    if (annotatorService == null)
      applySettings()
    val ta = annotatorService.createBasicTextAnnotation(corpusId, textId, text)
    ta
  }
}
