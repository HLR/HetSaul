package edu.illinois.cs.cogcomp.saul.datamodel.node

import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete.DiscreteAttribute

import scala.collection.mutable.{ Map => MutableMap }
import scala.reflect.ClassTag

/** A Node E is an instances of base types T.
  */
class Node[T <: AnyRef](
  val primaryKeyFunction: T => String,
  var secondaryKeyMap: MutableMap[Symbol, T => String],
  val tag: ClassTag[T],
  val address: T => AnyRef
) {

  var secondaryKeyFunction = (t: T) => secondaryKeyMap.mapValues(f => f(t))

  def getTrainingInstances: Iterable[T] = this.trainingSet map {
    primaryKey: String => this.collections.get(primaryKey).get
  }

  def getAllInstances: Iterable[T] = this.collections.map({
    case (s, t) => t
  })

  def getTestingInstances: Iterable[T] = this.testingSet map {
    primaryKey: String => this.collections.get(primaryKey).get
  }

  // TODO: remove these 
  val trainingSet: scala.collection.mutable.Set[String] = scala.collection.mutable.HashSet[String]()
  val testingSet: scala.collection.mutable.Set[String] = scala.collection.mutable.HashSet[String]()

  private val orderingMap: scala.collection.mutable.Map[Int, T] = scala.collection.mutable.Map[Int, T]()
  private val reverseOrderingMap: scala.collection.mutable.Map[T, Int] = scala.collection.mutable.Map[T, Int]()

  /** Maps from (PrimaryID => T)
    */
  private val collections = scala.collection.mutable.Map[String, T]()

  /** Maps of (NameOfSecondaryID => ValueOfSecondary => PrimaryID)
    */
  private val indices = scala.collection.mutable.Map[Symbol, scala.collection.mutable.Map[String, scala.collection.mutable.MutableList[String]]]()

  def getInstanceWithPrimaryKey(s: String): T = this.collections.get(s) match {
    case Some(t) => t
    case _ => throw new Exception("Element not found")
  }

  /** Auther Parisa
    * This is supposed to be the physical address with a flexible type depending on the
    * applcation domain.
    * TODO: explain more why/where this might be helpful.
    */
  def getAddresswithPrimaryKey(x: T): AnyRef = this.collections.get(primaryKeyFunction(x)) match {
    case Some(t) => this.address
    case _ => throw new Exception("Element not found")
  }

  // TODO: add doocumentation to this method
  def filterNode(attribute: DiscreteAttribute[T], value: String): Node[T] = {
    val node = new Node[T](primaryKeyFunction, this.secondaryKeyMap, tag, address)
    node populate collections.values.filter {
      attribute.mapping(_) == value
    }.toSeq
    node
  }

  def getWittSecondaryKey(key: Symbol, value: String): List[T] = this.getPrimaryKeyGivenSecondaryKey(key, value).map {
    this.getInstanceWithPrimaryKey(_)
  }

  def getPrimaryKeyGivenSecondaryKey(key: Symbol, value: String): List[String] = this.indices.get(key) match {
    case Some(dict) => dict.get(value) match {
      case Some(t) => t.toList
      case _ =>
        //        println(s"Can;t found ${tag}")
        //        println(s"Can;t found ${key} => ${value}")
        //        throw new Exception("Element not found")
        Nil
    }
    case _ => throw new Exception("Element not found")
  }

  var count = 0

  def incrementCount(): Int = this.synchronized {
    val ret = count
    count = count + 1
    ret
  }

  def decreaseCount(): Int = this.synchronized {
    val ret = count
    count = count - 1
    ret
  }

  /** Operator for adding a sequence of T into my table.
    */
  def populate(ts: Seq[T]) = {
    ts foreach {
      t =>
        {
          val order = incrementCount()
          val primaryKey = primaryKeyFunction(t)

          this.trainingSet += primaryKey
          this.collections += (primaryKey -> t)
          secondaryKeyFunction(t) foreach {
            case (secondaryKey, value) =>
              val secondaries = this.indices.getOrElseUpdate(secondaryKey, scala.collection.mutable.Map[String, scala.collection.mutable.MutableList[String]]())
              secondaries.getOrElseUpdate(value, new scala.collection.mutable.MutableList[String]()) += primaryKey
          }
          this.orderingMap += (order -> t)
          this.reverseOrderingMap += (t -> order)
        }
    }
  }

  def addToTest(ts: Seq[T]) = {
    ts foreach {
      t =>
        {
          val order = incrementCount()
          val primaryKey = primaryKeyFunction(t)

          this.testingSet += primaryKey
          this.collections += (primaryKey -> t)

          secondaryKeyFunction(t) foreach {
            case (secondaryKey, value) => {
              val secondaries = this.indices.getOrElseUpdate(secondaryKey, scala.collection.mutable.Map[String, scala.collection.mutable.MutableList[String]]())
              secondaries.getOrElseUpdate(value, new scala.collection.mutable.MutableList[String]()) += primaryKey
            }
          }
          this.orderingMap += (order -> t)
          this.reverseOrderingMap += (t -> order)
        }
    }
  }

  /** Relational operators
    */
  val nodeOfTypeT = this

  def join[U <: AnyRef](nodeOfTypeU: Node[U]) = new {
    def on(matchesList: (Symbol, Symbol)*): List[(T, U)] = // new LBPList[(T,U)]()
      {
        collections.flatMap {
          case (primaryKey, t) => {
            val listOfCandidates = matchesList.toList.map {
              case (secondaryKeyOfT, secondaryKeyOfU) => {
                val v = nodeOfTypeT.secondaryKeyFunction(t).get(secondaryKeyOfT) match {
                  case Some(v) => v
                  case _ => throw new Exception("Secondary Key not found for " + secondaryKeyOfT)
                }
                nodeOfTypeU.getPrimaryKeyGivenSecondaryKey(secondaryKeyOfU, v).toSet
              }
            }
            listOfCandidates.reduce(_ intersect _).map(primaryKey => (t, nodeOfTypeU.getInstanceWithPrimaryKey(primaryKey)))
          }
        }.toList
      }
  }

  def getWithRelativePosition(t: T, relativePos: Int): Option[T] = {
    getWithRelativePosition(t, relativePos, Nil)
  }

  def getWithRelativePosition(t: T, relativePos: Int, filters: List[Symbol]): Option[T] = {
    if (relativePos == 0) {
      Some(t)
    } else {
      // relative <> 0

      this.reverseOrderingMap.get(t) match {
        case Some(ord) => {
          val targetOrd = ord + relativePos
          this.orderingMap.get(targetOrd) match {
            case Some(x) => {
              if (underSameParent(t, x, filters)) {
                Some(x)
              } else {
                None
              }
            }
            case None => None
          }

        }
        case _ => None
      }
    }
  }

  def nextOf(t: T, filters: List[Symbol]): Option[T] = {
    getWithWindow(t, 1, 1, Nil) match {
      case head :: more => head
      case Nil => None
    }
  }

  def pervOf(t: T, filters: List[Symbol]): Option[T] = {
    getWithWindow(t, -1, -1, Nil) match {
      case head :: more => head
      case Nil => None
    }
  }

  // TODO: add documentation to the following methods
  def getWithWindow(t: T, before: Int, after: Int): List[Option[T]] = {
    getWithWindow(t, before, after, Nil)
  }

  def getWithWindow(t: T, before: Int, after: Int, filterSym: Symbol): List[Option[T]] = {
    getWithWindow(t, before, after, filterSym :: Nil)
  }

  def underSameParent(t: T, x: T, filters: List[Symbol]): Boolean = {
    val indexMapOfT = this.secondaryKeyFunction(t).filterKeys(filters.contains)
    val indexMapOfX = this.secondaryKeyFunction(x).filterKeys(filters.contains)

    val existNotMatch = filters.exists({
      // exist key k, s.t
      // the value of k on t and x are different
      key =>
        {
          val keyOnT = indexMapOfT(key)
          val keyOnX = indexMapOfX(key)
          keyOnT != keyOnX
        }
    })
    !existNotMatch
  }

  def between(t1: T, t2: T, filter: List[Symbol]): List[Option[T]] = {
    val wildCard = !underSameParent(t1, t2, filter)
    // If t1 and t2 are not under same parents, then we ignore the parent.
    (this.reverseOrderingMap.get(t1), this.reverseOrderingMap.get(t2)) match {
      case (Some(start), Some(end)) => {
        (start to end) map {
          position =>
            this.orderingMap.get(position) match {
              case Some(v) => if (wildCard || underSameParent(t1, v, filter)) {
                Some(v)
              } else {
                None
              }
              case None => None
            }
        }
      } toList
      case _ => throw new Exception("Can't find element.")
    }
  }

  def getWithWindow(t: T, before: Int, after: Int, filters: List[Symbol]): List[Option[T]] = {
    this.reverseOrderingMap.get(t) match {
      case Some(myOrder) => {
        val start = myOrder + before
        val end = myOrder + after
        val indexMapOfT = this.secondaryKeyFunction(t).filterKeys(filters.contains)
        val result = (start to end).flatMap(this.orderingMap.get).filter {
          x =>
            {
              val indexMapOfX = this.secondaryKeyFunction(x).filterKeys(filters.contains)
              // All secondary keys in filters list mush agree with t.
              val existNotMatch = filters.exists({
                // exist key k, s.t
                // the value of k on t and x are different
                key =>
                  {
                    val keyOnT = indexMapOfT(key)
                    val keyOnX = indexMapOfX(key)
                    keyOnT != keyOnX
                  }
              })
              // if it exist such a value,
              // then we should discard it.
              // So since this function passed to a filter. we put a not! in front.
              !existNotMatch
            }
        }.toList

        val pos = result.indexOf(t)
        // Now we need to shift the list
        // before is negative, after is positive.
        val tShouldBeAt = -before
        if (tShouldBeAt < 0) {
          result map (Some(_))
        } else {

          val prependAmount = tShouldBeAt - pos
          val appendAmount = (after - before + 1) - (prependAmount + result.size)

          val toPrepend = Nil.padTo(prependAmount, None)
          val toAppend = Nil.padTo(appendAmount, None)

          val middle = result map (Some(_))
          toPrepend ::: middle ::: toAppend
        }
      }
      case _ => {
        throw new Exception("Can't found order of " + t.toString)
      }
    }
  }
}
