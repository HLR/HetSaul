/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SpRL2013Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.SpRL2015Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory
import org.apache.commons.lang.NotImplementedException

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/** Created by taher on 7/28/16.
  */
object PopulateSpRLDataModel {
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
          val ta = TextAnnotationFactory.createTextAnnotation("", "", doc.getTEXT().getContent)
          SetSpRLLabels(ta, doc.getTAGS.getTRAJECTOR.asScala.toList, "Trajector")
          SetSpRLLabels(ta, doc.getTAGS.getSPATIALINDICATOR.asScala.toList, "SpatialIndicator")
          SetSpRLLabels(ta, doc.getTAGS.getLANDMARK.asScala.toList, "Landmark")
          ta.sentences().asScala.foreach(s => sentences += s)
        })
        sentences.toList
      }

      def SetSpRLLabels(ta: TextAnnotation, tokens: List[HasSpan], label: String) = {
        tokens.foreach(t => {
          val start = t.getStart().intValue()
          val end = t.getEnd().intValue()
          if (start >= 0) {
            val startTokenId = ta.getTokenIdFromCharacterOffset(start)
            val view = ta.getView("sprl-" + label).asInstanceOf[TokenLabelView]
            val c = view.getConstituentAtToken(startTokenId)
            if (c == null)
              view.addTokenLabel(startTokenId, label, 1.0)
          }
        })
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
