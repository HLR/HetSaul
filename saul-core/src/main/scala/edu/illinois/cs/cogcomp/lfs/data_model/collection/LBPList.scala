package edu.illinois.cs.cogcomp.lfs.data_model.collection

import java.util

import edu.illinois.cs.cogcomp.lfs.data_model.DataModel
import edu.illinois.cs.cogcomp.lfs.data_model.node.Node

import scala.collection.immutable.LinearSeq
import scala.reflect.ClassTag

import scala.collection.JavaConversions._
/** Created by haowu on 2/2/15.
  */
class LBPList[T](implicit val tTag: ClassTag[T]) extends util.ArrayList[T] {

  def become[Z <: AnyRef](f: T => Z)(implicit tag: ClassTag[Z]): Node[Z] = {
    val coll = DataModel.node[Z]

    //    this.toList.map(f)

    null
  }

}
