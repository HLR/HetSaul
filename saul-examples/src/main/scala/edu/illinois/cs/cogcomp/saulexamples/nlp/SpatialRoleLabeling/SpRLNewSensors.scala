/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, TextAnnotation, TokenLabelView, TreeView }
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator.{ USE_NER_CONLL, USE_NER_ONTONOTES, USE_SRL_NOM, USE_SRL_VERB, USE_STANFORD_DEP, USE_STANFORD_PARSE }
import edu.illinois.cs.cogcomp.nlp.utilities.CollinsHeadFinder
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

import scala.collection.JavaConverters._
import scala.collection.mutable

/** Created by parisakordjamshidi on 12/25/16.
  */
object SpRLNewSensors {

  def RelToTrMatching(r: Relation, p: Phrase): Boolean = {
    // when not using exact matching it can be more than one phrases for a trajector
    p.getPropertyValues("TRAJECTOR_id").contains(r.getArgumentId(0)) ||
      r.getArgumentId(0) == p.getId
  }

  def RelToLmMatching(r: Relation, p: Phrase): Boolean = {
    p.getPropertyValues("LANDMARK_id").contains(r.getArgumentId(2)) ||
      r.getArgumentId(2) == p.getId
  }

  def RelToSpMatching(r: Relation, p: Phrase): Boolean = {
    p.getPropertyValues("SPATIALINDICATOR_id").contains(r.getArgumentId(1)) ||
      r.getArgumentId(1) == p.getId
  }
}

