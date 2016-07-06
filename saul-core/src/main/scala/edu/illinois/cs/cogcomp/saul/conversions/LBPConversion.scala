/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.conversions

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property$
import edu.illinois.cs.cogcomp.saul.util.ListNodeInterface

import scala.reflect.ClassTag

object LBPConversion {

  implicit def listToNodeBuilder[T <: AnyRef](l: List[T])(implicit tag: ClassTag[T]): ListNodeInterface[T] = {
    new ListNodeInterface[T](l)
  }

  implicit def symbolWithEqualToMakePair(s: Symbol): SymbolWithEqualToMakePair = {
    new SymbolWithEqualToMakePair(s)
  }
}
// TODO: remove this
class SymbolWithEqualToMakePair(s: Symbol) {
  def ===(s2: Symbol): (Symbol, Symbol) = {
    (s, s2)
  }
}