/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package util

import java.lang.reflect.Field

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge
import edu.illinois.cs.cogcomp.saul.datamodel.node.{ NodeProperty, Node }

object DataModelJsonInterface {

  import play.api.libs.json._
  def getPopulatedInstancesJson(dm: DataModel): JsValue = {
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

    /** Function used for parsing the whole data model
      *
      * @return json representation of the whole populated data model
      */
    def getFullJson = {
      val nodesJson = nodesObjs.map {
        case (name, node) =>
          (name, node.asInstanceOf[Node[_]].getAllInstances.map(x => (name + x.hashCode.toString, x.toString)).toMap)
      } toMap
      val invertedNodesMap: Map[Object, String] = nodesObjs.map(_.swap).toMap
      val edgesJson = buildEdgeJson(invertedNodesMap)
      val propertiesJson = buildPropertiesJson(nodesObjs)
      parseJsonGraph(nodesJson, edgesJson, propertiesJson)
    }

    /** Function used for query 'prop'
      * @return json representation of the queried properties and associated nodes
      */
    def getPropertyQueryJson: JsObject = {
      val instanceSet = VisualizerInstance.propertySet.underlying
      val property = VisualizerInstance.propertySet.property
      val queryNodesObjs = nodesObjs.filter { case (_, instance) => instance eq instanceSet.node }
      val name = queryNodesObjs(0)._1
      val nodesJson = Map(name ->
        instanceSet.instances.map(x => (name + x.hashCode.toString, x.toString)).toMap)
      val invertedNodesMap: Map[Object, String] = queryNodesObjs.map(_.swap).toMap
      val queryPropsObjs = propsObjs.filter(prop => prop._2 eq property)
      val invertedPropsMap: Map[Object, String] = queryPropsObjs.map(_.swap).toMap
      val edgesJson = buildEdgeJson(invertedNodesMap)
      val propsJson = instanceSet.instances.map(instance => name + instance.hashCode.toString).toList.zip(
        instanceSet.instances.map(instance => Map(invertedPropsMap(property) -> property(instance).toString))
      )
      parseJsonGraph(nodesJson, edgesJson, propsJson)
    }

    /** Function used to get the sub-graph after query '~>' and 'filter'
      *
      * @return json representation of the queried nodes and associated edges after query
      */
    def getInstanceQueryJson: JsObject = {
      val queryNodesObjs = nodesObjs.filter { case (_, instance) => instance eq VisualizerInstance.instanceSet.node }
      val name = queryNodesObjs(0)._1
      val nodesJson = Map(name ->
        VisualizerInstance.instanceSet.instances.map(x => (name + x.hashCode.toString, x.toString)).toMap)
      val invertedNodesMap: Map[Object, String] = queryNodesObjs.map(_.swap).toMap
      val edgesJson = buildEdgeJson(invertedNodesMap)
      val propertiesJson = buildPropertiesJson(queryNodesObjs)
      parseJsonGraph(nodesJson, edgesJson, propertiesJson)
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

    /** Build the json object representing the sub-graphs of properties
      *
      * @param nodes  Nodes of the datamodel
      * @return       Json object represeting the sub-graphs of properties
      */
    def buildPropertiesJson(nodes: Array[(String, AnyRef)]): List[(String, Map[String, String])] = {
      var propertiesJson = List[(String, Map[String, String])]()
      for (p <- properties) {
        p.setAccessible(true)
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

    def parseJsonGraph(
      nodesJson: Map[String, Map[String, String]],
      edgesJson: List[(String, String)],
      propertiesJson: List[(String, Map[String, String])]
    ): JsObject = {
      JsObject(Seq(
        "nodes" -> Json.toJson(nodesJson),
        "edges" -> Json.toJson(edgesJson.groupBy(_._1).map { case (k, v) => (k, v.map(_._2)) }),
        "properties" -> Json.toJson(propertiesJson.groupBy(_._1).map { case (k, v) => (k, v.map(_._2)) })
      ))
    }

    val selectedGraph: JsValue = {
      if (VisualizerInstance.instanceSet != null) {
        getInstanceQueryJson
      } else if (VisualizerInstance.propertySet != null) {
        getPropertyQueryJson
      } else {
        JsNull
      }
    }

    val fullGraph = getFullJson
    JsObject(Map("selected" -> selectedGraph, "full" -> fullGraph))
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
