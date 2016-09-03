/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property

object PairwiseConjunction {

  def apply[T](x: List[Property[T]], y: T): List[String] = {
    concat(x.head, x.drop(1), y)
  }

  def concat[T](a: Property[T], b: List[Property[T]], y: T): List[String] =
    {
      if (b.isEmpty)
        List.empty
      else
        b.map(x => a.name + "_" + a(y) + "_" + x.name + "_" + x(y)) ::: concat(b.head, b.drop(1), y)
    }
}
