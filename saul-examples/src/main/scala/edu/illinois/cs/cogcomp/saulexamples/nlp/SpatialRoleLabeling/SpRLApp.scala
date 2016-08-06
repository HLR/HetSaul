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

import scala.collection.JavaConverters._

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

  PopulateSpRLDataModel(getDataPath(), isTrain, version)

  logger.info("Total sentences :" + sentences.count)
  logger.info("Total tokens :" + tokens.count)
  logger.info("Total spatial indicators :" + tokens().count(x => SpRLDataModel.isSpatialIndicator(x).equals("true")))
  logger.info("Total trajectors :" + tokens().count(x => SpRLDataModel.isTrajector(x).equals("true")))
  logger.info("Total landmarks :" + tokens().count(x => SpRLDataModel.isLandmark(x).equals("true")))
  logger.info("Total lm-sp relations:" + relations().count(x => x.getRelationName.equals("lm-sp")))
  logger.info("Total tr-sp srelations:" + relations().count(x => x.getRelationName.equals("tr-sp")))

  runClassifier(relationTypeClassifier, "relations")
  runClassifier(trajectorClassifier, "trajectors")
  runClassifier(landmarkClassifier, "landmarks")
  runClassifier(spatialIndicatorClassifier, "spatialIndicators")

  def runClassifier[T <: AnyRef](classifier: Learnable[T], name: String) = {
    classifier.modelDir = modelDir + version + File.separator + name + File.separator
    if (isTrain) {
      logger.info("training " + name + "...")
      classifier.learn(50)
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
  val text = "About 20 kids in traditional clothing and hats waiting on stairs .\n\na house and a green wall with gate in the background .\n\n"
  var ta = TextAnnotationFactory.createBasicTextAnnotation("", "", text)
  ta.sentences().asScala.foreach(s => {
    val sc = s.getSentenceConstituent
    val start = sc.getInclusiveStartCharOffset()
    val end = sc.getInclusiveEndCharOffset()
    println(start)
    println(end)
    println(ta.text.substring(start, end + 1))
  });
}
