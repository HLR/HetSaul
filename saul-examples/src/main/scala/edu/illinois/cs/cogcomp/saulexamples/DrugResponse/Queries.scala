package edu.illinois.cs.cogcomp.saulexamples.DrugResponse

import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property

import scala.reflect.ClassTag

/**
 * Created by Parisa on 4/30/16.
 */
object Queries {
  def SGroupBy[T<:AnyRef] (n:Node[T], p1: Property[T], p2: Property[T])(implicit t: ClassTag[T]): Map[String, Iterable[p2.S]] = {
    val groupLists = n().map(x => (p1(x).asInstanceOf[List[String]].map(pvalue => (p2(x), pvalue)))).flatten.groupBy(_._2).map(x => (x._1, x._2.map(t1 => t1._1)))
    return groupLists
  }
}
