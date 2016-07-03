/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.DrugResponse

import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property

import scala.reflect.ClassTag

/** Created by Parisa on 4/30/16.
  */
object Queries {
  // This query recieves a node and groups them based on a specific property and generates a grouped list of items where the base item is the key for ech group and p2 is another property that is listed.

  def SGroupBy[T <: AnyRef](n: Node[T], p1: Property[T], p2: Property[T])(implicit t: ClassTag[T]): Map[String, Iterable[p2.S]] = {
    val groupLists = n().map(x => (p1(x).asInstanceOf[List[String]].map(pvalue => (p2(x), pvalue)))).flatten.groupBy(_._2).map(x => (x._1, x._2.map(t1 => t1._1)))
    return groupLists
  }

  def SGroupBy[T <: AnyRef](n: Node[T], p1: Property[T])(implicit t: ClassTag[T]): Map[String, Iterable[T]] = {
    val groupLists = n().map(x => (p1(x).asInstanceOf[List[String]].map(pvalue => (x, pvalue)))).flatten.groupBy(_._2).map(x => (x._1, x._2.map(t1 => t1._1)))
    return groupLists
  }
}
