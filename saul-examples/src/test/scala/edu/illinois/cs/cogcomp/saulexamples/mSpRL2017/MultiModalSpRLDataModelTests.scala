package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.data.CLEFImageReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Sentence, Token}
import org.scalatest.{FlatSpec, Matchers}
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.{getCandidateRelations, getPos}
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.XmlMatchings

import scala.collection.JavaConversions._

/** Created by Taher on 2017-01-24.
  */
class MultiModalSpRLDataModelTests extends FlatSpec with Matchers {
  val reader = new NlpXmlReader("./saul-examples/src/test/resources/SpRL/2017/test.xml", "SCENE", "SENTENCE", null, null)
  val CLEFDataSet = new CLEFImageReader("data/mSprl/saiapr_tc-12", "test.xml", "test.xml", false)

  reader.setIdUsingAnotherProperty("SCENE", "DOCNO")
  val documentList = reader.getDocuments()
  val sentenceList = reader.getSentences()

  documents.populate(documentList)
  images.populate(CLEFDataSet.trainingImages)
  images.populate(CLEFDataSet.testImages)
  segments.populate(CLEFDataSet.trainingSegments)
  segments.populate(CLEFDataSet.testSegments)
  segmentRelations.populate(CLEFDataSet.trainingRelations)
  segmentRelations.populate(CLEFDataSet.testRelations)
  sentences.populate(sentenceList)

  reader.addPropertiesFromTag("TRAJECTOR", phrases().toList, XmlMatchings.xmlContainsElementHeadwordMatching)
  reader.addPropertiesFromTag("LANDMARK", phrases().toList, XmlMatchings.xmlContainsElementHeadwordMatching)
  reader.addPropertiesFromTag("SPATIALINDICATOR", phrases().toList, XmlMatchings.xmlContainsElementHeadwordMatching)

  s"text features for '${sentences().head.getText}'" should "be correct." in {
    val sentenceList = sentences().toList
    val firstSentencePhrases = phrases().filter(_.getSentence.getId == sentenceList(0).getId).toList
    val secondSentencePhrases = phrases().filter(_.getSentence.getId == sentenceList(1).getId).toList
    val thirdSentencePhrases = phrases().filter(_.getSentence.getId == sentenceList(2).getId).toList
    spatialContext(firstSentencePhrases(0)) should be("1")
    spatialContext(firstSentencePhrases(1)) should be("0")
    spatialContext(firstSentencePhrases(2)) should be("0")
    spatialContext(firstSentencePhrases(3)) should be("2")
    spatialContext(firstSentencePhrases(4)) should be("0")
    spatialContext(firstSentencePhrases(5)) should be("0")
    spatialContext(firstSentencePhrases(6)) should be("0")
    spatialContext(firstSentencePhrases(7)) should be("1")
    spatialContext(firstSentencePhrases(8)) should be("0")
    spatialContext(firstSentencePhrases(9)) should be("0")
    spatialContext(firstSentencePhrases(10)) should be("2")
    spatialContext(firstSentencePhrases(11)) should be("0")

    spatialRole(firstSentencePhrases(0)) should be("Indicator")
    spatialRole(firstSentencePhrases(1)) should be("Landmark")
    spatialRole(firstSentencePhrases(2)) should be("Trajector")
    spatialRole(firstSentencePhrases(3)) should be("None")
    spatialRole(firstSentencePhrases(4)) should be("None")
    spatialRole(firstSentencePhrases(5)) should be("None")
    spatialRole(firstSentencePhrases(6)) should be("Trajector")
    spatialRole(firstSentencePhrases(7)) should be("None")
    spatialRole(firstSentencePhrases(8)) should be("Trajector")
    spatialRole(firstSentencePhrases(9)) should be("None")
    spatialRole(firstSentencePhrases(10)) should be("Indicator")
    spatialRole(firstSentencePhrases(11)) should be("Landmark")

    isImageConcept(firstSentencePhrases(0)) should be("false")
    isImageConcept(firstSentencePhrases(1)) should be("false")
    isImageConcept(firstSentencePhrases(2)) should be("false")
    isImageConcept(firstSentencePhrases(3)) should be("false")
    isImageConcept(firstSentencePhrases(4)) should be("false")
    isImageConcept(firstSentencePhrases(5)) should be("false")
    isImageConcept(firstSentencePhrases(6)) should be("false")
    isImageConcept(firstSentencePhrases(7)) should be("false")
    isImageConcept(firstSentencePhrases(8)) should be("false")
    isImageConcept(firstSentencePhrases(9)) should be("false")
    isImageConcept(firstSentencePhrases(10)) should be("false")
    isImageConcept(firstSentencePhrases(11)) should be("false")

    isImageConcept(secondSentencePhrases(0)) should be("false")
    isImageConcept(secondSentencePhrases(1)) should be("false")
    isImageConcept(secondSentencePhrases(2)) should be("false")
    isImageConcept(secondSentencePhrases(3)) should be("true")
    isImageConcept(secondSentencePhrases(4)) should be("false")
    isImageConcept(secondSentencePhrases(5)) should be("false")
    isImageConcept(secondSentencePhrases(6)) should be("false")
    isImageConcept(secondSentencePhrases(7)) should be("false")
    isImageConcept(secondSentencePhrases(8)) should be("false")
  }
}
