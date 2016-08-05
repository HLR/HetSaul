/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.lang.Boolean
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import org.apache.commons.lang.NotImplementedException

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.nlp.utilities.CollinsHeadFinder
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SpRL2013Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.SpRL2015Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

/** Created by taher on 7/28/16.
  */
object PopulateSpRLDataModel extends Logging {
  def apply(path: String, isTraining: Boolean, version: String) = {

    val views = List("sprl-Trajector", "sprl-Landmark", "sprl-SpatialIndicator")

    def readSpRLDocuments(): (List[Sentence], List[(Constituent, Constituent, String)]) = {

      def readSpRL2013(): (List[Sentence], List[(Constituent, Constituent, String)]) = {

        val reader = new SpRLDataReader(path, classOf[SpRL2013Document])
        reader.readData()

        val sentences = ListBuffer[Sentence]()
        val relationTuples = ListBuffer[(Constituent, Constituent, String)]()

        reader.documents.asScala.foreach(doc => {

          logger.info("working on " + doc.getFilename + " ...")
          val ta = TextAnnotationFactory.createTextAnnotation("2013", doc.getFilename, doc.getTEXT().getContent)

          sentences ++= ta.sentences.asScala

          relationTuples ++= getRelationPairs(ta, doc)
        })

        (sentences.toList, relationTuples.toList)
      }

      def getRelationPairs(ta: TextAnnotation, doc: SpRL2013Document): List[(Constituent, Constituent, String)] = {

        val tuples = ListBuffer[(Constituent, Constituent, String)]()

        doc.getTAGS.getRELATION.asScala.foreach(r => {

          val sp = doc.getSpatialIndicatorMap.get(r.getSpatialIndicatorId)
          val tr = doc.getTrajectorHashMap.get(r.getTrajectorId)
          val lm = doc.getLandmarkHashMap.get(r.getLandmarkId)

          addTuple(tuples, ta, sp, tr, "tr-sp")
          addTuple(tuples, ta, sp, lm, "lm-sp")
        })

        tuples.toList
      }

      def addTuple(tuples: ListBuffer[(Constituent, Constituent, String)], ta: TextAnnotation, sp: HasSpan, other: HasSpan, relationType: String) = {

        if (!tagIsNullOrEmpty(sp) && !tagIsNullOrEmpty(other)) {
          val tuple = (getHeadword(other, ta), getHeadword(sp, ta), relationType)
          tuples += tuple
        }
      }

      def tagIsNullOrEmpty(t: HasSpan): Boolean = {
        t == null || t.getStart.intValue() < 0 || t.getEnd.intValue() < 0
      }

      def getHeadword(t: HasSpan, ta: TextAnnotation): Constituent = {
        ta.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituentAtToken(getHeadwordId(t, ta))
      }

      def getHeadwordId(t: HasSpan, ta: TextAnnotation): Int = {

        if (tagIsNullOrEmpty(t))
          return -1

        val start = t.getStart().intValue()
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
      def readSpRL2015(): (List[Sentence], List[(Constituent, Constituent, String)]) = {
        val reader = new SpRLDataReader(path, classOf[SpRL2015Document])
        reader.readData()
        throw new NotImplementedException
      }

      version match {
        case "2013" => readSpRL2013
        case _ => readSpRL2015
      }
    }

    val (sentences, tuples) = readSpRLDocuments()
    SpRLDataModel.sentences.populate(sentences, train = isTraining)
  }
}
