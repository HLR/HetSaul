/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.QuestionTypeClassification

import java.io.File
import java.util.Properties

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator._
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

import scala.io.Source

object QuestionTypeClassificationSensors {
  val dataFolder = "../data/QuestionTypeClassification/"
  lazy val professons = Source.fromFile(new File(dataFolder + "prof.txt")).getLines().toSet
  lazy val mountainKeywords = Source.fromFile(new File(dataFolder + "mount.txt")).getLines().toSet
  lazy val foodKeywords = Source.fromFile(new File(dataFolder + "food.txt")).getLines().toSet

  lazy val pipeline = {
    val settings = new Properties()
    TextAnnotationFactory.disableSettings(settings, USE_SRL_NOM)
    TextAnnotationFactory.createPipelineAnnotatorService(settings)
  }

  val useTRECAsTest = false
  lazy val (trainInstances, testInstances) = {
    if (useTRECAsTest) {
      val train = getInstances("train_1000.label.txt") ++ getInstances("train_2000.label.txt") ++
        getInstances("train_3000.label.txt") ++ getInstances("train_4000.label.txt") ++ getInstances("train_5500.label.txt")
      val test = getInstances("TREC_10.label.txt")
      (train, test)
    } else {
      val train = getInstances("train_1000.label.txt") ++ getInstances("train_2000.label.txt") ++
        getInstances("train_3000.label.txt") ++ getInstances("train_4000.label.txt")
      val test = getInstances("train_5500.label.txt")
      (train, test)
    }
  }

  def getInstances(fileName: String): List[QuestionTypeInstance] = {
    val allLines = Source.fromFile(new File(dataFolder + fileName), "ISO-8859-1").getLines().toList
    allLines.map { line =>
      val split = line.split(" ")
      val splitLabel = split(0).split(":")
      val question = line.drop(split(0).length).trim
      val ta = pipeline.createBasicTextAnnotation("", "", question)
      pipeline.addView(ta, ViewNames.LEMMA)
      pipeline.addView(ta, ViewNames.POS)
      pipeline.addView(ta, ViewNames.SHALLOW_PARSE)
      pipeline.addView(ta, ViewNames.NER_CONLL)
      QuestionTypeInstance(question, Some(splitLabel(0)), Some(split(0)), Some(ta))
    }
  }

  def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  lazy val wordGroupLists = {
    val files = getListOfFiles(dataFolder + "publish/lists")
    assert(files.nonEmpty, "list of files not found")
    files.map { f: File => f.getName -> Source.fromFile(f).getLines().toSet.map { line: String => line.toLowerCase.trim } }
  }
}
