/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp

import java.util.Properties

import edu.illinois.cs.cogcomp.annotation.AnnotatorService
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ TextAnnotation, TokenLabelView }
import edu.illinois.cs.cogcomp.core.utilities.configuration.{ Configurator, Property, ResourceManager }
import edu.illinois.cs.cogcomp.curator.{ CuratorConfigurator, CuratorFactory }
import edu.illinois.cs.cogcomp.curator.CuratorConfigurator._
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator._
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory

/** Created by taher on 7/30/16.
  */
object TextAnnotationFactory {

  def disableSettings(settings: Properties, props: Property*) = {
    props.foreach(p => settings.setProperty(p.key, Configurator.FALSE))
  }

  def enableSettings(settings: Properties, props: Property*) = {
    props.foreach(p => settings.setProperty(p.key, Configurator.TRUE))
  }

  def createTextAnnotation(as: AnnotatorService, corpusId: String, textId: String, text: String, views: String*): TextAnnotation = {
    val ta = as.createAnnotatedTextAnnotation(corpusId, textId, text)
    views.foreach(v => ta.addView(v, new TokenLabelView(v, ta)))
    ta
  }

  def createBasicTextAnnotation(as: AnnotatorService, corpusId: String, textId: String, text: String): TextAnnotation =
    as.createBasicTextAnnotation(corpusId, textId, text)

  def createPipelineAnnotatorService(settings: Properties): AnnotatorService = {
    IllinoisPipelineFactory.buildPipeline(
      new CuratorConfigurator().getConfig(new ResourceManager(settings))
    )
  }

  def createCuratorAnnotatorService(settings: Properties): AnnotatorService = {
    CuratorFactory.buildCuratorClient(
      new CuratorConfigurator().getConfig(new ResourceManager(settings))
    )
  }

}
