package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.lbjava.learn.{ LinearThresholdUnit, SparseNetworkLearner }

/** Created by Parisa on 5/24/15.
  */
class SparseNetworkLBP() extends SparseNetworkLearner {

  /** Pseudo-Getter method for [[conjunctiveLabels]] field.
    * @return
    */
  def iConjuctiveLables: Boolean = this.conjunctiveLabels

  /** Setter method for [[conjunctiveLabels]] field.
    * @param newVal
    */
  def iConjuctiveLables_=(newVal: Boolean): Unit = this.conjunctiveLabels = newVal

  /** Override method to return LTU of type [[LinearThresholdUnit]].
    * @param i Position of the LTU unit in the network.
    * @return LTU unit of type [[LinearThresholdUnit]]
    */
  override def getLTU(i: Int): LinearThresholdUnit = super.getLTU(i).asInstanceOf[LinearThresholdUnit]
}
