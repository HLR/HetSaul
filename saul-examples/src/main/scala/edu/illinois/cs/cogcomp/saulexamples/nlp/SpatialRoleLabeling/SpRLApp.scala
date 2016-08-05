/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.util.Logging
import java.io.File

import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

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
  val version = properties.getString(VERSION)

  logger.info("population starts.")

  PopulateSpRLDataModel(getDataPath(), isTrain, version)

  logger.info("Total sentences :" + sentences.count)
  logger.info("Total tokens :" + tokens.count)
  logger.info("Total pairs:" + pairs.count)
  println(sentences.trainingSet.head.t.getText)
  pairs.trainingSet.slice(0, 50).foreach(x => println(x.t))

  //  runClassifier(trajectorClassifier, "trajectors")
  //  runClassifier(landmarkClassifier, "landmarks")
  //  runClassifier(spatialIndicatorClassifier, "spatialIndicators")

  def runClassifier(classifier: Learnable[Constituent], name: String) = {
    classifier.modelDir = modelDir + name + File.separator
    if (isTrain) {
      logger.info("training " + name + "...")
      classifier.learn(10)
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
  var ta = TextAnnotationFactory.createBasicTextAnnotation("", "", "This is a sample sentence.     And, this is another one.")
  ta.sentences().asScala.foreach(s => {
    val sc = s.getSentenceConstituent
    val start = sc.getInclusiveStartCharOffset()
    val end = sc.getInclusiveEndCharOffset()
    println(start)
    println(end)
    println(ta.text.substring(start, end + 1))
  });
}
