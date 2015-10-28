package edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation
import edu.illinois.cs.cogcomp.curator.CuratorFactory
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory
import edu.illinois.cs.cogcomp.nlp.utilities.BasicTextAnnotationBuilder
import edu.illinois.cs.cogcomp.saulexamples.data.{ Document, DocumentReader }
import edu.illinois.cs.cogcomp.saulexamples.nlp.commonSensors

import scala.collection.JavaConversions._

/** We populate the data and use features in the application below. */
object edisonApp {

  def main(args: Array[String]): Unit = {

    import edisonDataModel._

    val data: List[Document] = new DocumentReader("./data/20newsToy/train").docs.toList.slice(1, 3)

    /** this generates a list of strings each member is a textual content of a document */
    val documentIndexPair = commonSensors.textCollection(data).zip(data.map(_.getGUID))

    val documentList = documentIndexPair.map {
      case (doc, id) =>
        commonSensors.annotateRawWithCurator(doc, id)
      //commonSensors.annotateWithPipeline(doc, id)
    }

    val sentenceList = documentList.flatMap(_.sentences())

    val constituentList = sentenceList.map(_.getSentenceConstituent)

    /** instantiating nodes */
    documents.populate(documentList)

    sentences.populate(sentenceList)

    constituents.populate(constituentList)

    /** instantiating edges */
    docToSen.populateWith(commonSensors.textAnnotationSentenceAlignment(_, _))

    senToCons.populateWith(commonSensors.sentenceConstituentAlignment(_, _))

    docToCons.populateWith(commonSensors.textAnnotationConstituentAlignment(_, _))

    /** query edges */
    val sentencesQueriedFromDocs = docToSen.forward.neighborsOf(documentList.head)

    val docsQueriedFromSentences = docToSen.backward.neighborsOf(sentenceList.head)

    val consQueriesFromSentences = senToCons.forward.neighborsOf(sentenceList.head)

    val sentencesQueriesFromCons = senToCons.backward.neighborsOf(constituentList.head)

    val consQueriesFromDocs = docToCons.forward.neighborsOf(documentList.head)

    val docsQueriesFromCons = docToCons.backward.neighborsOf(constituentList.head)

    println(sentencesQueriedFromDocs.map(_.toString).toSet == consQueriesFromDocs.map(_.toString).toSet)

    println(sentencesQueriesFromCons.map(_.toString).toSet == consQueriesFromSentences.map(_.toString).toSet)

    println(docsQueriedFromSentences.map(_.toString).toSet == docsQueriesFromCons.map(_.toString).toSet)

    /** querty properties */
    val sentenceContentFromDoc = docToSen.to().prop(sentenceContent)

    val sentencesContentFromCons = senToCons.from().prop(sentenceContent)

    val docContentFromSentence = docToSen.from().prop(documentContent)

    val docContentFromCons = docToCons.from().prop(documentContent)

    val consContentFromSentence = senToCons.to().prop(constituentContent)

    val consContentFromDocs = docToCons.to().prop(constituentContent)

    println(sentenceContentFromDoc.toSet == sentencesContentFromCons.toSet)
    println(docContentFromSentence.toSet == docContentFromCons.toSet)
    println(consContentFromSentence.toSet == consContentFromDocs.toSet)
  }
}

object toyDataGenerator {
  val documentString = "Saul or Soul; that is the question"
  def generateToyDocuments(numDocs: Int): IndexedSeq[Document] = {
    (1 to numDocs).map(_ => new Document(documentString.split(" ").toList, util.Random.nextInt(2).toString))
  }

  def generateToyTextAnnotation(numDocs: Int): List[TextAnnotation] = {
    (1 to numDocs).map { _ =>
      val numSentences = 5
      val documentsTokenized = for (i <- 1 to numSentences)
        yield documentString.split(" ")
      val a = BasicTextAnnotationBuilder.createTextAnnotationFromTokens(documentsTokenized)
      println(a.getAvailableViews)
      // TODO: add sentences
      println(a.sentences().size())
      a
    }.toList
  }

  def main(args: Array[String]): Unit = {
    generateToyTextAnnotation(3)
  }
}
