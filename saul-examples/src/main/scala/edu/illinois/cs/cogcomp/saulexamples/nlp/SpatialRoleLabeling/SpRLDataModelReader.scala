/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.annotation.AnnotatorService
import edu.illinois.cs.cogcomp.core.datastructures.IntPair
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator._
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SpRL2013Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/** Created by taher on 8/6/16.
  */
object SpRLDataModelReader extends Logging {

  def read(path: String, version: String): List[SpRLSentence] = {

    val settings = new Properties()
    TextAnnotationFactory.disableSettings(settings, USE_SRL_NOM, USE_NER_ONTONOTES)
    val as = TextAnnotationFactory.createPipelineAnnotatorService(settings)
    val reader = new SpRLDataReader(path, classOf[SpRL2013Document])
    reader.readData()

    reader.documents.asScala.flatMap { doc =>
      logger.info("working on " + doc.getFilename + " ...")
      val sentenceOffsetList = getDocumentSentences(as, doc, version)

      sentenceOffsetList.map {
        case (sentence, offset) =>
          assert(sentence == doc.getTEXT.getContent.substring(offset.getFirst, offset.getSecond))
          val ta = TextAnnotationFactory.createTextAnnotation(as, version, doc.getFilename + offset, sentence)
          assert(ta.sentences.size() == 1)
          new SpRLSentence(offset, ta.sentences.get(0), getRelations(doc, offset).asJava)
      }
    }.toList
  }

  private def getSentenceOffset(s: Sentence): IntPair = {
    val sc = s.getSentenceConstituent
    val start = sc.getInclusiveStartCharOffset()
    val end = sc.getEndCharOffset()
    new IntPair(start, end)
  }

  private def getDocumentSentences(as: AnnotatorService, doc: SpRL2013Document, version: String): List[(String, IntPair)] = {
    version match {
      case "2012" => {
        // 2012 sentences end with '.\n\n' so
        // split by '.\n\n' and keep the delimiter with the previous sentence
        val sentences = doc.getTEXT.getContent.split("(?<=.\\n\\n)")
        var start = 0
        sentences.map(s => {
          val pair = (s, new IntPair(start, start + s.length))
          start = start + s.length
          pair
        }).toList
      }
      case "2013" => {
        // 2013 sentences are more complex to split compared to 2012 version, so we let the TextAnnotationFactory
        // do it
        val ta = TextAnnotationFactory.createBasicTextAnnotation(as, version, doc.getFilename, doc.getTEXT().getContent)
        ta.sentences().asScala.map(s => (s.getText, getSentenceOffset(s))).toList
      }
    }
  }

  private def getRelations(doc: SpRL2013Document, offset: IntPair): List[SpRLRelation] = {

    val relations = ListBuffer[SpRLRelation]()
    val goldRelations = doc.getTAGS.getRELATION.asScala
      .filter(x => !tagIsNullOrOutOfSentence(doc.getSpatialIndicatorMap.get(x.getSpatialIndicatorId), offset)).toList
    for (r <- goldRelations) {
      val tr = setNullIfEmpty(doc.getTrajectorHashMap.get(r.getTrajectorId))
      val lm = setNullIfEmpty(doc.getLandmarkHashMap.get(r.getLandmarkId))
      val sp = setNullIfEmpty(doc.getSpatialIndicatorMap.get(r.getSpatialIndicatorId))
      relations += new SpRLRelation(r.getId, sp, tr, lm)
    }
    relations.toList
  }

  private def setNullIfEmpty(t: SpRLAnnotation): SpRLAnnotation = {
    t.getStart.intValue() match {
      case -1 => null
      case _ => t
    }
  }

  private def tagIsNullOrOutOfSentence(t: SpRLAnnotation, offset: IntPair): Boolean = {
    t == null || t.getStart.intValue() < 0 || t.getEnd.intValue() < 0 ||
      !(offset.getFirst <= t.getStart.intValue() && t.getEnd.intValue() <= offset.getSecond)
  }
}
