/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.io.{ File, FileReader, PrintWriter }
import java.math.BigInteger

import edu.illinois.cs.cogcomp.core.datastructures.IntPair
import edu.illinois.cs.cogcomp.core.utilities.XmlModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.{ RELATION, SpRL2013Document, TRAJECTOR }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2017.{ Scene, Sentence, SpRL2017Document }

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.io.Source

/** Created by Taher on 2016-10-17.
  */
object DocumentConverterApp extends App {
  val specificType = 10
  val spatialValue = 11
  val foR = 14
  val fileSentencePattern = "scene\\(f(\\d+),s(\\d+)\\)\\.".r
  val sentenceWordPattern = "interpretation\\(i(\\d+), word\\(w\\d+,(.+)\\)\\)\\.".r
  val clefPath = "data/SpRL/CLEF/"
  val srcPath = "data/SpRL/2013/IAPR TC-12/"
  val destPath = "data/SpRL/2017/clef/"

  val sentenceToFileMap = getSentenceToFileMap()
  val sentenceToRelationsMap = getSentenceToRelationMap()

  val sprl2012Reader = new SpRLDataReader(srcPath, classOf[SpRL2013Document])
  sprl2012Reader.readData()
  sprl2012Reader.documents.get(0).getTAGS().getTRAJECTOR

  val clefReader = new SpRLDataReader(clefPath, classOf[ClefDocument], ".eng")
  clefReader.readData()

  val gold = sprl2012Reader.documents.get(0)
  val test = sprl2012Reader.documents.get(1)
  val train = sprl2012Reader.documents.get(2)

  val trainSentCount = alginDocs(train, "train", 0)
  val goldSentCount = alginDocs(gold, "gold", trainSentCount)

  private def alginDocs(sprl2012Doc: SpRL2013Document, dataPartition: String, sentenceNumberOffset: Int): Int = {
    val landmarks = sprl2012Doc.getTAGS.getLANDMARK.asScala
    val spatialIndicators = sprl2012Doc.getTAGS.getSPATIALINDICATOR.asScala
    val trajectors = sprl2012Doc.getTAGS.getTRAJECTOR.asScala
    val relations = sprl2012Doc.getTAGS.getRELATION.asScala.toList

    val sprl2017Doc = new SpRL2017Document()
    val sentenceOffsetPairs = getSentenceOffsetPairs(sprl2012Doc)
    val doclist = mutable.HashMap[String, Int]()
    sentenceOffsetPairs.zipWithIndex.foreach {
      case ((sentence, sentenceOffset), i) => {
        val sentenceNumber = i + sentenceNumberOffset + 1
        val trimmedSentence = sentence.trim
        val (s, f) = sentenceToFileMap(sentenceNumber)
        val fileNumber = sentenceNumber match {
          //file number correction
          case x if (x > 735) => f + 2
          case x if (x > 721) => f + 1
          case x if (x > 115 && x < 200) => f + 1
          case x => f
        }
        if (!sentencesAreEqual(s, trimmedSentence)) {
          //println(s"${dataPartition}_s$sentenceNumber . corresponding sentences are not equal :[ $trimmedSentence ] != [ $s ]")
        }
        if (fileNumber >= clefReader.documents.size()) {
          //println(s"${dataPartition}_s + $sentenceNumber . file index out of bound( $sentenceNumber > ${clefReader.documents.size() - 1} })")
        } else {
          val scene = getScene(sprl2017Doc, clefReader.documents.get(fileNumber))
          addSentenceToScene(sprl2012Doc, relations, scene, sentenceOffset, trimmedSentence, sentenceNumber)
          doclist.put(scene.getDocNo(), 1)
        }
      }
    }
    val out = new PrintWriter(s"$dataPartition.txt")
    doclist.toList.sortBy(x => x._1).foreach(d => out.println(d._1))
    out.close()
    val destDir = new File(destPath + dataPartition)
    if (!destDir.exists())
      destDir.mkdirs()
    XmlModel.write(sprl2017Doc, destPath + s"$dataPartition/sprl2017_$dataPartition.xml")
    sentenceOffsetPairs.size
  }

  private def getSentenceToRelationMap(): Map[Int, List[List[String]]] = {
    val lines = Source.fromFile(clefPath + "CLEF_tigist_with modalities_ModifiedBackg.csv").getLines()
      .drop(1)
      .filter(x => !x.trim.startsWith("**") && x.nonEmpty).map(_.split(";").map(_.trim).toList).toList
    lines.zipWithIndex.foldLeft(List[(Int, List[String])]())((list, element) => {
      val s = element._1 match {
        case (head :: tail) => head.trim
        case _ => ""
      }
      (list.isEmpty, s) match {
        case (true, _) => list :+ (1, element._1)
        case (false, "") => list :+ (list.last._1, element._1)
        case _ => list :+ (list.last._1 + 1, element._1)
      }
    }).groupBy(_._1).map { case (k, v) => (k, v.map(_._2)) }
  }

  private def addSentenceToScene(sprl2012Doc: SpRL2013Document, relations: List[RELATION], scene: Scene, sentenceOffset: IntPair, text: String, id: Int): Boolean = {
    val rels = relations.filter(r => containsRelation(sprl2012Doc, r, sentenceOffset))
    val relationFeatures = sentenceToRelationsMap(id).filter(x => x.size > 7 && x(7).trim != "_")
    //    if (relationFeatures.size != rels.size)
    //      println(s"cannot find relation features for sentence s$id : ${relationFeatures.size} != ${rels.size}")

    rels.zipWithIndex.foreach {
      case (r, i) =>
        r.setSpecificType(relationFeatures(i)(specificType))
        r.setFoR(relationFeatures(i)(foR))
        r.setRCC8Value(relationFeatures(i)(spatialValue))
    }
    val s = new Sentence
    s.setId(s"s$id");
    s.setStart(sentenceOffset.getFirst)
    s.setEnd(sentenceOffset.getSecond)
    s.setText(text)
    s.setRelations(rels.asJava)
    s.setLandmarks(rels.map(r => sprl2012Doc.getLandmarkHashMap.get(r.getLandmarkId)).distinct.asJava)
    s.setTrajectors(rels.map(r => sprl2012Doc.getTrajectorHashMap.get(r.getTrajectorId)).distinct.asJava)
    s.setSpatialindicators(rels.map(r => sprl2012Doc.getSpatialIndicatorMap.get(r.getSpatialIndicatorId)).distinct.asJava)

    s.getTrajectors.asScala.foreach(x => adjustOffsets(sentenceOffset, x))
    s.getLandmarks.asScala.foreach(x => adjustOffsets(sentenceOffset, x))
    s.getSpatialindicators.asScala.foreach(x => adjustOffsets(sentenceOffset, x))

    scene.getSentences.add(s)
  }

  private def sentencesAreEqual(s1: String, s2: String): Boolean = {
    if (s1.size != s2.size)
      return false
    (s1.toLowerCase zip s2.toLowerCase)
      .forall(x => x._1 == x._2 || x._1 == '0' || x._2 == '0' || x._1 == '\'' || x._2 == '\'')
  }

  private def getSentenceToFileMap(): Map[Int, (String, Int)] = {
    val sentenceWords = Source.fromFile(clefPath + "spatial_ext.pl")
      .getLines().filter(l => sentenceWordPattern.findFirstMatchIn(l).isDefined)
      .map(l => {
        val m = sentenceWordPattern.findFirstMatchIn(l).head
        (m.group(1).toInt, m.group(2).replace("_", "-"))
      }).filter(!_._2.trim.equalsIgnoreCase("undef0")).toList.groupBy(_._1)
    Source.fromFile(clefPath + "scene.pl").getLines().map(l => {
      val pair = fileSentencePattern.findFirstMatchIn(l).head
      val fileIndex = pair.group(1).toInt
      val sentenceNumber = pair.group(2).toInt
      (sentenceNumber, (sentenceWords(sentenceNumber).map(_._2).mkString(" "), fileIndex))
    }).toMap
  }

  private def adjustOffsets(sentenceOffset: IntPair, x: SpRLAnnotation): Unit = {
    if (x.getStart > -1)
      x.setStart(x.getStart - sentenceOffset.getFirst)
    if (x.getEnd > -1)
      x.setEnd(x.getEnd - sentenceOffset.getFirst)
  }

  private def containsRelation(sprl2012Doc: SpRL2013Document, r: RELATION, offsets: IntPair): Boolean = {
    val sp = sprl2012Doc.getSpatialIndicatorMap.get(r.getSpatialIndicatorId)
    val lm = sprl2012Doc.getLandmarkHashMap.get(r.getLandmarkId)
    val tr = sprl2012Doc.getTrajectorHashMap.get(r.getTrajectorId)

    containsAnnotation(sp, offsets) || containsAnnotation(lm, offsets) || containsAnnotation(tr, offsets)
  }

  private def containsAnnotation(a: SpRLAnnotation, offsets: IntPair): Boolean = {
    offsets.getFirst <= a.getStart && a.getEnd <= offsets.getSecond
  }

  private def getScene(sprl2017Doc: SpRL2017Document, doc: ClefDocument): Scene = {
    sprl2017Doc.getScenes.asScala.find(x => x.getDocNo == doc.getDocNo) match {
      case None =>
        val s = new Scene()
        s.setDocNo(doc.getDocNo)
        s.setImage(doc.getImage)
        sprl2017Doc.getScenes.add(s)
        s

      case x => x.head
    }
  }

  private def getSentenceOffsetPairs(sprl2012Doc: SpRL2013Document): List[(String, IntPair)] = {
    val sentences = sprl2012Doc.getTEXT.getContent.split("(?<=.\\n\\n)")
    var start = 0
    sentences.map(s => {
      val pair = (s, new IntPair(start, start + s.length))
      start = start + s.length
      pair
    }).toList
  }

}
