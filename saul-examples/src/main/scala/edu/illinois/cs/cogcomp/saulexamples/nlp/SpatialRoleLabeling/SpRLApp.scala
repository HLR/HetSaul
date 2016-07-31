/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.io.File

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.util.Logging

import scala.collection.JavaConverters._

/** Created by Parisa on 7/29/16.
  */
object SpRLApp extends App {
  import SpRLConfigurator._
  import SpRLDataModel._
  import SpRLClassifiers._

  val properties: ResourceManager = {
    logger.info("Loading default configuration parameters")
    new SpRLConfigurator().getDefaultConfig
  }
  val modelDir = properties.getString(MODELS_DIR) +
    File.separator + properties.getString(SpRL_MODEL_DIR) + File.separator
  val srlPredictionsFile = properties.getString(SpRLConfigurator.SpRL_OUTPUT_FILE)
  val modelJars = properties.getString(SpRLConfigurator.SpRL_JAR_MODEL_PATH)

  val startTime = System.currentTimeMillis()
  logger.info("population starts.")

  PopulateSpRLDataModel()
  logger.info("all sentences number after population:" + sentences().size)
  logger.info("all tokens number after population:" + tokens().size)

  trajectorClassifier.modelDir = modelDir + "trajectors"
  trajectorClassifier.learn(10)
  trajectorClassifier.test()
  trajectorClassifier.save()
}
