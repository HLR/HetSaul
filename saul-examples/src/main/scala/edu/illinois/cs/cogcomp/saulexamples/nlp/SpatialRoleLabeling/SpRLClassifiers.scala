/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation }
import edu.illinois.cs.cogcomp.lbjava.learn.Learner
import edu.illinois.cs.cogcomp.saul.classifier.{ Learnable, SparseNetworkLBP }
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property

/** Created by taher on 7/30/16.
  */
object SpRLClassifiers {

  import SpRLDataModel._
  object spatialIndicatorClassifier extends Learnable[Constituent](tokens) {

    def label: Property[Constituent] = isSpatialIndicator
    override def feature = using(lemma, posTag, headword, subcategorization)
    override lazy val classifier = new SparseNetworkLBP
  }
  object trajectorClassifier extends Learnable[Constituent](tokens) {

    def label: Property[Constituent] = isTrajector
    override def feature = using(lemma, posTag, headword, subcategorization)
    override lazy val classifier = new SparseNetworkLBP
  }
  object landmarkClassifier extends Learnable[Constituent](tokens) {

    def label: Property[Constituent] = isLandmark
    override def feature = using(lemma, posTag, headword, subcategorization)
    override lazy val classifier = new SparseNetworkLBP
  }
}
