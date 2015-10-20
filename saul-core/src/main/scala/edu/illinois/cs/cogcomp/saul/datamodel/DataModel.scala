package edu.illinois.cs.cogcomp.saul.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete._
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.real._
import edu.illinois.cs.cogcomp.saul.datamodel.property.{ Property, EvaluatedProperty }
import edu.illinois.cs.cogcomp.saul.datamodel.node.{ JoinNode, Node }
import edu.illinois.cs.cogcomp.saul.datamodel.edge.{ Edge, Link }

import scala.collection.mutable.{ Map => MutableMap, ListBuffer }
import scala.reflect.ClassTag

trait DataModel {
  val PID = 'PID

  final val NODES = new ListBuffer[Node[_]]
  final val PROPERTIES = new ListBuffer[Property[_]]
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

  // TODO: keep one of the following three functions
  def getAllPropertiesOf[T <: AnyRef](implicit tag: ClassTag[T]): Seq[Property[T]] = {
    // TODO: implement this
    getAllFeatures[T]
  }

  def getAllFeatures[T](implicit tag: ClassTag[T]): Seq[Property[T]] = {
    this.PROPERTIES.filter(_.tag.equals(tag)).map(_.asInstanceOf[Property[T]])
  }

  // TODO: create lbj feature classifier.
  def getFeaturesOf[T](implicit tag: ClassTag[T]): Seq[Property[T]] = {
    this.getAllFeatures[T]
  }

  /** Functions for internal usages. */
  def getAllPropertyOf[T <: AnyRef](implicit tag: ClassTag[T]): List[Property[T]] = {
    this.PROPERTIES.filter(a => a.tag.equals(tag)).map(_.asInstanceOf[Property[T]]).toList
  }

  @deprecated("Use node.populate() instead.")
  def populate[T <: AnyRef](node: Node[T], coll: Seq[T]) = {
    node.populate(coll)
  }

  @deprecated
  def getNodeWithType[T <: AnyRef](implicit tag: ClassTag[T]): Node[T] = {
    this.NODES.filter {
      e: Node[_] => tag.equals(e.tag)
    }.head.asInstanceOf[Node[T]]
  }

  @deprecated
  def getFromRelation[FROM <: AnyRef, NEED <: AnyRef](t: FROM)(implicit tag: ClassTag[FROM], headTag: ClassTag[NEED]): Iterable[NEED] = {
    val dm = this
    if (tag.equals(headTag)) {
      Set(t.asInstanceOf[NEED])
    } else {
      val r = this.EDGES.filter {
        r => r.from.tag.toString.equals(tag.toString) && r.to.tag.toString.equals(headTag.toString)
      }
      if (r.isEmpty) {
        // reverse search
        val r = this.EDGES.filter {
          r => r.to.tag.toString.equals(tag.toString) && r.from.tag.toString.equals(headTag.toString)
        }
        if (r.isEmpty) {
          throw new Exception(s"Failed to found relations between $tag to $headTag")
        } else r flatMap (_.asInstanceOf[Edge[NEED, FROM]].backward.neighborsOf(t)) distinct
      } else r flatMap (_.asInstanceOf[Edge[FROM, NEED]].forward.neighborsOf(t)) distinct
    }
  }

  // TODO: comment this function
  @deprecated
  def getFromRelation[T <: AnyRef, HEAD <: AnyRef](name: Symbol, t: T)(implicit tag: ClassTag[T], headTag: ClassTag[HEAD]): Iterable[HEAD] = {
    if (tag.equals(headTag)) {
      List(t.asInstanceOf[HEAD])
    } else {
      val r = this.EDGES.filter {
        r =>
          r.to.tag.equals(tag) && r.from.tag.equals(headTag) && r.forward.name.isDefined && name.equals(r.forward.name.get)
      }

      // there must be only one such relation
      if (r.isEmpty) {
        throw new Exception(s"Failed to find any relation between $tag to $headTag")
      } else if (r.size > 1) {
        throw new Exception(s"Found too many relations between $tag to $headTag,\nPlease specify a name")
      } else {
        r.head.asInstanceOf[Link[T, HEAD]].neighborsOf(t)
      }
    }
  }

  @deprecated
  def getRelatedFieldsBetween[T <: AnyRef, U <: AnyRef](implicit fromTag: ClassTag[T], toTag: ClassTag[U]): Iterable[Link[T, U]] = {
    this.EDGES.filter(r => r.from.tag.equals(fromTag) && r.to.tag.equals(toTag)).map(_.forward.asInstanceOf[Link[T, U]]) ++
      this.EDGES.filter(r => r.to.tag.equals(fromTag) && r.from.tag.equals(toTag)).map(_.backward.asInstanceOf[Link[T, U]])
  }

  def testWith[T <: AnyRef](coll: Seq[T])(implicit tag: ClassTag[T]) = {
    println("Adding for type" + tag.toString)
    //getNodeWithType[T].addToTest(coll)
  }

  /** node definitions */
  def node[T <: AnyRef](implicit tag: ClassTag[T]): Node[T] = {
    val n = new Node[T](tag)
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
    val e = Edge(new Link(a, b, Some(name)), new Link(b, a, Some(Symbol("-" + name.name))))
    a.outgoing += e
    b.incoming += e
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

  /** Discrete feature without range, same as discrete SpamLabel in lbjava */
  def discretePropertyOf[T <: AnyRef](name: Symbol)(f: T => String)(implicit tag: ClassTag[T]): DiscreteProperty[T] = {
    val a = new DiscreteProperty[T](name.toString, f, None)
    PROPERTIES += a
    a
  }

  /** Discrete feature with range, same as discrete{"spam", "ham"} SpamLabel in lbjava */
  def rangedDiscretePropertyOf[T <: AnyRef](name: Symbol)(range: String*)(f: T => String)(implicit tag: ClassTag[T]): DiscreteProperty[T] = {
    val r = range.toList
    val a = new DiscreteProperty[T](name.toString, f, Some(r))
    PROPERTIES += a
    a
  }

  /** Discrete array feature with range, same as discrete[] SpamLabel in lbjava */
  def discretePropertiesArrayOf[T <: AnyRef](name: Symbol)(f: T => List[String])(implicit tag: ClassTag[T]): DiscreteArrayProperty[T] = {
    val a = new DiscreteArrayProperty[T](name.toString, f)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as discrete% name in lbjava */
  def discretePropertiesGeneratorOf[T <: AnyRef](name: Symbol)(f: T => List[String])(implicit tag: ClassTag[T]): DiscreteGenProperty[T] = {
    val a = new DiscreteGenProperty[T](name.toString, f)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real name in lbjava */
  def realPropertiesOf[T <: AnyRef](name: Symbol)(f: T => Double)(implicit tag: ClassTag[T]): RealProperty[T] = {
    val a = new RealProperty[T](name.toString, f)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real[] name in lbjava */
  def realPropertiesArrayOf[T <: AnyRef](name: Symbol)(f: T => List[Double])(implicit tag: ClassTag[T]): RealArrayProperty[T] = {
    val a = new RealArrayProperty[T](name.toString, f)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real% name in lbjava */
  def realPropertiesGeneratorOf[T <: AnyRef](name: Symbol)(f: T => List[Double])(implicit tag: ClassTag[T]): RealGenProperty[T] = {
    val a = new RealGenProperty[T](name.toString, f)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real name in lbjava */
  def intPropertyOf[T <: AnyRef](name: Symbol)(f: T => Int)(implicit tag: ClassTag[T]): RealProperty[T] = {
    val newf: T => Double = { t => f(t).toDouble }
    val a = new RealProperty[T](name.toString, newf)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real[] name in lbjava */
  def intPropertiesArrayOf[T <: AnyRef](name: Symbol)(f: T => List[Int])(implicit tag: ClassTag[T]): RealArrayProperty[T] = {
    val newf: T => List[Double] = { t => f(t).map(_.toDouble) }
    val a = new RealArrayProperty[T](name.toString, newf)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real% name in lbjava */
  def intPropertiesGeneratorOf[T <: AnyRef](name: Symbol)(f: T => List[Int])(implicit tag: ClassTag[T]): RealGenProperty[T] = {
    val newf: T => List[Double] = { t => f(t).map(_.toDouble) }
    val a = new RealGenProperty[T](name.toString, newf)
    PROPERTIES += a
    a
  }

  def booleanProperyOf[T <: AnyRef](name: Symbol)(f: T => Boolean)(implicit tag: ClassTag[T]): BooleanProperty[T] = {
    val a = new BooleanProperty[T](name.toString, f)
    PROPERTIES += a
    a
  }

  class PropertyApply[T <: AnyRef] private[DataModel] (name: String, ordered: Boolean) {

    def this(name: String) {
      this(name, false)
    }

    // used to be "booleanAttributeOf"
    def apply(f: T => Boolean)(implicit tag: ClassTag[T]): BooleanProperty[T] = {
      val a = new BooleanProperty[T](name, f)
      PROPERTIES += a
      a
    }

    // used ot be "intAttributesGeneratorOf", and "intAttributesArrayOf"
    def apply(f: T => List[Int])(implicit tag: ClassTag[T], d: DummyImplicit): RealPropertyCollection[T] = {
      val newf: T => List[Double] = { t => f(t).map(_.toDouble) }
      val a = if (ordered) {
        new RealArrayProperty[T](name, newf)
      } else {
        new RealGenProperty[T](name, newf)
      }
      PROPERTIES += a
      a
    }

    // used to be "intAttributeOf"
    def apply(f: T => Int)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit): RealProperty[T] = {
      val newf: T => Double = { t => f(t).toDouble }
      val a = new RealProperty[T](name, newf)
      PROPERTIES += a
      a
    }

    // used to be "realAttributesGeneratorOf", and "realAttributesArrayOf"
    def apply(f: T => List[Double])(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit,
      d3: DummyImplicit): RealCollectionProperty[T] = {
      val a = new RealCollectionProperty[T](name, f, ordered)
      PROPERTIES += a
      a
    }

    // used to be called "realAttributeOf"
    def apply(f: T => Double)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit): RealProperty[T] = {
      val a = new RealProperty[T](name, f)
      PROPERTIES += a
      a
    }

    // used to be called "discreteAttributeOf"
    def apply(f: T => String)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit, d5: DummyImplicit): DiscreteProperty[T] = {
      val a = new DiscreteProperty[T](name, f, None)
      PROPERTIES += a
      a
    }

    // used to be called "discreteAttributesArrayOf", and "discreteAttributesGeneratorOf"
    def apply(f: T => List[String])(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit, d5: DummyImplicit, d6: DummyImplicit): DiscreteCollectionProperty[T] = {
      val a = if (ordered) {
        new DiscreteCollectionProperty[T](name, f, ordered = false)
      } else {
        new DiscreteCollectionProperty[T](name, f, ordered = true)
      }
      PROPERTIES += a
      a
    }

    // used to be called "rangedDiscreteAttributeOf"
    def apply(range: String*)(f: T => String)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit, d5: DummyImplicit, d6: DummyImplicit,
      d7: DummyImplicit): DiscreteProperty[T] = {
      val r = range.toList
      val a = new DiscreteProperty[T](name, f, Some(r))
      PROPERTIES += a
      a
    }
  }
  def property[T <: AnyRef](name: String) = new PropertyApply[T](name)
  def property[T <: AnyRef](name: String, ordered: Boolean) = new PropertyApply[T](name, ordered)
}

