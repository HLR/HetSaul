/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SpRL2013Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.SpRL2015Document
import org.apache.commons.lang.NotImplementedException

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

    def readSpRLDocuments(): List[TextAnnotation] = {
      val path = getDataPath()
      val version = rm.getString(SpRLConfigurator.VERSION)

      //TODO: generate TextAnnotations from SpRLDocuments
      def readSpRL2013(): List[TextAnnotation] = {
        val reader = new SpRLDataReader(path, classOf[SpRL2013Document])
        reader.readData()
        throw new NotImplementedException
      }
      def readSpRL2015(): List[TextAnnotation] = {
        val reader = new SpRLDataReader(path, classOf[SpRL2015Document])
        reader.readData()
        throw new NotImplementedException
      }

      version match {
        case "2013" => readSpRL2013()
        case _ => readSpRL2015()
      }
    }
  }
}
