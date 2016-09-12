/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.io._

import ch.qos.logback.classic.Level
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

import scala.collection.immutable.HashSet
import scala.collection.JavaConverters._
import scala.reflect.io.File

/** Created by Parisa on 7/29/16.
  */
object SpRLApp extends App with Logging {

  import SpRLConfigurator._

  val properties: ResourceManager = {
    logger.info("Loading default configuration parameters")
    new SpRLConfigurator().getDefaultConfig
  }
  val modelDir = properties.getString(MODELS_DIR) +
    File.separator + properties.getString(SpRL_MODEL_DIR) + File.separator
  val isTrain = properties.getBoolean(IS_TRAINING)
  val version = properties.getString(VERSION)
  var modelName = properties.getString(MODEL_NAME)

  logger.info("population starts.")

  modelName match {
    case "Relation" =>
      val name = "Relation"
      val lexPath = modelDir + version + File.separator + name + File.separator + "lexicon.lex"
      val lex = if (isTrain) null else loadRobertsLexicon(lexPath)
      PopulateSpRLDataModel(getDataPath(), isTrain, version, modelName, lex)
      if (isTrain) {
        saveRobertsLexicon(lexPath)
      }
      runClassifier(SpRLClassifiers.relationClassifier, name)
  }

  def runClassifier[T <: AnyRef](classifier: Learnable[T], name: String) = {
    classifier.modelDir = modelDir + version + File.separator + name + File.separator
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

  def loadRobertsLexicon(filePath: String): HashSet[String] = {
    if (File(filePath).exists) {
      val stream = new ObjectInputStream(new FileInputStream(filePath))
      val lex = stream.readObject().asInstanceOf[HashSet[String]]
      stream.close()
      return lex
    }
    return null
  }
  def saveRobertsLexicon(filePath: String): Unit = {
    val stream = new ObjectOutputStream(new FileOutputStream(filePath))
    stream.writeObject(Dictionaries.spLexicon)
    stream.close()
  }

  def getDataPath(): String = {
    if (isTrain) properties.getString(TRAIN_DIR)
    else properties.getString(TEST_DIR)
  }
}
