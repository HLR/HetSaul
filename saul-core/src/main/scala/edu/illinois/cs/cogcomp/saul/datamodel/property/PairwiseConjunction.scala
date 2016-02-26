package edu.illinois.cs.cogcomp.saul.datamodel.property

object PairwiseConjunction {

  def apply[T](x: List[Property[T]], y: T): List[String] = {
    var a: List[String] = List()
    for (t1 <- 0 until x.size - 1) {
      for (t2 <- t1 + 1 until x.size) {
        a = (x(t1).name + "_" + x(t1)(y) + "_" + x(t2).name + "_" + x(t2)(y)) :: a }
    }
    a
  }
}

