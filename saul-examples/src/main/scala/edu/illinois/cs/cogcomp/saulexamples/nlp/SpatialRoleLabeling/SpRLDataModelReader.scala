/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.core.datastructures.{ IntPair, ViewNames }
import edu.illinois.cs.cogcomp.nlp.utilities.CollinsHeadFinder
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SpRL2013Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.SpRL2015Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.{ CommonSensors, TextAnnotationFactory }
import org.apache.commons.lang.NotImplementedException

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/** Created by taher on 8/6/16.
  */
object SpRLDataModelReader extends Logging {

  def read(path: String, isTraining: Boolean, version: String): (List[Sentence], List[Relation]) = {

    def readSpRL2013(): (List[Sentence], List[Relation]) = {

      val reader = new SpRLDataReader(path, classOf[SpRL2013Document])
      reader.readData()

      val sentences = ListBuffer[Sentence]()
      val relations = ListBuffer[Relation]()

      reader.documents.asScala.foreach(doc => {

        logger.info("working on " + doc.getFilename + " ...")
        val sentenceOffsetList = getDocumentSentences(doc)

        sentenceOffsetList.foreach(s => {
          //logger.info("sentence: '" + s._1 + "'")
          assert(s._1 == doc.getTEXT.getContent.substring(s._2.getFirst, s._2.getSecond))
          val ta = TextAnnotationFactory.createTextAnnotation(version, doc.getFilename + s._2, s._1)
          sentences ++= ta.sentences.asScala
          relations ++= getRelations(ta, doc, s._2)
        })
      })

      (sentences.toList, relations.toList)
    }

    def getSentenceOffset(s: Sentence): IntPair = {
      val sc = s.getSentenceConstituent
      val start = sc.getInclusiveStartCharOffset()
      val end = sc.getEndCharOffset()
      new IntPair(start, end)
    }

    def getDocumentSentences(doc: SpRL2013Document): List[(String, IntPair)] = {
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
          val ta = TextAnnotationFactory.createBasicTextAnnotation(version, doc.getFilename, doc.getTEXT().getContent)
          ta.sentences().asScala.map(s => (s.getText, getSentenceOffset(s))).toList
        }
      }
    }

    def getRelations(ta: TextAnnotation, doc: SpRL2013Document, offset: IntPair): List[Relation] = {

      val relations = ListBuffer[Relation]()

      // Gold relations
      doc.getTAGS.getRELATION.asScala.foreach(r => {

        val sp = doc.getSpatialIndicatorMap.get(r.getSpatialIndicatorId)
        val tr = doc.getTrajectorHashMap.get(r.getTrajectorId)
        val lm = doc.getLandmarkHashMap.get(r.getLandmarkId)

        addRelation(relations, ta, sp, tr, "tr-sp", offset)
        addRelation(relations, ta, sp, lm, "lm-sp", offset)
      })

      // Candidate relations
      val constituents = ta.getView(ViewNames.TOKENS).getConstituents.asScala
      val candidates = constituents.filter(x => SpRLSensors.isCandidate(x))
      val args = constituents.filter(x => CommonSensors.getPosTag(x).startsWith("NN"))
      for (a <- args; c <- candidates) {
        if (canAddRelation(relations, a, c))
          relations += new Relation("none-none", a, c, 0.1)
      }
      relations.toList
    }

    def canAddRelation(relations: Iterable[Relation], a: Constituent, b: Constituent): Boolean = {
      SpRLDataModel.getUniqueSentenceId(a) == SpRLDataModel.getUniqueSentenceId(b) &&
        !relations.exists(x => x.getSource.getSpan == a.getSpan && x.getTarget.getSpan == b.getSpan)
    }

    def addRelation(relations: ListBuffer[Relation], ta: TextAnnotation, sp: HasSpan, other: HasSpan, relationType: String, offset: IntPair) = {

      if (!tagIsNullOrOutOfSentence(sp, offset) && !tagIsNullOrOutOfSentence(other, offset)) {
        val r = new Relation(relationType, getHeadword(other, ta, offset), getHeadword(sp, ta, offset), 1)
        relations += r
      }
    }

    def tagIsNullOrOutOfSentence(t: HasSpan, offset: IntPair): Boolean = {
      t == null || t.getStart.intValue() < 0 || t.getEnd.intValue() < 0 ||
        !(offset.getFirst <= t.getStart.intValue() && t.getEnd.intValue() <= offset.getSecond)
    }

    def getHeadword(t: HasSpan, ta: TextAnnotation, offset: IntPair): Constituent = {
      ta.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituentAtToken(getHeadwordId(t, ta, offset))
    }

    def getHeadwordId(t: HasSpan, ta: TextAnnotation, offset: IntPair): Int = {

      if (tagIsNullOrOutOfSentence(t, offset))
        return -1

      val start = t.getStart().intValue() - offset.getFirst
      val startTokenId = ta.getTokenIdFromCharacterOffset(start)
      var headwordId = startTokenId

      val phrases = ta.getView(ViewNames.SHALLOW_PARSE).getConstituentsCoveringToken(startTokenId)

      if (phrases.size > 0) {
        val phrase = phrases.get(0)
        val tree: TreeView = ta.getView(SpRLDataModel.parseView).asInstanceOf[TreeView]
        val parsePhrase = tree.getParsePhrase(phrase)
        headwordId = CollinsHeadFinder.getInstance.getHeadWordPosition(parsePhrase)
        //logger.info("headword for '" + c.toString + "' is " + ta.getToken(headwordId))
      } else {
        logger.warn("cannot find phrase for '" + ta.getToken(startTokenId) + "'")
      }
      headwordId
    }

    //TODO: generate TextAnnotations from SpRLDocuments
    def readSpRL2015(): (List[Sentence], List[Relation]) = {
      val reader = new SpRLDataReader(path, classOf[SpRL2015Document])
      reader.readData()
      throw new NotImplementedException
    }

    version match {
      case "2013" | "2012" => readSpRL2013
      case _ => readSpRL2015
    }
  }
}
