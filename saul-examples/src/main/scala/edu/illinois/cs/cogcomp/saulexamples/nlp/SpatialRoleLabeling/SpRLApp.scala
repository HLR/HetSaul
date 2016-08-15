/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.io.File

import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

/** Created by Parisa on 7/29/16.
  */
object SpRLApp extends App with Logging {

  import SpRLClassifiers._
  import SpRLConfigurator._
  import SpRLDataModel._

  val properties: ResourceManager = {
    logger.info("Loading default configuration parameters")
    new SpRLConfigurator().getDefaultConfig
  }
  val modelDir = properties.getString(MODELS_DIR) +
    File.separator + properties.getString(SpRL_MODEL_DIR) + File.separator
  val isTrain = properties.getBoolean(IS_TRAINING)
  val version = properties.getString(VERSION)

  logger.info("population starts.")

  PopulateSpRLDataModel(getDataPath(), isTrain, version, "")

  logger.info("Total sentences :" + sentences.count)
  logger.info("Total tokens :" + tokens.count)
  logger.info("Total spatial indicators :" + pairs().count(x => SpRLDataModel.isSpatialIndicator(x).equals("true")))
  logger.info("Total trajectors :" + pairs().count(x => SpRLDataModel.isTrajector(x).equals("true")))
  logger.info("Total landmarks :" + pairs().count(x => SpRLDataModel.isLandmark(x).equals("true")))

  runClassifier(spatialIndicatorClassifier, "spatialIndicators")

  //  runClassifier(pairTypeClassifier, "relations")
  //  runClassifier(trajectorClassifier, "trajectors")
  //  runClassifier(landmarkClassifier, "landmarks")

  def runClassifier[T <: AnyRef](classifier: Learnable[T], name: String) = {
    classifier.modelDir = modelDir + version + File.separator + name + File.separator
    if (name == "relations") {
      spatialIndicatorClassifier.load()
    }
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

  def getDataPath(): String = {
    if (isTrain) properties.getString(TRAIN_DIR)
    else properties.getString(TEST_DIR)
  }
}

object SpRLTestApp extends App {
  val text = "Cars parked in front of the house ."
  var ta = TextAnnotationFactory.createTextAnnotation("", "", text)
  var f = SpRLSensors.getDependencyPath(ta, 0, 2)
  print(f)

}


