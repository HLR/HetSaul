package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.lbjava.learn.SupportVectorMachine
import edu.illinois.cs.cogcomp.saul.classifier.{ Learnable, SparseNetworkLBP }
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property

/** Created by taher on 8/16/16.
  */
object RobertsClassifiers {
  import RobertsDataModel._

  val robertsFeatures = List(JF2_1, JF2_2, JF2_3, JF2_4, JF2_5, JF2_6, JF2_7, JF2_8,
    JF2_9, JF2_10, JF2_11, JF2_12, JF2_13, JF2_14, JF2_15, BH1)

  object robertsSupervised2Classifier extends Learnable[RobertsRelation](relations) {

    override lazy val classifier = new SupportVectorMachine()

    def label: Property[RobertsRelation] = relationLabel

    override def feature = using(robertsFeatures)
  }

}
