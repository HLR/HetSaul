package edu.illinois.cs.cogcomp.lfs.conversions

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.lfs.constraint.ConstraintTypeConversion
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.Attribute
import edu.illinois.cs.cogcomp.lfs.util.ListNodeInterface

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