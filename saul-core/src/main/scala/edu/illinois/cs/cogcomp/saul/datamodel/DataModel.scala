package edu.illinois.cs.cogcomp.saul.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete.{ BooleanAttribute, DiscreteArrayAttribute, DiscreteAttribute, DiscreteGenAttribute }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.real.{ RealArrayAttribute, RealAttribute, RealGenAttribute }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.{ Attribute, EvaluatedAttribute }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.edge.{ Edge, Link }

import scala.collection.mutable.{ Map => MutableMap, ListBuffer }
import scala.reflect.ClassTag

trait DataModel {
  val PID = 'PID

  final val NODES = new ListBuffer[Node[_]]
  final val PROPERTIES = new ListBuffer[Attribute[_]]
  final val LINKS = new ListBuffer[Link[_, _]]

  // TODO: Implement this function.
  def select[T <: AnyRef](node: Node[T], conditions: EvaluatedAttribute[T, _]*): List[T] = {
    val conds = conditions.toList
    node.getAllInstances.filter({
      t =>
        conds.exists({
          cond => cond.att.mapping(t).equals(cond.value)
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

  /** Functions for internal usages.
    */
  def getAllAttributeOf[T <: AnyRef](implicit tag: ClassTag[T]): List[Attribute[T]] = {
    this.PROPERTIES.filter(a => a.tag.equals(tag)).map(_.asInstanceOf[Attribute[T]]).toList
  }

  // TODO: remove this/or make it more understandable
  def ~~(es: Node[_]*): List[Node[_]] = es.toList

  //def flatList(es: Attribute[_]*): List[Attribute[_]] = es.toList

  // def flatList(es: List[Edge[_, _]]*): List[Edge[_, _]] = es.toList.flatten

  @deprecated("Use node.populate() instead.")
  def populate[T <: AnyRef](node: Node[T], coll: Seq[T]) = {
    node populate coll
  }

  @deprecated
  def getNodeWithType[T <: AnyRef](implicit tag: ClassTag[T]): Node[T] = {
    this.NODES.filter {
      e: Node[_] =>
        {
          tag.equals(e.tag)
        }
    }.head.asInstanceOf[Node[T]]
  }

  // TODO: comment this function
  @deprecated
  def getFromRelation[FROM <: AnyRef, NEED <: AnyRef](t: FROM)(implicit tag: ClassTag[FROM], headTag: ClassTag[NEED]): Seq[NEED] = {
    val dm = this
    if (tag.equals(headTag)) {
      List(t.asInstanceOf[NEED])
    } else {
      val r = this.LINKS.filter {
        r => r.from.tag.toString.equals(tag.toString) && r.to.tag.toString.equals(headTag.toString)
      }
      if (r.isEmpty) {
        throw new Exception(s"Failed to found relations between $tag to $headTag")
      } else {

        if (r.size == 1) {
          r.head.asInstanceOf[Link[FROM, NEED]].retrieveFromDataModel(dm, t)
        } else {
          val ret = r flatMap (_.asInstanceOf[Link[FROM, NEED]].retrieveFromDataModel(dm, t))
          ret
        }
      }
    }
  }

  // TODO: comment this function
  @deprecated
  def getFromRelation[T <: AnyRef, HEAD <: AnyRef](name: Symbol, t: T)(implicit tag: ClassTag[T], headTag: ClassTag[HEAD]): List[HEAD] = {
    if (tag.equals(headTag)) {
      List(t.asInstanceOf[HEAD])
    } else {
      val r = this.LINKS.filter {
        r =>
          r.to.tag.equals(tag) && r.from.tag.equals(headTag) && (if (r.nameOfRelation.isDefined) {
            name.equals(r.nameOfRelation.get)
          } else {
            false
          })
      }

      // there must be only one such relation
      if (r.isEmpty) {
        throw new Exception(s"Failed to find any relation between $tag to $headTag")
      } else if (r.size > 1) {
        throw new Exception(s"Found too many relations between $tag to $headTag,\nPlease specify a name")
      } else {
        r.head.asInstanceOf[Link[T, HEAD]].retrieveFromDataModel(this, t)
      }
    }
  }

  // TODO: rename this function with a better name
  @deprecated
  def getRelatedFieldsBetween[T, U](implicit tag: ClassTag[T], headTag: ClassTag[U]): List[Symbol] = {
    this.LINKS.filter(r => r.to.tag.equals(tag) && r.from.tag.equals(headTag)).toList match {
      case x :: xs => x.matchesList.map(_._1)
      case Nil => Nil
    }
  }

  // TODO: complete this (after figuring our what it is supposed to do!)
  def childrenOf[T <: AnyRef, U <: AnyRef](t: T): List[U] = {
    Nil
  }

  // TODO: remove this
  def testWith[T <: AnyRef](coll: Seq[T])(implicit tag: ClassTag[T]) = {
    println("Adding for type" + tag.toString)
    //getNodeWithType[T].addToTest(coll)
  }

  /** node definitions */
  def node[T <: AnyRef](implicit tag: ClassTag[T]): Node[T] = {
    node[T](PrimaryKey = { t: T => String.valueOf(t.hashCode()) })
  }

  def node[T <: AnyRef](PrimaryKey: T => String)(implicit tag: ClassTag[T]): Node[T] = {
    node(PrimaryKey, MutableMap.empty[Symbol, T => String])
  }

  def node[T <: AnyRef](PrimaryKey: T => String, SecondaryKeyMap: MutableMap[Symbol, T => String])(implicit tag: ClassTag[T]): Node[T] = {
    val combinedSecondaryKeyMap = SecondaryKeyMap + ('PID -> ((t: T) => PrimaryKey(t)))
    val n = new Node[T](PrimaryKey, combinedSecondaryKeyMap, tag, null)
    NODES += n
    n
  }

  def node[T <: AnyRef](PrimaryKey: T => String, SecondaryKeyMap: MutableMap[Symbol, T => String], Address: T => AnyRef)(implicit tag: ClassTag[T]): Node[T] = {
    val combinedSecondaryKeyMap = SecondaryKeyMap + ('PID -> ((t: T) => PrimaryKey(t)))
    val n = new Node[T](PrimaryKey, combinedSecondaryKeyMap, tag, Address)
    NODES += n
    n
  }

  /** edges */
  def edge[ONE <: AnyRef, MANY <: AnyRef](one: Node[ONE], many: Node[MANY], name: Symbol): Edge[ONE, MANY] = {
    val ss = List(('PID, name)) //when the edge is created the list of matching symbols for the identifiers also is created.
    //now this should be added to the definition of the entities that are related by this edge.
    val ptoc = new Link[ONE, MANY](one, many, ss.toList, Some(name))
    val reverted = ss.toList.map(v => (v._2, v._1)).toList
    val ctop = new Link[MANY, ONE](many, one, reverted, Some(name))
    LINKS += ptoc
    LINKS += ctop
    Edge(ptoc, ctop)
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
    val a = new DiscreteArrayAttribute[T](name.toString, f, None)
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
}
