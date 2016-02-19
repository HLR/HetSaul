package edu.illinois.cs.cogcomp.saul.datamodel

import edu.illinois.cs.cogcomp.lbjava.util.{ ExceptionlessInputStream, ExceptionlessOutputStream }
import edu.illinois.cs.cogcomp.saul.datamodel.edge.{ AsymmetricEdge, Edge, Link, SymmetricEdge }
import edu.illinois.cs.cogcomp.saul.datamodel.node.{ JoinNode, Node, NodeProperty }
import edu.illinois.cs.cogcomp.saul.datamodel.property.EvaluatedProperty
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete._
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.real._

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

trait DataModel {
  val PID = 'PID

  final val NODES = new ListBuffer[Node[_]]
  final val PROPERTIES = new ListBuffer[NodeProperty[_]]
  final val EDGES = new ListBuffer[Edge[_, _]]

  // TODO: Implement this function.
  def select[T <: AnyRef](node: Node[T], conditions: EvaluatedProperty[T, _]*): List[T] = {
    val conds = conditions.toList
    node.getAllInstances.filter({
      t =>
        conds.exists({
          cond => cond.property.sensor(t).equals(cond.value)
        })
    }).toList
  }

  def clearInstances = {
    NODES.foreach(_.clear)
    EDGES.foreach(_.clear)
  }

  def addFromModel[T <: DataModel](dataModel: T): Unit = {
    assert(this.NODES.size == dataModel.NODES.size)
    for ((n1, n2) <- NODES.zip(dataModel.NODES)) {
      n1.populateFrom(n2)
    }
    assert(this.EDGES.size == dataModel.EDGES.size)
    for ((e1, e2) <- EDGES.zip(dataModel.EDGES)) {
      e1.populateFrom(e2)
    }
  }

  def testWith[T <: AnyRef](coll: Seq[T])(implicit tag: ClassTag[T]) = {
    println("Adding for type" + tag.toString)
    //getNodeWithType[T].addToTest(coll)
  }

  /** node definitions */
  def node[T <: AnyRef](implicit tag: ClassTag[T]): Node[T] = node((x: T) => x)

  def node[T <: AnyRef](keyFunc: T => Any)(implicit tag: ClassTag[T]): Node[T] = {
    val n = new Node[T](keyFunc, tag)
    NODES += n
    n
  }

  def join[A <: AnyRef, B <: AnyRef](a: Node[A], b: Node[B])(matcher: (A, B) => Boolean)(implicit tag: ClassTag[(A, B)]): Node[(A, B)] = {
    val n = new JoinNode(a, b, matcher, tag)
    a.joinNodes += n
    b.joinNodes += n
    NODES += n
    n
  }

  /** edges */
  def edge[A <: AnyRef, B <: AnyRef](a: Node[A], b: Node[B], name: Symbol = 'default): Edge[A, B] = {
    val e = AsymmetricEdge(new Link(a, b, Some(name)), new Link(b, a, Some(Symbol("-" + name.name))))
    a.outgoing += e
    b.incoming += e
    EDGES += e
    e
  }

  def symmEdge[A <: AnyRef](a: Node[A], b: Node[A], name: Symbol = 'default): Edge[A, A] = {
    val e = SymmetricEdge(new Link(a, b, Some(name)))
    a.incoming += e
    a.outgoing += e
    b.incoming += e
    b.outgoing += e
    EDGES += e
    e
  }

  /** property definitions */
  object PropertyType extends Enumeration {
    val Real, Discrete = Value
    type PropertyType = Value
  }

  import PropertyType._

  case class PropertyDefinition(ty: PropertyType, name: Symbol)

  class PropertyApply[T <: AnyRef] private[DataModel] (val node: Node[T], name: String, cache: Boolean, ordered: Boolean) { papply =>

    // TODO: make the hashmaps immutable
    val propertyCacheMap = collection.mutable.HashMap[T, Any]()
    node.propertyCacheList += propertyCacheMap

    def getOrUpdate(input: T, f: (T) => Any): Any = { propertyCacheMap.getOrElseUpdate(input, f(input)) }

    def apply(f: T => Boolean)(implicit tag: ClassTag[T]): BooleanProperty[T] = {
      def cachedF = if (cache) { x: T => getOrUpdate(x, f).asInstanceOf[Boolean] } else f
      val a = new BooleanProperty[T](name, cachedF) with NodeProperty[T] { override def node: Node[T] = papply.node }
      papply.node.properties += a
      PROPERTIES += a
      a
    }

    def apply(f: T => List[Int])(implicit tag: ClassTag[T], d: DummyImplicit): RealPropertyCollection[T] = {
      def cachedF = if (cache) { x: T => getOrUpdate(x, f).asInstanceOf[List[Int]] } else f
      val newf: T => List[Double] = { t => cachedF(t).map(_.toDouble) }
      val a = if (ordered) {
        new RealArrayProperty[T](name, newf) with NodeProperty[T] {
          override def node: Node[T] = papply.node
        }
      } else {
        new RealGenProperty[T](name, newf) with NodeProperty[T] {
          override def node: Node[T] = papply.node
        }
      }
      papply.node.properties += a
      PROPERTIES += a
      a
    }

    /** Discrete sensor feature with range, same as real name in lbjava */
    def apply(f: T => Int)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit): RealProperty[T] = {
      def cachedF = if (cache) { x: T => getOrUpdate(x, f).asInstanceOf[Int] } else f
      val newf: T => Double = { t => cachedF(t).toDouble }
      val a = new RealProperty[T](name, newf) with NodeProperty[T] {
        override def node: Node[T] = papply.node
      }
      papply.node.properties += a
      PROPERTIES += a
      a
    }

    /** Discrete sensor feature with range, same as real% and real[] in lbjava */
    def apply(f: T => List[Double])(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit,
      d3: DummyImplicit): RealCollectionProperty[T] = {
      def cachedF = if (cache) { x: T => getOrUpdate(x, f).asInstanceOf[List[Double]] } else f
      val a = new RealCollectionProperty[T](name, cachedF, ordered) with NodeProperty[T] {
        override def node: Node[T] = papply.node
      }
      papply.node.properties += a
      PROPERTIES += a
      a
    }

    /** Discrete sensor feature with range, same as real name in lbjava */
    def apply(f: T => Double)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit): RealProperty[T] = {
      def cachedF = if (cache) { x: T => getOrUpdate(x, f).asInstanceOf[Double] } else f
      val a = new RealProperty[T](name, cachedF) with NodeProperty[T] {
        override def node: Node[T] = papply.node
      }
      papply.node.properties += a
      PROPERTIES += a
      a
    }

    /** Discrete feature without range, same as discrete SpamLabel in lbjava */
    def apply(f: T => String)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit, d5: DummyImplicit): DiscreteProperty[T] = {
      def cachedF = if (cache) { x: T => getOrUpdate(x, f).asInstanceOf[String] } else f
      val a = new DiscreteProperty[T](name, cachedF, None) with NodeProperty[T] {
        override def node: Node[T] = papply.node
      }
      papply.node.properties += a
      PROPERTIES += a
      a
    }

    /** Discrete array feature with range, same as discrete[] and discrete% in lbjava */
    def apply(f: T => List[String])(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit, d5: DummyImplicit, d6: DummyImplicit): DiscreteCollectionProperty[T] = {
      def cachedF = if (cache) { x: T => getOrUpdate(x, f).asInstanceOf[List[String]] } else f
      val a = if (ordered) {
        new DiscreteCollectionProperty[T](name, cachedF, ordered = false) with NodeProperty[T] {
          override def node: Node[T] = papply.node
        }
      } else {
        new DiscreteCollectionProperty[T](name, cachedF, ordered = true) with NodeProperty[T] {
          override def node: Node[T] = papply.node
        }
      }
      papply.node.properties += a
      PROPERTIES += a
      a
    }

    /** Discrete feature with range, same as discrete{"spam", "ham"} SpamLabel in lbjava */
    def apply(range: String*)(f: T => String)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit, d5: DummyImplicit, d6: DummyImplicit,
      d7: DummyImplicit): DiscreteProperty[T] = {
      def cachedF = if (cache) { x: T => getOrUpdate(x, f).asInstanceOf[String] } else f
      val r = range.toList
      val a = new DiscreteProperty[T](name, cachedF, Some(r)) with NodeProperty[T] {
        override def node: Node[T] = papply.node
      }
      papply.node.properties += a
      PROPERTIES += a
      a
    }
  }

  def property[T <: AnyRef](node: Node[T], name: String = "prop" + PROPERTIES.size, cache: Boolean = false, ordered: Boolean = false) =
    new PropertyApply[T](node, name, cache, ordered)

  /** Methods for caching Data Model */
  var hasDerivedInstances = false

  def deriveInstances() = {
    NODES.foreach { node =>
      val relatedProperties = PROPERTIES.filter(property => property.tag.equals(node.tag)).toList
      node.deriveInstances(relatedProperties)
    }
    EDGES.foreach { edge =>
      edge.deriveIndexWithIds()
    }
    hasDerivedInstances = true
  }

  val defaultDIFilePath = "models/" + getClass.getCanonicalName + ".di"

  def write(filePath: String = defaultDIFilePath) = {
    val out = ExceptionlessOutputStream.openCompressedStream(filePath)

    out.writeInt(NODES.size)
    NODES.zipWithIndex.foreach {
      case (node, nodeId) =>
        out.writeInt(nodeId)
        node.writeDerivedInstances(out)
    }

    out.writeInt(EDGES.size)
    EDGES.zipWithIndex.foreach {
      case (edge, edgeId) =>
        out.writeInt(edgeId)
        edge.writeIndexWithIds(out)
    }

    out.close()
  }

  def load(filePath: String = defaultDIFilePath) = {
    val in = ExceptionlessInputStream.openCompressedStream(filePath)

    val nodesSize = in.readInt()
    (0 until nodesSize).foreach { _ =>
        val nodeId = in.readInt()
        NODES(nodeId).loadDerivedInstances(in)
    }

    val edgesSize = in.readInt()
    (0 until edgesSize).foreach { _ =>
        val edgeId = in.readInt()
        EDGES(edgeId).loadIndexWithIds(in)
    }

    in.close()

    hasDerivedInstances = true
  }
}

object dataModelJsonInterface {
  def getJson(dm: DataModel): String = {
    val declaredFields = dm.getClass.getDeclaredFields

    val nodes = declaredFields.filter(_.getType.getSimpleName == "Node")
    val edges = declaredFields.filter(_.getType.getSimpleName == "Edge")
    val properties = declaredFields.filter(_.getType.getSimpleName.contains("Property")).filterNot(_.getName.contains("$module"))

    import play.api.libs.json._

    val json: JsValue = JsObject(Seq(
      "nodes" -> JsArray(nodes.map(node => JsString(node.getName))),
      "edges" -> JsArray(edges.map(edge => JsString(edge.getName))),
      "properties" -> JsArray(properties.map(prop => JsString(prop.getName)))
    ))

    println(json.toString())

    json.toString()
  }
}
