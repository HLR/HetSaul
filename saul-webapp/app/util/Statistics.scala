/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package util

import play.api.libs.json._
import java.lang.reflect.Field
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge
import edu.illinois.cs.cogcomp.saul.datamodel.node.{ NodeProperty, Node }

/** A utility class for generating statistics about a trained datamodel
  */
object Statistics {

  def getStatistics(dm: DataModel) = {
    val declaredFields = dm.getClass.getDeclaredFields
    val nodes = declaredFields.filter(_.getType.getSimpleName == "Node")
    val edges = declaredFields.filter(_.getType.getSimpleName == "Edge")
    val properties = declaredFields.filter(_.getType.getSimpleName.contains("Property")).filterNot(_.getName.contains("$module"))
    def getObjs(fields: Array[Field]) = {
      fields.map { n =>
        n.setAccessible(true)
        (n.getName, n.get(dm))
      }
    }
    val nodesObjs = getObjs(nodes)
    val edgesObjs = getObjs(edges)
    val propsObjs = getObjs(properties)

    val nodeStatistics = getNodeStatistics(nodesObjs)

    val propertyStatistics = getPropertyStatistics(nodesObjs, propsObjs)
    JsObject(Seq(
      "nodes" -> nodeStatistics,
      "properties" -> propertyStatistics
    ))
  }

  def getPropertyStatistics(nodeTuples: Iterable[(String, _)], propsObjs: Iterable[(String, _)]) = {
    var stat = Map[String, Map[String, JsValue]]()
    propsObjs foreach {
      case (propertyType, instance) =>
        val propertyObj = instance.asInstanceOf[NodeProperty[AnyRef]]
        val l: List[List[String]] = nodeTuples.map {
          case (_, n) if n == propertyObj.node =>
            n.asInstanceOf[Node[AnyRef]].getAllInstances.map(x => propertyObj(x).toString).toList
          case _ => List[String]()
        }.toList
        val valueList = l.flatten
        stat = stat + (propertyType -> getArrayStatistics(propertyType, valueList))
    }
    println(stat)
    Json.toJson(stat)
  }
  def getNodeStatistics(nodeTuples: Iterable[(String, _)]) = {
    var stat = Map[String, Map[String, JsValue]]()

    nodeTuples foreach {
      case (nodeType, instance) =>

        val valueList = instance.asInstanceOf[Node[_]].getAllInstances.map(x => x.toString).toList
        stat = stat + (nodeType -> (getArrayStatistics(nodeType, valueList) + ("Number of " + nodeType -> Json.toJson(valueList.length.toString))))
      //TODO: add some metric for strings
    }
    println(stat)
    Json.toJson(stat)
  }

  def getArrayStatistics(typ: String, valueList: List[String]) = {
    var stat2 = Map[String, JsValue]()
    isNumberArray(valueList) match {
      case true => {
        stat2 = stat2 + (typ + "'s variance" -> Json.toJson(getVariance(valueList)))
        stat2 = stat2 + (typ + "'s mean" -> Json.toJson(getMean(valueList)))
        stat2 = stat2 + (typ + "'s max" -> Json.toJson(getMax(valueList)))
        stat2 = stat2 + (typ + "'s min" -> Json.toJson(getMin(valueList)))
      }
      case _ =>
    }
    stat2 = stat2 + ("Frequency" -> getFrequency(valueList))
    stat2
  }

  def isNumberArray(list: Iterable[String]): Boolean = {
    try {
      (list.toList)(0).toDouble
    } catch {
      case e: Exception => return false
    }
    return true
  }

  def getFrequency(list: Iterable[String]) = {
    Json.toJson(list.toSeq.groupBy(identity).mapValues(_.size))
  }
  def getVariance(list: Iterable[String]) = {
    val mean = getMean(list).toDouble
    val l = list.map(_.toDouble).map(x => Math.pow(mean - x, 2)).toList
    (l.sum / l.length).toString
  }

  def getMean(list: Iterable[String]) = {
    val l = list.map(_.toDouble).toList
    (l.sum / l.length).toString
  }
  def getMax(list: Iterable[String]) = {
    val l = list.map(_.toDouble).toList
    l.max.toString
  }
  def getMin(list: Iterable[String]) = {
    val l = list.map(_.toDouble).toList
    l.min.toString
  }
}

