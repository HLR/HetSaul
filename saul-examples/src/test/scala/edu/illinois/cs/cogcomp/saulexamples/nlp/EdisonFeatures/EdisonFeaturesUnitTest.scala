/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors
import org.scalatest._

import scala.collection.JavaConversions._

class EdisonFeaturesUnitTest extends FlatSpec with Matchers {

  val documentList: List[TextAnnotation] = toyDataGenerator.generateToyTextAnnotation(3)

  val sentenceList = documentList.flatMap(_.sentences())

  val constituentList = sentenceList.map(_.getSentenceConstituent)

  import edisonDataModel._

  /** instantiating nodes */
  documents.populate(documentList)

  sentences.populate(sentenceList)

  constituents.populate(constituentList)

  /** instantiating edges */
  docToSen.populateWith(CommonSensors.textAnnotationSentenceAlignment _)

  senToCons.populateWith(CommonSensors.sentenceConstituentAlignment _)

  docToCons.populateWith(CommonSensors.textAnnotationConstituentAlignment _)

  "querying on `Documents`, `TextAnnotation` and `Constituents`" should " work" in {

    /** query edges */
    val sentencesQueriedFromDocs = docToSen.forward.neighborsOf(documentList.head)

    val docsQueriedFromSentences = docToSen.backward.neighborsOf(sentenceList.head)

    val consQueriesFromSentences = senToCons.forward.neighborsOf(sentenceList.head)

    val sentencesQueriesFromCons = senToCons.backward.neighborsOf(constituentList.head)

    val consQueriesFromDocs = docToCons.forward.neighborsOf(documentList.head)

    val docsQueriesFromCons = docToCons.backward.neighborsOf(constituentList.head)

    sentencesQueriedFromDocs.map(_.toString).toSet should be(consQueriesFromDocs.map(_.toString).toSet)

    sentencesQueriesFromCons.map(_.toString).toSet should be(consQueriesFromSentences.map(_.toString).toSet)

    docsQueriedFromSentences.map(_.toString).toSet should be(docsQueriesFromCons.map(_.toString).toSet)

    /** query properties */
    val sentenceContentFromDoc = docToSen.to().prop(sentenceContent)

    val sentencesContentFromCons = senToCons.from().prop(sentenceContent)

    val docContentFromSentence = docToSen.from().prop(documentContent)

    val docContentFromCons = docToCons.from().prop(documentContent)

    val consContentFromSentence = senToCons.to().prop(constituentContent)

    val consContentFromDocs = docToCons.to().prop(constituentContent)

    sentenceContentFromDoc.toSet should be(sentencesContentFromCons.toSet)
    docContentFromSentence.toSet should be(docContentFromCons.toSet)
    consContentFromSentence.toSet should be(consContentFromDocs.toSet)
  }
}

