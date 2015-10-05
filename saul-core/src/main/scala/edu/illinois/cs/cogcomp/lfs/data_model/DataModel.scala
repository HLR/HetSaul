package edu.illinois.cs.cogcomp.lfs.data_model

import edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.discrete.{ BooleanAttribute, DiscreteArrayAttribute, DiscreteAttribute, DiscreteGenAttribute }
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.real.{ RealArrayAttribute, RealAttribute, RealGenAttribute }
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.{ Attribute, EvaluatedAttribute }
import edu.illinois.cs.cogcomp.lfs.data_model.node.Node
import edu.illinois.cs.cogcomp.lfs.data_model.edge.Edge

import scala.collection.mutable.{ Map => MutableMap }
import scala.reflect.ClassTag

trait DataModel {
  val PID = 'PID

  val NODES: List[Node[_]]
  val PROPERTIES: List[Attribute[_]]
  val EDGES: List[Edge[_, _]]

  // TODO: comment this function
  def getType[T <: AnyRef](implicit tag: ClassTag[T]): ClassTag[T] = tag

  // TODO: implement this: retrieve all data with type T.
  // This will equals to select all where type is T
  def getInstancesWithType[T <: AnyRef](implicit tag: ClassTag[T]): Iterable[T] = {
    this.getNodeWithType[T].getAllInstances
  }

  // TODO: Implement this function.
  def select[T <: AnyRef](conditions: EvaluatedAttribute[T, _]*)(implicit tag: ClassTag[T]): List[T] = {
    val conds = conditions.toList
    this.getNodeWithType[T].getAllInstances.filter({
      t =>
        conds.exists({
          cond => cond.att.mapping(t).equals(cond.value)
        })
    }).toList
  }

  // TODO: keep one of the following three functions
  def getAllAttributesOf[T <: AnyRef](implicit tag: ClassTag[T]): List[Attribute[T]] = {
    // TODO: implement this
    getAllFeatures[T]
  }

  def getAllFeatures[T](implicit tag: ClassTag[T]): List[Attribute[T]] = {
    this.PROPERTIES.filter(_.tag.equals(tag)).map(_.asInstanceOf[Attribute[T]])
  }

  // TODO: create lbj feature classifier.
  def getFeaturesOf[T](implicit tag: ClassTag[T]): List[Attribute[T]] = {
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

  def populate[T <: AnyRef](coll: Seq[T])(implicit tag: ClassTag[T]) = {
    this.getNodeWithType[T] ++ coll
  }

  def populateWith[FROM <: AnyRef, TO <: AnyRef](sensor: FROM => List[TO], edgeKeyName: Symbol)(implicit tagF: ClassTag[FROM], tagT: ClassTag[TO]) = {
    //TODO send a sensor with FROM => TO and check whether the TO is a List or not in here.
    val toNode = this.getNodeWithType[TO]
    val fromInstances = this.getInstancesWithType[FROM]
    fromInstances.foreach { instance =>
      val manyInstances = sensor(instance)
      val newSecondaryKeyMappingsList = manyInstances.map(x => edgeKeyName -> ((x: TO) => instance.hashCode().toString))
      newSecondaryKeyMappingsList.foreach(secondaryKeyMapping => toNode.secondaryKeyMap += secondaryKeyMapping)
      this populate manyInstances
    }
  }

  def populateWith[FROM <: AnyRef, TO <: AnyRef](manyInstances: List[TO], sensor: (FROM, TO) => Boolean, edgeKeyName: Symbol)(implicit tagF: ClassTag[FROM], tagT: ClassTag[TO]) = {
    val toNode = this.getNodeWithType[TO]
    val fromInstances = this.getInstancesWithType[FROM]
    var temp = manyInstances
    fromInstances.foreach { instance =>
      var twoLists = temp.partition(sensor(instance, _))
      val matching = twoLists._1
      val unmatching = twoLists._2

      val newSecondaryKeyMappingsList = matching.map(x => edgeKeyName -> ((x: TO) => instance.hashCode().toString))
      newSecondaryKeyMappingsList.foreach { secondaryKeyMapping => toNode.secondaryKeyMap += secondaryKeyMapping }
      this populate matching
      temp = unmatching
    }
  }

  def getNodeWithType[T <: AnyRef](implicit tag: ClassTag[T]): Node[T] = {
    this.NODES.filter {
      e: Node[_] =>
        {
          tag.equals(e.tag)
        }
    }.head.asInstanceOf[Node[T]]
  }

  def getNodesWithTypeTag(tag: ClassTag[_]): Node[_] = {
    this.NODES.filter {
      e: Node[_] =>
        {
          tag.equals(e.tag)
        }
    }.head
  }

  def get[T <: AnyRef](pi: String)(implicit tag: ClassTag[T]): T = {
    this.getNodeWithType[T].getInstanceWithPrimaryKey(pi)
  }

  // TODO: comment this function
  def getFromRelation[FROM <: AnyRef, NEED <: AnyRef](t: FROM)(implicit tag: ClassTag[FROM], headTag: ClassTag[NEED]): List[NEED] = {
    val dm = this
    if (tag.equals(headTag)) {
      List(t.asInstanceOf[NEED])
    } else {
      val r = this.EDGES.filter {
        r => r.tagT.toString.equals(tag.toString) && r.tagU.toString.equals(headTag.toString)
      }
      if (r.isEmpty) {
        throw new Exception(s"Failed to found relations between $tag to $headTag")
      } else {

        if (r.size == 1) {
          r.head.asInstanceOf[Edge[FROM, NEED]].retrieveFromDataModel(dm, t)
        } else {
          val ret = r flatMap (_.asInstanceOf[Edge[FROM, NEED]].retrieveFromDataModel(dm, t))
          ret
        }
      }
    }
  }

  // TODO: comment this function
  def getFromRelation[T <: AnyRef, HEAD <: AnyRef](name: Symbol, t: T)(implicit tag: ClassTag[T], headTag: ClassTag[HEAD]): List[HEAD] = {
    if (tag.equals(headTag)) {
      List(t.asInstanceOf[HEAD])
    } else {
      val r = this.EDGES.filter {
        r =>
          r.tagT.equals(tag) && r.tagU.equals(headTag) && (if (r.nameOfRelation.isDefined) {
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
        r.head.asInstanceOf[Edge[T, HEAD]].retrieveFromDataModel(this, t)
      }
    }
  }

  // TODO: rename this function with a better name
  def getRelatedFieldsBetween[T, U](implicit tag: ClassTag[T], headTag: ClassTag[U]): List[Symbol] = {
    this.EDGES.filter(r => r.tagT.equals(tag) && r.tagU.equals(headTag)) match {
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
    getNodeWithType[T].addToTest(coll)
  }

  /** edges */
  def edge[ONE <: AnyRef, MANY <: AnyRef](name: Symbol)(implicit ptag: ClassTag[ONE], ctag: ClassTag[MANY]): List[Edge[_, _]] = {
    val ss = List(('PID, name)) //when the edge is created the list of matching symbols for the identifiers also is created.
    //now this should be added to the definition of the entities that are related by this edge.
    val ptoc = new Edge[ONE, MANY](ss.toList, Some(name))
    val reverted = ss.toList.map(v => (v._2, v._1)).toList
    val ctop = new Edge[MANY, ONE](reverted, Some(name))
    ptoc :: ctop :: Nil
  }
}

object DataModel {

  /** node definitions */
  def node[T <: AnyRef](implicit tag: ClassTag[T]): Node[T] = {
    node[T](PrimaryKey = { t: T => String.valueOf(t.hashCode()) })
  }

  def node[T <: AnyRef](PrimaryKey: T => String)(implicit tag: ClassTag[T]): Node[T] = {
    node(PrimaryKey, MutableMap.empty[Symbol, T => String])
  }

  def node[T <: AnyRef](PrimaryKey: T => String, SecondaryKeyMap: MutableMap[Symbol, T => String])(implicit tag: ClassTag[T]): Node[T] = {
    val combinedSecondaryKeyMap = SecondaryKeyMap + ('PID -> ((t: T) => PrimaryKey(t)))
    new Node[T](PrimaryKey, combinedSecondaryKeyMap, tag, null)
  }

  def node[T <: AnyRef](PrimaryKey: T => String, SecondaryKeyMap: MutableMap[Symbol, T => String], Address: T => AnyRef)(implicit tag: ClassTag[T]): Node[T] = {
    val combinedSecondaryKeyMap = SecondaryKeyMap + ('PID -> ((t: T) => PrimaryKey(t)))
    new Node[T](PrimaryKey, combinedSecondaryKeyMap, tag, Address)
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
    new DiscreteAttribute[T](name.toString, f, None)
  }

  /** Discrete feature with range, same as discrete{"spam", "ham"} SpamLabel in lbjava */
  def rangedDiscreteAttributeOf[T <: AnyRef](name: Symbol)(range: String*)(f: T => String)(implicit tag: ClassTag[T]): DiscreteAttribute[T] = {
    val r = range.toList
    new DiscreteAttribute[T](name.toString, f, Some(r))
  }

  /** Discrete array feature with range, same as discrete[] SpamLabel in lbjava */
  def discreteAttributesArrayOf[T <: AnyRef](name: Symbol)(f: T => List[String])(implicit tag: ClassTag[T]): DiscreteArrayAttribute[T] = {
    new DiscreteArrayAttribute[T](name.toString, f, None)
  }

  /** Discrete sensor feature with range, same as discrete% name in lbjava */
  def discreteAttributesGeneratorOf[T <: AnyRef](name: Symbol)(f: T => List[String])(implicit tag: ClassTag[T]): DiscreteGenAttribute[T] = {
    new DiscreteGenAttribute[T](name.toString, f)
  }

  /** Discrete sensor feature with range, same as real name in lbjava */
  def realAttributeOf[T <: AnyRef](name: Symbol)(f: T => Double)(implicit tag: ClassTag[T]): RealAttribute[T] = {
    new RealAttribute[T](name.toString, f)
  }

  /** Discrete sensor feature with range, same as real[] name in lbjava */
  def realAttributesArrayOf[T <: AnyRef](name: Symbol)(f: T => List[Double])(implicit tag: ClassTag[T]): RealArrayAttribute[T] = {
    new RealArrayAttribute[T](name.toString, f)
  }

  /** Discrete sensor feature with range, same as real% name in lbjava */
  def realAttributesGeneratorOf[T <: AnyRef](name: Symbol)(f: T => List[Double])(implicit tag: ClassTag[T]): RealGenAttribute[T] = {
    new RealGenAttribute[T](name.toString, f)
  }

  /** Discrete sensor feature with range, same as real name in lbjava */
  def intAttributeOf[T <: AnyRef](name: Symbol)(f: T => Int)(implicit tag: ClassTag[T]): RealAttribute[T] = {
    val newf: T => Double = { t => f(t).toDouble }
    new RealAttribute[T](name.toString, newf)
  }

  /** Discrete sensor feature with range, same as real[] name in lbjava */
  def intAttributesArrayOf[T <: AnyRef](name: Symbol)(f: T => List[Int])(implicit tag: ClassTag[T]): RealArrayAttribute[T] = {
    val newf: T => List[Double] = { t => f(t).map(_.toDouble) }
    new RealArrayAttribute[T](name.toString, newf)
  }

  /** Discrete sensor feature with range, same as real% name in lbjava */
  def intAttributesGeneratorOf[T <: AnyRef](name: Symbol)(f: T => List[Int])(implicit tag: ClassTag[T]): RealGenAttribute[T] = {
    val newf: T => List[Double] = { t => f(t).map(_.toDouble) }
    new RealGenAttribute[T](name.toString, newf)
  }

  def booleanAttributeOf[T <: AnyRef](name: Symbol)(f: T => Boolean)(implicit tag: ClassTag[T]): BooleanAttribute[T] = {
    new BooleanAttribute[T](name.toString, f)
  }
}