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
    } filter (t =>
      dm.NODES contains t._2)

    val invertedNodesMap: Map[Object, String] = nodesObjs.map(_.swap).toMap

    val edgesObjs = edges.map { n =>
      n.setAccessible(true)
      (n.getName, n.get(dm))
    }

    var edgesJson = List[(String, String)]()

    val selectedNodes = nodesObjs.flatMap {
      case (name, node) => node.asInstanceOf[Node[_]].getAllInstances.map(x => name + x.hashCode.toString).toSet[String]
    }

    for {
      (name, edge) <- edgesObjs;
      (start, ends) <- edge.asInstanceOf[Edge[_, _]].forward.index
    } {

      val from = invertedNodesMap.get(edge.asInstanceOf[Edge[_, _]].from) match {
        case Some(v) => v + start.hashCode.toString
        case _ => ""
      }
      if (selectedNodes contains from) {
        for (end <- ends) {
          val to = invertedNodesMap.get(edge.asInstanceOf[Edge[_, _]].to) match {
            case Some(v) => v + end.hashCode.toString
            case _ => ""
          }
          if (selectedNodes contains to) {
            edgesJson = (from, to) :: edgesJson

          }
        }
      }

    }

    val nodesJson = nodesObjs.map {
      case (name, node) =>
        (name, node.asInstanceOf[Node[_]].getAllInstances.map(x => name + x.hashCode.toString).toArray)
    } toMap

    var propertiesJson = List[(String, Map[String, String])]()

    for (p <- properties) {
      p.setAccessible(true)
      println(p.getName + "---------------------------------------------")
      val propertyObj = p.get(dm).asInstanceOf[NodeProperty[AnyRef]]
      nodesObjs.find { case (_, x) => x == propertyObj.node } match {
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

    JsObject(Seq(
      "nodes" -> Json.toJson(nodesJson),
      "edges" -> Json.toJson(edgesJson.groupBy(_._1).map { case (k, v) => (k, v.map(_._2)) }),
      "properties" -> Json.toJson(propertiesJson.groupBy(_._1).map { case (k, v) => (k, v.map(_._2)) })
    ))

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
