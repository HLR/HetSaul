package edu.illinois.cs.cogcomp.saul.conversions

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.Attribute
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