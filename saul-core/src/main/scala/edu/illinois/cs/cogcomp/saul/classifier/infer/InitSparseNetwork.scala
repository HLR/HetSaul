/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.infer

import edu.illinois.cs.cogcomp.lbjava.learn.{ LinearThresholdUnit, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

/** Created by Parisa on 9/18/16.
  */
object InitSparseNetwork {
  def apply[HEAD <: AnyRef](node: Node[HEAD], cClassifier: ConstrainedClassifier[_, HEAD]) = {
    val allHeads = node.getTrainingInstances
    //this means we are not reading any model into the SparseNetworks
    // but we forget all the models and go over the data to build the right
    // size for the lexicon and the right number of the ltu s
    cClassifier.onClassifier.classifier.forget()
    val iLearner = cClassifier.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
    allHeads.foreach {
      head =>
        {
          val candidates: Seq[_] = cClassifier.getCandidates(head)
          candidates.foreach {
            x =>
              val a = cClassifier.onClassifier.classifier.getExampleArray(x)
              val exampleLabels = a(2).asInstanceOf[Array[Int]]
              val label = exampleLabels(0)
              val N = iLearner.getNetwork.size()
              if (label >= N || iLearner.getNetwork.get(label) == null) {
                val isConjunctiveLabels = iLearner.isUsingConjunctiveLabels | iLearner.getLabelLexicon.lookupKey(label).isConjunctive
                iLearner.setConjunctiveLabels(isConjunctiveLabels)
                val ltu: LinearThresholdUnit = iLearner.getBaseLTU
                ltu.initialize(iLearner.getNumExamples, iLearner.getNumFeatures)
                iLearner.getNetwork.set(label, ltu)
              }
          } // for each candidate
        } // end case
    } // for each example
  } //end f apply
} // end of object