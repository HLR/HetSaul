/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.NamedEntityRecognition

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saulexamples.nlp.NamedEntityRecognition.NERDataModel._
/** Created by Parisa on 5/17/16.
  */
object NERClassifiers {

  object NERClassifier extends Learnable[Constituent](word) {
    def label = NERLabel
    override def feature = using(surface)
    override lazy val classifier = new SparseNetworkLearner()
  }
}

