package edu.illinois.cs.cogcomp.saulexamples.nlp

import edu.illinois.cs.cogcomp.annotation.AnnotatorService
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Sentence, TextAnnotation }
import edu.illinois.cs.cogcomp.core.utilities.ResourceManager
import edu.illinois.cs.cogcomp.curator.CuratorFactory
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.GazeteerReader
import edu.illinois.cs.cogcomp.saulexamples.data.Document

import scala.collection.JavaConversions._

/**  an object containing many popular sensors used in examples */
object sensors {

  def textCollection(x: List[Document]) = {
    x.map(documentContent)
  }

  def documentContent(x: Document): String = {
    x.getWords.mkString(" ")
  }

  def processDocumentWith(annotatorService: AnnotatorService, cid: String, did: String, text: String, services: String*): TextAnnotation = {
    val ta = annotatorService.createBasicTextAnnotation(cid, did, text)
    println(ta.getAvailableViews)
    ta
  }

  def getSentences(x: TextAnnotation): List[Sentence] = {
    x.sentences().toList
  }

  def sentenceTextAnnotationAlignment(ta: TextAnnotation, sentence: Sentence): Boolean = {
    ta.getId == sentence.getSentenceConstituent.getTextAnnotation.getId
  }

  def getConstituents(x: TextAnnotation): List[Constituent] = {
    x.getView(ViewNames.POS).getConstituents.toList
  }

  def annotateWithCurator(document: Document): TextAnnotation = {
    val config = "./saul-examples/config/caching-curator.properties"
    val rm = new ResourceManager(config)
    val annotatorService = CuratorFactory.buildCuratorClient(rm)
    val content = documentContent(document)
    processDocumentWith(annotatorService, "corpus", document.getGUID, content)
  }

  def cityGazetSensor: GazeteerReader = {
    new GazeteerReader("./data/EntityMentionRelation/known_city.lst", "Gaz:City", true)
  }

  def personGazetSensor: GazeteerReader = {
    val persongazet = new GazeteerReader("./data/EntityMentionRelation/known_maleFirst.lst", "Gaz:Person", true)
    persongazet.addFile("./data/EntityMentionRelation/known_femaleFirst.lst", true)
    persongazet
  }
}

