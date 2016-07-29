package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.utilities.configuration.{ Configurator, ResourceManager }
import edu.illinois.cs.cogcomp.curator.CuratorConfigurator
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory

/** Created by Parisa on 7/29/16.
  */
object testReadingTextAnnotation extends App {

  val nonDefaultProps = new Properties()
  nonDefaultProps.setProperty(PipelineConfigurator.USE_POS.key, Configurator.TRUE)
  nonDefaultProps.setProperty(PipelineConfigurator.USE_NER_CONLL.key, Configurator.FALSE)
  nonDefaultProps.setProperty(PipelineConfigurator.USE_NER_ONTONOTES.key, Configurator.FALSE)
  nonDefaultProps.setProperty(PipelineConfigurator.USE_SRL_VERB.key, Configurator.FALSE)
  nonDefaultProps.setProperty(PipelineConfigurator.USE_SRL_NOM.key, Configurator.FALSE)
  nonDefaultProps.setProperty(PipelineConfigurator.USE_STANFORD_DEP.key, Configurator.FALSE)
  nonDefaultProps.setProperty(PipelineConfigurator.USE_SHALLOW_PARSE.key, Configurator.FALSE)
  nonDefaultProps.setProperty(PipelineConfigurator.USE_STANFORD_PARSE.key, Configurator.FALSE)
  val pipeline = IllinoisPipelineFactory.buildPipeline(new CuratorConfigurator().getConfig(new ResourceManager(nonDefaultProps)))
  val ta = pipeline.createBasicTextAnnotation("idc", "idd", "This is my text.")
  pipeline.addView(ta, ViewNames.POS)
  print(ta.getView("POS").toString)
}
