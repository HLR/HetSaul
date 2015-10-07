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
