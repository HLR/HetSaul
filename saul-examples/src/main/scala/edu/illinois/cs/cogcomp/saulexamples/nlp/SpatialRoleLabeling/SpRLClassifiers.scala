/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation
import edu.illinois.cs.cogcomp.lbjava.learn.Learner
import edu.illinois.cs.cogcomp.saul.classifier.{Learnable, SparseNetworkLBP}
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property

/** Created by taher on 7/30/16.
  */
object SpRLClassifiers {

  import SpRLDataModel._

  val pairFeatures = List(argLemma, argPosTag, argHeadword, argSubCategorization,
    pivotLemma, pivotPosTag, pivotHeadword, pivotSubCategorization, isPivot) //, pathToPivot, positionRelativeToPivot)

  val tokenFeatures = List(lemma, posTag, headword, subCategorization)

  object pairTypeClassifier extends Learnable[Relation](pairs) {
    override lazy val classifier: Learner = new SparseNetworkLBP

    def label: Property[Relation] = pairType

    override def feature: List[Property[Relation]] = using(pairFeatures.union(List(pipeLineIsSp)))
  }
  object spatialIndicatorClassifier extends Learnable[Relation](pairs) {

    override lazy val classifier = new SparseNetworkLBP

    def label: Property[Relation] = isSpatialIndicator

    override def feature = using(pairFeatures)
  }

  object trajectorClassifier extends Learnable[Relation](pairs) {

    override lazy val classifier = new SparseNetworkLBP

    def label: Property[Relation] = isTrajector

    override def feature = using(pairFeatures)
  }

  object landmarkClassifier extends Learnable[Relation](pairs) {

    override lazy val classifier = new SparseNetworkLBP

    def label: Property[Relation] = isLandmark

    override def feature = using(pairFeatures)
  }
}
