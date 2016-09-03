/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.node

import scala.reflect.ClassTag

class JoinNode[A <: AnyRef, B <: AnyRef](val na: Node[A], val nb: Node[B], matcher: (A, B) => Boolean, tag: ClassTag[(A, B)])
  extends Node[(A, B)](p => na.keyFunc(p._1) -> nb.keyFunc(p._2), tag) {

  def addFromChild[T <: AnyRef](node: Node[T], t: T, train: Boolean = true, populateEdge: Boolean = true) = {
    node match {
      case this.na => matchAndAddChildrenA(t.asInstanceOf[A], train, populateEdge)
      case this.nb => matchAndAddChildrenB(t.asInstanceOf[B], train, populateEdge)
    }
  }

  private def matchAndAddChildrenA(a: A, train: Boolean = true, populateEdge: Boolean = true): Unit = {
    val instances = if (train) nb.getTrainingInstances else nb.getTestingInstances
    for ((a, b) <- instances.filter(matcher(a, _)).map(a -> _)) {
      if (!contains(a -> b))
        this addInstance (a -> b, train, populateEdge)
    }
  }

  private def matchAndAddChildrenB(b: B, train: Boolean = true, populateEdge: Boolean = true): Unit = {
    val instances = if (train) na.getTrainingInstances else na.getTestingInstances
    for ((a, b) <- instances.filter(matcher(_, b)).map(_ -> b)) {
      if (!contains(a -> b))
        this addInstance (a -> b, train, populateEdge)
    }
  }

  override def addInstance(t: (A, B), train: Boolean, populateEdge: Boolean = true): Unit = {
    assert(matcher(t._1, t._2))
    if (!contains(t)) {
      super.addInstance(t, train, populateEdge)
      na.addInstance(t._1, train, populateEdge)
      nb.addInstance(t._2, train, populateEdge)
    }
  }
}
