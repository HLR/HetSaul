/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SpRL2013Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.SpRL2015Document
import org.apache.commons.lang.NotImplementedException

import scala.collection.JavaConversions._

/** Created by taher on 7/28/16.
  */
object SpRLSensors {
  def textAnnotationToRelation(textAnnotation: TextAnnotation): List[Relation] = {
    //TODO: how to add relations?
    throw new NotImplementedException
  }
  def relationToToken(relation: Relation): List[Constituent] = {
    //TODO: how to add relations?
    throw new NotImplementedException
  }
  //TODO: should be moved to CommonSensores
  def getPOS(x: Constituent): String = {
    WordFeatureExtractorFactory.pos.getFeatures(x).mkString
  }

}
