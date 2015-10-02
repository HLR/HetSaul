package edu.illinois.cs.cogcomp.examples.nlp.FeatureExamples

import edu.illinois.cs.cogcomp.annotation.AnnotatorService
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation

/** Created by Parisa on 9/17/15.
  */
object CogcompGiantSensor {

  def processDocumentWith(annotatorService: AnnotatorService, cid: String, did: String, text: String, services: String*): TextAnnotation = {

    val ta = annotatorService.createBasicTextAnnotation(cid, did, text)
    // annotatorService.addView(ta, ViewNames.POS)
    println(ta.getAvailableViews)

    annotatorService.addView(ta, ViewNames.SHALLOW_PARSE)

    //val chunks2: SpanLabelView = ta.getView(ViewNames.SHALLOW_PARSE).asInstanceOf[SpanLabelView]
    // val parse2: SpanLabelView= ta.getView(ViewNames.NER).asInstanceOf[SpanLabelView]
    //val Pos1: TokenLabelView =ta.getView(ViewNames.POS).asInstanceOf[TokenLabelView]
    // Add views we need
    ta
  }
}
