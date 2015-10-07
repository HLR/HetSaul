package edu.illinois.cs.cogcomp.saulexamples.nlp

import edu.illinois.cs.cogcomp.annotation.AnnotatorService
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Sentence, TextAnnotation }
import edu.illinois.cs.cogcomp.saulexamples.data.Document

import scala.collection.JavaConversions._
/** Created by Parisa on 9/10/15.
  */
object sensors {

  def getText(x:Document):String = {
    val l = x.getWords()
    val lx = l.mkString(" ")
    lx
  }


  def textCollection(x: List[Document]): List[String] = {
    var l1: List[String] = List[String]()
    for (x1 <- x) {
      val l = x1.getWords
      val lx = l.mkString(" ")
      l1 = lx :: l1
      print(lx)
    }
    l1
  }

  def processDocumentWith(annotatorService: AnnotatorService, cid: String, did: String, text: String, services: String*): TextAnnotation = {

    val ta = annotatorService.createBasicTextAnnotation(cid, did, text)
    // annotatorService.addView(ta, ViewNames.POS)
    println(ta.getAvailableViews)
    //The following lines can be used to get classes of various views from TextAnnotation
    //annotatorService.addView(ta, ViewNames.SHALLOW_PARSE)
    //val chunks2: SpanLabelView = ta.getView(ViewNames.SHALLOW_PARSE).asInstanceOf[SpanLabelView]
    //val parse2: SpanLabelView= ta.getView(ViewNames.NER).asInstanceOf[SpanLabelView]
    //val Pos1: TokenLabelView =ta.getView(ViewNames.POS).asInstanceOf[TokenLabelView]
    //Add views we need
    ta
  }

  def f(x: TextAnnotation): List[Sentence] = x.sentences().toList
  def alignment(x: TextAnnotation, y: Sentence): Boolean = x.getId == y.getSentenceConstituent.getTextAnnotation.getId
  def f2(x: TextAnnotation): List[Constituent] = x.getView(ViewNames.POS).getConstituents.toList
}
