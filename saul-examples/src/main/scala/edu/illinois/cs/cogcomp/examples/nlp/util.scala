package edu.illinois.cs.cogcomp.examples

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Constituent, Sentence, TextAnnotation}
import edu.illinois.cs.cogcomp.tutorial_related.Document

import scala.collection.JavaConversions._
/** Created by Parisa on 9/10/15.
  */
object util {

  def textCollection(x: List[Document]): List[String] = {
    var l1: List[String] = List[String]()
    for (x1 <- x) {
      val l = x1.getWords()
      val lx = l.mkString(" ")
      l1 = lx :: l1
      print(lx)
    }
    l1
  }
  // def makeSymbol[T,U](f:T=>List[U])(x:T):(Symbol,Symbol)={
  //val u=node[T]
  // val u=f(x)
  // (Symbol(x.hashCode().toString),Symbol(u(1).hashCode().toString))
  //}

  def f(x: TextAnnotation): List[Sentence] = x.sentences().toList
  def alignment(x: TextAnnotation, y: Sentence): Boolean = x.getId == y.getSentenceConstituent.getTextAnnotation.getId
  def f2(x: TextAnnotation): List[Constituent] = x.getView(ViewNames.POS).getConstituents.toList
}
