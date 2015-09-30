package edu.illinois.cs.cogcomp.lfs.data_model.edge

import edu.illinois.cs.cogcomp.lfs.data_model.DataModel

import scala.reflect.ClassTag

class Edge[FROM <: AnyRef, TO <: AnyRef](
  val matchesList: List[(Symbol, Symbol)],
  val nameOfRelation: Option[Symbol]
)(implicit val tagT: ClassTag[FROM], implicit val tagU: ClassTag[TO]) {
  def retrieveFromDataModel(dm: DataModel, t: FROM): List[TO] = {

    val nodeOfFrom = dm.getNodeWithType[FROM]
    val nodeOfTo = dm.getNodeWithType[TO]

    val listOfCandidatePrimaryKeySets = matchesList.map {
      case (secondaryKeyOfFrom, secondaryKeyOfTo) => {
        val v = nodeOfFrom.secondaryKeyFunction(t).get(secondaryKeyOfFrom) match {
          case Some(va) =>
            va
          case _ => throw new Exception("Secondary Key not found for " + secondaryKeyOfFrom)
        }
        nodeOfTo.getPrimaryKeyGivenSecondaryKey(secondaryKeyOfTo, v).toSet
      }
    }

    val candidatePrimaryKeySet = listOfCandidatePrimaryKeySets.reduce(_ intersect _)

    // get list of nodes
    candidatePrimaryKeySet.map { candidatePI => nodeOfTo.getInstanceWithPrimaryKey(candidatePI) }.toList
  }
}
