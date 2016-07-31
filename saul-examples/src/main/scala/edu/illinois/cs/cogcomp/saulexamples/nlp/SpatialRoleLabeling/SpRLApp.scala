/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.io.File

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.lbjava.learn.Learner
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.util.Logging

import scala.collection.JavaConverters._

/** Created by Parisa on 7/29/16.
  */
object SpRLApp extends App with Logging {
  import SpRLConfigurator._
  import SpRLDataModel._
  import SpRLClassifiers._

  val properties: ResourceManager = {
    logger.info("Loading default configuration parameters")
    new SpRLConfigurator().getDefaultConfig
  }
  val modelDir = properties.getString(MODELS_DIR) +
    File.separator + properties.getString(SpRL_MODEL_DIR) + File.separator
  val isTrain = properties.getBoolean(IS_TRAINING)

  logger.info("population starts.")

  PopulateSpRLDataModel()
  val trajectors = tokens().filter(x => isTrajector(x).equals("true"))
  val landmarks = tokens().filter(x => isLandmark(x).equals("true"))
  val spatialIndicators = tokens().filter(x => isSpatialIndicator(x).equals("true"))

  logger.info("all sentences number after population:" + sentences().size)
  logger.info("all tokens number after population:" + tokens().size)
  logger.info("all trajectors number after population:" + trajectors.size)
  logger.info("all landmarks number after population:" + landmarks.size)
  logger.info("all spatialIndicators number after population:" + spatialIndicators.size)

  runClassifier(trajectorClassifier, "trajectors")
  runClassifier(landmarkClassifier, "landmarks")
  runClassifier(spatialIndicatorClassifier, "spatialIndicators")

  def runClassifier(classifier: Learnable[Constituent], name: String): Unit = {
    classifier.modelDir = modelDir + name + File.separator
    if (isTrain) {
      logger.info("training " + name + "...")
      classifier.learn(100)
      classifier.save()
    } else {
      classifier.load()
      logger.info("testing " + name + " ...")
      classifier.test()
    }
    logger.info("done.")
  }

}
