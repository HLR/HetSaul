/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.lbjrelated

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier

/** Encapsulates an instance of LBJava's [[Classifier]] class.
  */
trait LBJClassifierEquivalent {
  val classifier: Classifier
}
