/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.QuestionTypeClassification

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation
import edu.illinois.cs.cogcomp.edison.features.factory.{ WordFeatureExtractorFactory, WordNetFeatureExtractor }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.collection.JavaConverters._

case class QuestionTypeInstance(
  question: String,
  coarseLabelOpt: Option[String],
  fineLabelOpt: Option[String],
  textAnnotationOpt: Option[TextAnnotation]
)

object QuestionTypeClassificationDataModel extends DataModel {
  val question = node[QuestionTypeInstance]

  // properties
  val coarseLabel = property(question) { x: QuestionTypeInstance => x.coarseLabelOpt.get }

  val fineLabel = property(question) { x: QuestionTypeInstance => x.fineLabelOpt.get }

  val surfaceWords = property(question) { x: QuestionTypeInstance =>
    x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala.map { _.getSurfaceForm }.toList
  }

  val lemma = property(question) { x: QuestionTypeInstance =>
    x.textAnnotationOpt.get.getView(ViewNames.LEMMA).getConstituents.asScala.map { _.getLabel }.toList
  }

  val pos = property(question) { x: QuestionTypeInstance =>
    val labels = x.textAnnotationOpt.get.getView(ViewNames.POS).getConstituents.asScala.map { _.getLabel }.toSet.toList
    val tokens = x.textAnnotationOpt.get.getView(ViewNames.LEMMA).getConstituents.asScala.map { _.getSurfaceForm }.toSet.toList
    tokens.zip(labels).map("##lemma:" + _.toString)
  }

  val chunks = property(question) { x: QuestionTypeInstance =>
    val labels = x.textAnnotationOpt.get.getView(ViewNames.SHALLOW_PARSE).getConstituents.asScala.map { _.getLabel }.toList
    val tokens = x.textAnnotationOpt.get.getView(ViewNames.SHALLOW_PARSE).getConstituents.asScala.map { _.getSurfaceForm }.toList
    tokens ++ labels
  }

  val questionTerms = Set("what", "when", "where", "which", "who", "whom", "whose", "why", "why don't", "how", "how far", "how long", "how many", "how much", "how old", "how come", "do", "did")

  // head chunks (i.e the first noun chunk and the first verb chunk after the question word in a sentence).
  val headChunks = property(question) { x: QuestionTypeInstance =>
    val chunks = x.textAnnotationOpt.get.getView(ViewNames.SHALLOW_PARSE).getConstituents.asScala.map(c => (c.getLabel, c.getSurfaceForm))
    val firstNoun = chunks.collectFirst { case (label, surface) if !questionTerms.contains(surface.toLowerCase.trim) && label.contains("NP") => surface }.getOrElse("")
    val firstVerb = chunks.collectFirst { case (label, surface) if !questionTerms.contains(surface.toLowerCase.trim) && label.contains("VP") => surface }.getOrElse("")
    List("##chunkHeadNoun:" + firstNoun, "##chunkHeadVerb:" + firstVerb, "##chunkHeadNounVerb:" + firstNoun + firstVerb)
  }

  val ner = property(question) { x: QuestionTypeInstance =>
    val labels = x.textAnnotationOpt.get.getView(ViewNames.NER_CONLL).getConstituents.asScala.map { _.getLabel }.toList
    val tokens = x.textAnnotationOpt.get.getView(ViewNames.NER_CONLL).getConstituents.asScala.map { _.getSurfaceForm }.toList
    labels ++ tokens.zip(labels).map("##ner" + _.toString)
  }

  val containsProfession = property(question) { x: QuestionTypeInstance =>
    val lemmas = x.textAnnotationOpt.get.getView(ViewNames.LEMMA).getConstituents.asScala.map { _.getSurfaceForm }.toList
    lemmas.exists(lemma => QuestionTypeClassificationSensors.professons.contains(lemma)).toString
  }

  val containsFoodterm = property(question) { x: QuestionTypeInstance =>
    val lemmas = x.textAnnotationOpt.get.getView(ViewNames.LEMMA).getConstituents.asScala.map { _.getSurfaceForm }.toList
    lemmas.exists(lemma => QuestionTypeClassificationSensors.foodKeywords.contains(lemma)).toString
  }

  val containsMountain = property(question) { x: QuestionTypeInstance =>
    val lemmas = x.textAnnotationOpt.get.getView(ViewNames.LEMMA).getConstituents.asScala.map { _.getSurfaceForm }.toList
    lemmas.exists(lemma => QuestionTypeClassificationSensors.mountainKeywords.contains(lemma)).toString
  }

  // indicator for whether the word is a number
  val numberNormalizer = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.map { WordFeatureExtractorFactory.numberNormalizer.getFeatures(_).asScala.mkString }.toSet.toList
  }

  val wordnetSynsetsFirstSense = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.synsetsFirstSense).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordnetSynsetsAllSenses = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.synsetsAllSenses).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordnetLexicographerFileNamesFirstSense = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.lexicographerFileNamesFirstSense).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordnetLexicographerFileNamesAllSenses = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.lexicographerFileNamesFirstSense).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordnetHypernymFirstSenseLexicographerFileNames = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.hypernymFirstSenseLexicographerFileNames).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordnetHypernymAllSensesLexicographerFileNames = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.hypernymAllSensesLexicographerFileNames).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordnetHypernymsFirstSense = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.hypernymsFirstSense).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordnetHypernymsAllSenses = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.hypernymsAllSenses).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordnetPointersFirstSense = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.pointersFirstSense).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordnetSynonymsFirstSense = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.synonymsFirstSense).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordnetSynonymsAllSenses = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala
    cons.flatMap { WordFeatureExtractorFactory.getWordNetFeatureExtractor(WordNetFeatureExtractor.WordNetFeatureClass.synonymsAllSenses).getFeatures(_).asScala.map { _.toString } }.toSet.toList
  }

  val wordGroups = property(question) { x: QuestionTypeInstance =>
    val cons = x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala.map { _.getSurfaceForm.toLowerCase.trim }.toSet
    QuestionTypeClassificationSensors.wordGroupLists.collect { case (label, set) if set.intersect(cons).nonEmpty => label }
  }
}
