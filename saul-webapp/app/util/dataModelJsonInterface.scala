package util

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge
import edu.illinois.cs.cogcomp.saul.datamodel.node.{ NodeProperty, Node }

object dataModelJsonInterface {

  import play.api.libs.json._
  def getPopulatedInstancesJson(dm: DataModel): JsValue = {

    val declaredFields = dm.getClass.getDeclaredFields
    val nodes = declaredFields.filter(_.getType.getSimpleName == "Node")
    val edges = declaredFields.filter(_.getType.getSimpleName == "Edge")
    val properties = declaredFields.filter(_.getType.getSimpleName.contains("Property")).filterNot(_.getName.contains("$module"))

    val nodesObjs = nodes.map { n =>
      n.setAccessible(true)
      (n.getName, n.get(dm))
    }

    val edgesObjs = edges.map { n =>
      n.setAccessible(true)
      (n.getName, n.get(dm))
    }
    /*
    val selected = {
      if (visualizer.instanceSet != null) {
        getInstanceQueryJson
      } else if (visualizer.propertySet != null) {
        getPropertyQueryJson
      } else {
        None
      }
    }*/

    /** @return json representation of the whole graph
      */
    def getFullJson = {
      val nodesJson = buildNodesJson(nodesObjs)
      val invertedNodesMap: Map[Object, String] = nodesObjs.map(_.swap).toMap
      val edgesJson = buildEdgeJson(invertedNodesMap)
      val propertiesJson = buildPropertiesJson(nodesObjs)
      parseJsonGraph(nodesJson, edgesJson, propertiesJson)
    }

    def parseJsonGraph(
      nodesJson: Map[String, Array[String]],
      edgesJson: List[(String, String)],
      propertiesJson: List[(String, Map[String, String])]
    ): JsObject = {
      JsObject(Seq(
        "nodes" -> Json.toJson(nodesJson),
        "edges" -> Json.toJson(edgesJson.groupBy(_._1).map { case (k, v) => (k, v.map(_._2)) }),
        "properties" -> Json.toJson(propertiesJson.groupBy(_._1).map { case (k, v) => (k, v.map(_._2)) })
      ))
    }

    def getPropertyQueryJson = {

    }

    def getInstanceQueryJson = {
      val queryNodesObjs = nodesObjs.filter(node => node._2 eq visualizer.instanceSet.node)
      val nodesJson = buildNodesJson(queryNodesObjs)
      val invertedNodesMap: Map[Object, String] = queryNodesObjs.map(_.swap).toMap
      val edgesJson = buildEdgeJson(invertedNodesMap)
      val propertiesJson = buildPropertiesJson(queryNodesObjs)
      parseJsonGraph(nodesJson, edgesJson, propertiesJson)
    }

    def buildNodesJson(nodes: Array[(String, AnyRef)]): Map[String, Array[String]] = {
      nodesObjs.map {
        case (name, node) =>
          (name, node.asInstanceOf[Node[_]].getAllInstances.map(x => name + x.hashCode.toString).toArray)
      } toMap
    }

    def buildEdgeJson(invertedNodesMap: Map[Object, String]): List[(String, String)] = {
      var edgesJson = List[(String, String)]()
      for {
        (name, edge) <- edgesObjs;
        (start, ends) <- edge.asInstanceOf[Edge[_, _]].forward.index
      } {
        val from = invertedNodesMap.get(edge.asInstanceOf[Edge[_, _]].from) match {
          case Some(v) => v + start.hashCode.toString
          case _ => ""
        }

        for (end <- ends) {
          val to = invertedNodesMap.get(edge.asInstanceOf[Edge[_, _]].to) match {
            case Some(v) => v + end.hashCode.toString
            case _ => ""
          }
          if (from != "" && to != "") {
            edgesJson = (from, to) :: edgesJson
          }
        }
      }
      edgesJson
    }

    def buildPropertiesJson(nodes: Array[(String, AnyRef)]): List[(String, Map[String, String])] = {
      var propertiesJson = List[(String, Map[String, String])]()
      for (p <- properties) {
        p.setAccessible(true)
        println(p.getName + "---------------------------------------------")
        val propertyObj = p.get(dm).asInstanceOf[NodeProperty[AnyRef]]
        nodes.find { case (_, x) => x == propertyObj.node } match {
          case Some((nodeName, node)) => {
            propertiesJson = node.asInstanceOf[Node[_]]
              .getAllInstances.map(x => nodeName + x.hashCode.toString)
              .toList.zip(node.asInstanceOf[Node[AnyRef]]
                .getAllInstances
                .map(x => Map(p.getName -> propertyObj(x).toString))) ::: propertiesJson

          }
          case None =>
        }
      }
      propertiesJson
    }
    getFullJson
  }
  def getSchemaJson(dm: DataModel): JsValue = {
    val declaredFields = dm.getClass.getDeclaredFields

    val nodes = declaredFields.filter(_.getType.getSimpleName == "Node")
    val edges = declaredFields.filter(_.getType.getSimpleName == "Edge")
    val properties = declaredFields.filter(_.getType.getSimpleName.contains("Property")).filterNot(_.getName.contains("$module"))

    //get a name-field tuple
    val nodesObjs = nodes.map { n =>
      n.setAccessible(true)
      (n.getName, n.get(dm))
    }

    import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
    //get a map of property -> [corresponding nodes]
    val propertyDict = properties.map {
      p =>
        p.setAccessible(true)
        val propertyObj = p.get(dm).asInstanceOf[NodeProperty[_]]
        nodesObjs.find { case (_, x) => x == propertyObj.node } match {
          case Some((nodeName, _)) => (p.getName, nodeName)
          case _ => (p.getName, "")
        }
    }.toMap

    //get a map of edge -> [two connecting nodes]
    val edgesDict = edges.map {
      e =>
        e.setAccessible(true)
        val edgeObj = e.get(dm).asInstanceOf[Edge[_, _]]
        nodesObjs.find { case (_, x) => x == edgeObj.from } match {
          case Some((startNodeName, _)) => {
            nodesObjs.find { case (_, x) => x == edgeObj.to } match {
              case Some((endNodeName, _)) => (e.getName, List(startNodeName, endNodeName))
              case _ => (e.getName, List())
            }
          }
          case _ => (e.getName, List())
        }
    }.toMap

    val json: JsValue = JsObject(Seq(
      "nodes" -> JsArray(nodes.map(node => JsString(node.getName))),
      "edges" -> Json.toJson(edgesDict),
      "properties" -> Json.toJson(propertyDict)

    ))

    json
  }
}
