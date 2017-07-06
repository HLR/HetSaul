package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.{CandidateGenerator, ImageReaderHelper, SpRLXmlReader}
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._

/** Created by Taher on 2017-02-12.
  */
object DataExplorationApp extends App with Logging {

  private val dataDir = "data/mSprl/saiapr_tc-12/"
  val xmlReader = new SpRLXmlReader(dataDir + "sprl2017_validation_test")
  val imageReader = new ImageReaderHelper(dataDir, "newSprl2017_validation_train", "newSprl2017_validation_test", false)
  val documentList = xmlReader.getDocuments.take(10)
  val sentenceList = xmlReader.getSentences.filter(s => documentList.exists(_.getId == s.getDocument.getId))
  val imageList = imageReader.getImageList
    .filter(i => documentList.exists(_.getPropertyFirstValue("IMAGE").endsWith("/" + i.getLabel)))
  val segmentList = imageReader.getSegmentList.filter(s => imageList.exists(_.getId == s.getAssociatedImageID))
  val imageRelationList = imageReader.getImageRelationList.filter(r => imageList.exists(_.getId == r.getImageId))

  val phrases = sentenceList.flatMap(sentenceToPhraseGenerating)
  xmlReader.setRoles(phrases)
  val trCandidates = CandidateGenerator.getTrajectorCandidates(phrases)
  val lmCandidates = CandidateGenerator.getLandmarkCandidates(phrases)
  val spCandidates = CandidateGenerator.getIndicatorCandidates(phrases)

  //  println(trCandidates)
  imageList.foreach(i => println(i.getId))
  /*
  val trlmCandidatesImage = segmentList.map(_.getSegmentConcept)

  var concept = ""
  var trcount = 0
  var lmcount = 0
  trlmCandidatesImage.distinct.foreach(segment =>
  {
    if (!phraseConceptToWord.contains(segment))
      concept = segment
    else
      concept = phraseConceptToWord(segment)
    trCandidates.foreach(tr => {
      val similarity = MultiModalSpRLSensors.getWord2VectorSimilarity(tr.toString, concept)
      if(similarity > 0.40) {
        trcount = trcount + 1
        println("trajector" + tr + " - " + concept + " -> " + similarity)
      }
    })
/*    lmCandidates.foreach(lm => {
      val similarity = MultiModalSpRLSensors.getWord2VectorSimilarity(lm.toString, concept)
      if(similarity > 0.40) {
        lmcount = lmcount + 1
        println("landmark" + lm + " - " + concept + " -> " + similarity)
      }
      println("All landmark" + lm + " - " + concept + " -> " + similarity)
    })*/
  })
  println("Total trajectors text ->" + trCandidates.length)
  println("Total trajectors Image ->" + trcount)
//  println("Total landmarks text ->" + lmCandidates.length)
//  println("Total landmarks Image ->" + lmcount)*/

}
