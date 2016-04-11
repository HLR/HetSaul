package edu.illinois.cs.cogcomp.saul.datamodel.property

object PairwiseConjunction {

  def apply[T](x: List[Property[T]], y: T): List[String] = {
    concat(x.head, x.drop(1), y)
  }

  def concat[T](a: Property[T], b: List[Property[T]], y: T): List[String] =
    {
      if (b.isEmpty)
        return List()
      else
        return b.map(x => a.name + "_" + a(y) + "_" + x.name + "_" + x(y)) ::: concat(b.head, b.drop(1), y)
    }
}
