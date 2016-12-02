/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.QuestionTypeClassification

import java.io.File
import java.util.Properties

import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator._
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

import scala.io.Source

object QuestionTypeClassificationSensors {
  val dataFolder = "data/QuestionTypeClassification/"
  lazy val professons = Source.fromFile(new File(dataFolder + "prof.txt")).getLines().toSet
  lazy val mountainKeywords = Source.fromFile(new File(dataFolder + "mount.txt")).getLines().toSet
  lazy val foodKeywords = Source.fromFile(new File(dataFolder + "food.txt")).getLines().toSet

  val settings = new Properties()
  TextAnnotationFactory.disableSettings(settings, USE_SRL_NOM, USE_NER_ONTONOTES)
  val pipeline = TextAnnotationFactory.createPipelineAnnotatorService(settings)

  lazy val (trainInstances, testInstances) = {
    val allLines = Source.fromFile(new File(dataFolder + "train_1000.label.txt")).getLines().toList ++
      Source.fromFile(new File(dataFolder + "train_2000.label.txt")).getLines().toList ++
      Source.fromFile(new File(dataFolder + "train_3000.label.txt")).getLines().toList ++
      Source.fromFile(new File(dataFolder + "train_4000.label.txt")).getLines().toList ++
      Source.fromFile(new File(dataFolder + "train_5500.label.txt")).getLines().toList
    val allInstances = allLines.map { line =>
      val splitted = line.split("\t")
      val splittedLabel = splitted(0).split(":")
      QuestionTypeInstance(splitted(1), Some(splitted(0)), Some(splittedLabel(0)), Some(splittedLabel(1)), None)
    }

    allInstances.splitAt((allInstances.size * 0.7).toInt)
  }

}
