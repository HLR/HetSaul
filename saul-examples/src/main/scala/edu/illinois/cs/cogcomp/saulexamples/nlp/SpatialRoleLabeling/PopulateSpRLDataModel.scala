/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.lang.Boolean

import edu.illinois.cs.cogcomp.saul.util.Logging

/** Created by taher on 7/28/16.
  */
object PopulateSpRLDataModel extends Logging {
  def apply(path: String, isTraining: Boolean, version: String) = {

    val (sentences, relations) = SpRLDataModelReader.read(path, isTraining, version)
    SpRLDataModel.sentences.populate(sentences, train = isTraining)
    SpRLDataModel.relations.populate(relations, train = isTraining)
  }
}
