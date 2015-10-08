package edu.illinois.cs.cogcomp.saul.datamodel.edge

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

import scala.reflect.ClassTag

class Edge[FROM <: AnyRef, TO <: AnyRef](
  val from: Node[FROM], val to: Node[TO],
  val matchesList: List[(Symbol, Symbol)],
  val nameOfRelation: Option[Symbol]
) {
  def retrieveFromDataModel(dm: DataModel, t: FROM): List[TO] = {
    val listOfCandidatePrimaryKeySets = matchesList.map {
      case (secondaryKeyOfFrom, secondaryKeyOfTo) => {
        val v = from.secondaryKeyFunction(t).get(secondaryKeyOfFrom) match {
          case Some(va) =>
            va
          case _ => throw new Exception("Secondary Key not found for " + secondaryKeyOfFrom)
        }
        to.getPrimaryKeyGivenSecondaryKey(secondaryKeyOfTo, v).toSet
      }
    }

    val candidatePrimaryKeySet = listOfCandidatePrimaryKeySets.reduce(_ intersect _)

    // get list of nodes
    candidatePrimaryKeySet.map { candidatePI => to.getInstanceWithPrimaryKey(candidatePI) }.toList
  }
}

case class Link[T <: AnyRef, U <: AnyRef](forward: Edge[T, U], backward: Edge[U, T]) {
  def populateWith(sensor: (T) => List[U]) = {
    val edge = forward
    val fromInstances = edge.from.getAllInstances
    fromInstances.foreach {
      fromInstance =>
        val toInstance_s = sensor(fromInstance)
        val newSecondaryKeyMappingsList = toInstance_s.map(x => edge.nameOfRelation.get -> ((x: U) => fromInstance.hashCode().toString))
        newSecondaryKeyMappingsList.foreach(secondaryKeyMapping => edge.to.secondaryKeyMap += secondaryKeyMapping)
        edge.to.populate(toInstance_s)
    }
  }

  def populateWith(sensor: (T) => U)(implicit d: DummyImplicit): Unit = {
    populateWith((f: T) => List(sensor(f)))
  }

  def populateWith(sensor: (T) => Option[U])(implicit d1: DummyImplicit, d2: DummyImplicit): Unit = {
    populateWith((f: T) => sensor(f).toList)
  }

  def populateWith(manyInstances: List[U], sensor: (T, U) => Boolean) = {
    val edge = forward
    val fromInstances = edge.from.getAllInstances
    var temp = manyInstances
    fromInstances.foreach { instance =>
      val twoLists = temp.partition(sensor(instance, _))
      val matching = twoLists._1
      val unmatching = twoLists._2

      val newSecondaryKeyMappingsList = matching.map(x => edge.nameOfRelation.get -> ((x: U) => instance.hashCode().toString))
      newSecondaryKeyMappingsList.foreach { secondaryKeyMapping => edge.to.secondaryKeyMap += secondaryKeyMapping }
      edge.to.populate(matching)
      temp = unmatching
    }
  }
}
