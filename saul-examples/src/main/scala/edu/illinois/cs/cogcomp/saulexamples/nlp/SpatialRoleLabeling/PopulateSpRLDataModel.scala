/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.nlp.utilities.CollinsHeadFinder
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SpRL2013Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.SpRL2015Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory
import org.apache.commons.lang.NotImplementedException

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/** Created by taher on 7/28/16.
  */
object PopulateSpRLDataModel extends Logging {
  def apply(rm: ResourceManager = new SpRLConfigurator().getDefaultConfig) = {

    val isTraining = rm.getBoolean(SpRLConfigurator.IS_TRAINING)
    SpRLDataModel.sentences.populate(readSpRLDocuments(), train = isTraining)

    def getDataPath(): String = {
      if (isTraining) rm.getString(SpRLConfigurator.TRAIN_DIR)
      else rm.getString(SpRLConfigurator.TEST_DIR)
    }

    def readSpRLDocuments(): List[Sentence] = {

      val path = getDataPath()
      val version = rm.getString(SpRLConfigurator.VERSION)

      def readSpRL2013(): List[Sentence] = {
        val reader = new SpRLDataReader(path, classOf[SpRL2013Document])
        reader.readData()
        val sentences = ListBuffer[Sentence]()
        reader.documents.asScala.foreach(doc => {
          logger.info("working on " + doc.getFilename + " ...")
          val views = List("sprl-Trajector", "sprl-Landmark", "sprl-SpatialIndicator")
          val ta = TextAnnotationFactory.createTextAnnotation("2013", doc.getFilename, doc.getTEXT().getContent, views: _*)
          SetSpRLLabels(ta, doc.getTAGS.getSPATIALINDICATOR.asScala.toList, "SpatialIndicator")
          SetSpRLLabels(ta, doc.getTAGS.getLANDMARK.asScala.toList, "Landmark")
          SetSpRLLabels(ta, doc.getTAGS.getTRAJECTOR.asScala.toList, "Trajector")
          sentences ++= ta.sentences().asScala
        })
        sentences.toList
      }

      def SetSpRLLabels(ta: TextAnnotation, tokens: List[HasSpan], label: String) = {
        tokens.foreach(t => {
          val start = t.getStart().intValue()
          val end = t.getEnd().intValue()
          if (start >= 0) {
            val startTokenId = ta.getTokenIdFromCharacterOffset(start)
            val headwordId = getHeadwordId(startTokenId)

            // add label if this token is not already labeled.
            val view = ta.getView("sprl-" + label).asInstanceOf[TokenLabelView]
            val c = view.getConstituentAtToken(headwordId)
            if (c == null)
              view.addTokenLabel(headwordId, label, 1.0)
          }
        })
        def getHeadwordId(startTokenId: Int): Int = {
          val constituents = ta.getView(ViewNames.SHALLOW_PARSE).getConstituentsCoveringToken(startTokenId).asScala
          var headwordId = startTokenId
          if (constituents.size > 0) {
            val c = constituents.head
            val tree: TreeView = ta.getView(SpRLDataModel.parseView).asInstanceOf[TreeView]
            val phrase = tree.getParsePhrase(c)
            headwordId = CollinsHeadFinder.getInstance.getHeadWordPosition(phrase)
            //logger.info("headword for '" + c.toString + "' is " + ta.getToken(headwordId))
          }
          else{
            logger.warn("cannot find phrase for '" + ta.getToken(startTokenId) + "'")
          }
          headwordId
        }
      }

      //TODO: generate TextAnnotations from SpRLDocuments
      def readSpRL2015(): List[Sentence] = {
        val reader = new SpRLDataReader(path, classOf[SpRL2015Document])
        reader.readData()
        throw new NotImplementedException
      }

      version match {
        case "2013" => readSpRL2013
        case _ => readSpRL2015
      }
    }
  }
}
