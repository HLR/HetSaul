package edu.illinois.cs.cogcomp.lfs.util

import scala.reflect.ClassTag

class ListNodeInterface[T <: AnyRef](val l : List[T])(implicit val tag: ClassTag[T]) {

//  def toNode: Node[T] = {
//    toNode(PrimaryKey = { t: T => String.valueOf(t.hashCode())})
//  }
//
//  def toNode(PrimaryKey: T => String)(implicit tag: ClassTag[T]): Node[T] = {
//    toNode(PrimaryKey, t => HashMap[Symbol, String]())
//  }
//
//  def toNode(PrimaryKey: T => String, SecondaryKey: T => Map[Symbol, String])(implicit tag: ClassTag[T]): Node[T] = {
//    val combinedKeyFunction: T => Map[Symbol, String] = {
//      t: T => SecondaryKey(t) + ('PID -> PrimaryKey(t))
//    }
//    new Node[T](PrimaryKey, combinedKeyFunction, tag, null) // TODO: fix this null
//  }
}