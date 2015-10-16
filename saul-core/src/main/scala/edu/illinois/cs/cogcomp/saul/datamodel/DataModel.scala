package edu.illinois.cs.cogcomp.saul.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete._
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.real._
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.{ Attribute, EvaluatedAttribute }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.edge.{ Edge, Link }

 import scala.collection.mutable.{ Map => MutableMap, ListBuffer }
import scala.reflect.ClassTag

trait DataModel {
  val PID = 'PID

  final val NODES = new ListBuffer[Node[_]]
  final val PROPERTIES = new ListBuffer[Attribute[_]]
  final val EDGES = new ListBuffer[Edge[_, _]]

  // TODO: Implement this function.
  def select[T <: AnyRef](node: Node[T], conditions: EvaluatedAttribute[T, _]*): List[T] = {
    val conds = conditions.toList
    node.getAllInstances.filter({
      t =>
        conds.exists({
          cond => cond.attribute.sensor(t).equals(cond.value)
        })
    }).toList
  }

  // TODO: keep one of the following three functions
  def getAllAttributesOf[T <: AnyRef](implicit tag: ClassTag[T]): Seq[Attribute[T]] = {
    // TODO: implement this
    getAllFeatures[T]
  }

  def getAllFeatures[T](implicit tag: ClassTag[T]): Seq[Attribute[T]] = {
    this.PROPERTIES.filter(_.tag.equals(tag)).map(_.asInstanceOf[Attribute[T]])
  }

  // TODO: create lbj feature classifier.
  def getFeaturesOf[T](implicit tag: ClassTag[T]): Seq[Attribute[T]] = {
    this.getAllFeatures[T]
  }

  /** Functions for internal usages. */
  def getAllAttributeOf[T <: AnyRef](implicit tag: ClassTag[T]): List[Attribute[T]] = {
    this.PROPERTIES.filter(a => a.tag.equals(tag)).map(_.asInstanceOf[Attribute[T]]).toList
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

  /** edges */
  def edge[A <: AnyRef, B <: AnyRef](a: Node[A], b: Node[B], name: Symbol = 'default): Edge[A, B] = {
    val e = Edge(new Link(a, b, Some(name)), new Link(b, a, Some(Symbol("-" + name.name))))
    a.outgoing += e
    b.incoming += e
    EDGES += e
    e
  }

  /** attribute definitions */
  object AttributeType extends Enumeration {
    val Real, Discrete = Value
    type AttributeType = Value
  }

  import AttributeType._

  case class AttributeDefinition(ty: AttributeType, name: Symbol)

  /** Discrete feature without range, same as discrete SpamLabel in lbjava */
  def discreteAttributeOf[T <: AnyRef](name: Symbol)(f: T => String)(implicit tag: ClassTag[T]): DiscreteAttribute[T] = {
    val a = new DiscreteAttribute[T](name.toString, f, None)
    PROPERTIES += a
    a
  }

  /** Discrete feature with range, same as discrete{"spam", "ham"} SpamLabel in lbjava */
  def rangedDiscreteAttributeOf[T <: AnyRef](name: Symbol)(range: String*)(f: T => String)(implicit tag: ClassTag[T]): DiscreteAttribute[T] = {
    val r = range.toList
    val a = new DiscreteAttribute[T](name.toString, f, Some(r))
    PROPERTIES += a
    a
  }

  /** Discrete array feature with range, same as discrete[] SpamLabel in lbjava */
  def discreteAttributesArrayOf[T <: AnyRef](name: Symbol)(f: T => List[String])(implicit tag: ClassTag[T]): DiscreteArrayAttribute[T] = {
    val a = new DiscreteArrayAttribute[T](name.toString, f)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as discrete% name in lbjava */
  def discreteAttributesGeneratorOf[T <: AnyRef](name: Symbol)(f: T => List[String])(implicit tag: ClassTag[T]): DiscreteGenAttribute[T] = {
    val a = new DiscreteGenAttribute[T](name.toString, f)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real name in lbjava */
  def realAttributeOf[T <: AnyRef](name: Symbol)(f: T => Double)(implicit tag: ClassTag[T]): RealAttribute[T] = {
    val a = new RealAttribute[T](name.toString, f)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real[] name in lbjava */
  def realAttributesArrayOf[T <: AnyRef](name: Symbol)(f: T => List[Double])(implicit tag: ClassTag[T]): RealArrayAttribute[T] = {
    val a = new RealArrayAttribute[T](name.toString, f)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real% name in lbjava */
  def realAttributesGeneratorOf[T <: AnyRef](name: Symbol)(f: T => List[Double])(implicit tag: ClassTag[T]): RealGenAttribute[T] = {
    val a = new RealGenAttribute[T](name.toString, f)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real name in lbjava */
  def intAttributeOf[T <: AnyRef](name: Symbol)(f: T => Int)(implicit tag: ClassTag[T]): RealAttribute[T] = {
    val newf: T => Double = { t => f(t).toDouble }
    val a = new RealAttribute[T](name.toString, newf)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real[] name in lbjava */
  def intAttributesArrayOf[T <: AnyRef](name: Symbol)(f: T => List[Int])(implicit tag: ClassTag[T]): RealArrayAttribute[T] = {
    val newf: T => List[Double] = { t => f(t).map(_.toDouble) }
    val a = new RealArrayAttribute[T](name.toString, newf)
    PROPERTIES += a
    a
  }

  /** Discrete sensor feature with range, same as real% name in lbjava */
  def intAttributesGeneratorOf[T <: AnyRef](name: Symbol)(f: T => List[Int])(implicit tag: ClassTag[T]): RealGenAttribute[T] = {
    val newf: T => List[Double] = { t => f(t).map(_.toDouble) }
    val a = new RealGenAttribute[T](name.toString, newf)
    PROPERTIES += a
    a
  }

  def booleanAttributeOf[T <: AnyRef](name: Symbol)(f: T => Boolean)(implicit tag: ClassTag[T]): BooleanAttribute[T] = {
    val a = new BooleanAttribute[T](name.toString, f)
    PROPERTIES += a
    a
  }

  class PropertyApply[T <: AnyRef] private[DataModel] (name: String, ordered: Boolean) {

    def this(name: String) {
      this(name, false)
    }

    // used to be "booleanAttributeOf"
    def apply(f: T => Boolean)(implicit tag: ClassTag[T]): BooleanAttribute[T] = {
      val a = new BooleanAttribute[T](name, f)
      PROPERTIES += a
      a
    }

    // used ot be "intAttributesGeneratorOf", and "intAttributesArrayOf"
    def apply(f: T => List[Int])(implicit tag: ClassTag[T], d: DummyImplicit): RealAttributeCollection[T] = {
      val newf: T => List[Double] = { t => f(t).map(_.toDouble) }
      val a = if (ordered) {
        new RealArrayAttribute[T](name, newf)
      } else {
        new RealGenAttribute[T](name, newf)
      }
      PROPERTIES += a
      a
    }

    // used to be "intAttributeOf"
    def apply(f: T => Int)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit): RealAttribute[T] = {
      val newf: T => Double = { t => f(t).toDouble }
      val a = new RealAttribute[T](name, newf)
      PROPERTIES += a
      a
    }

    // used to be "realAttributesGeneratorOf", and "realAttributesArrayOf"
    def apply(f: T => List[Double])(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit,
      d3: DummyImplicit): RealCollectionAttribute[T] = {
      //      val a = if (ordered) {
      //        new RealArrayAttribute[T](name, f)
      //      } else {
      //        new RealGenAttribute[T](name, f)
      //      }
      val a = new RealCollectionAttribute[T](name, f, ordered)
      PROPERTIES += a
      a
    }

    // used to be called "realAttributeOf"
    def apply(f: T => Double)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit): RealAttribute[T] = {
      val a = new RealAttribute[T](name, f)
      PROPERTIES += a
      a
    }

    // used to be called "discreteAttributeOf"
    def apply(f: T => String)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit, d5: DummyImplicit): DiscreteAttribute[T] = {
      val a = new DiscreteAttribute[T](name, f, None)
      PROPERTIES += a
      a
    }

    // used to be called "discreteAttributesArrayOf", and "discreteAttributesGeneratorOf"
    def apply(f: T => List[String])(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit, d5: DummyImplicit, d6: DummyImplicit): DiscreteCollectionAttribute[T] = {
      val a = if (ordered) {
        new DiscreteCollectionAttribute[T](name, f, ordered = false)
      } else {
        new DiscreteCollectionAttribute[T](name, f, ordered = true)
      }
      PROPERTIES += a
      a
    }

    // used to be called "rangedDiscreteAttributeOf"
    def apply(range: String*)(f: T => String)(implicit tag: ClassTag[T], d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit,
      d4: DummyImplicit, d5: DummyImplicit, d6: DummyImplicit,
      d7: DummyImplicit): DiscreteAttribute[T] = {
      val r = range.toList
      val a = new DiscreteAttribute[T](name, f, Some(r))
      PROPERTIES += a
      a
    }
  }
  def property[T <: AnyRef](name: String) = new PropertyApply[T](name)
  def property[T <: AnyRef](name: String, ordered: Boolean) = new PropertyApply[T](name, ordered)
}

