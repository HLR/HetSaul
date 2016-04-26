package util

import play.api.libs.json._
import java.lang.reflect.Field
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge
import edu.illinois.cs.cogcomp.saul.datamodel.node.{ NodeProperty, Node }

object Statistics {

	def getStatistics(dm : DataModel) = {
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

		val propertyStatistics = getPropertyStatistics(nodesObjs,propsObjs)
		JsObject(Seq(
      		"nodes" -> nodeStatistics,
      		"properties" -> propertyStatistics
    	))
	}

	def getPropertyStatistics(nodeTuples : Iterable[(String,_)], propsObjs : Iterable[(String,_)]) = {
		var stat = Map[String,String]()
		propsObjs foreach { case (propertyType, instance) =>

			val propertyObj = instance.asInstanceOf[NodeProperty[AnyRef]]
			val l : List[List[String]]= nodeTuples.map{ case(_,n) if n == propertyObj.node =>
				n.asInstanceOf[Node[AnyRef]].getAllInstances.map(x => propertyObj(x).toString).toList
				case _ => List[String]()
			}.toList
			val valueList = l.flatten
			stat = getArrayStatistics(propertyType,valueList,stat)
		}
		println(stat)
		Json.toJson(stat)
	}
	def getNodeStatistics(nodeTuples : Iterable[(String,_)]) = {
		var stat = Map[String,String]()

		nodeTuples foreach { case (nodeType, instance) =>

			val valueList = instance.asInstanceOf[Node[_]].getAllInstances.map(x => x.toString).toList
			stat = stat + ("Number of " + nodeType -> valueList.length.toString)
			stat = getArrayStatistics(nodeType,valueList,stat)
			//TODO: add some metric for strings
		}
		println(stat)
		Json.toJson(stat)
	}

	def getArrayStatistics(typ: String,valueList : List[String], stat:Map[String,String]) = {
		var stat2 = stat
		isNumberArray(valueList) match {
			case true => {
					stat2 = stat2 + (typ+"'s variance" -> getVariance(valueList))
					stat2 = stat2 + (typ+"'s mean" -> getMean(valueList))
					stat2 = stat2 + (typ+"'s max" -> getMax(valueList))
					stat2 = stat2 + (typ+"'s min" -> getMin(valueList))
			}
			case _ =>
		}
		stat2
	}
	def isNumberArray(list : Iterable[String]) : Boolean = {
		try{
			(list.toList)(0).toDouble
		} catch{
			case e : Exception => return false
		}
		return true
	}

	def getVariance(list : Iterable[String]) = {
		val mean = getMean(list).toDouble
		val l = list.map(_.toDouble).map(x=>Math.pow(mean-x,2)).toList
		(l.sum / l.length).toString
	}

	def getMean(list: Iterable[String]) = {
		val l = list.map(_.toDouble).toList
		(l.sum / l.length).toString
	}
	def getMax(list : Iterable[String]) = {
		val l = list.map(_.toDouble).toList
		l.max.toString
	}
	def getMin(list : Iterable[String]) = {
		val l = list.map(_.toDouble).toList
		l.min.toString
	}
}

