/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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
