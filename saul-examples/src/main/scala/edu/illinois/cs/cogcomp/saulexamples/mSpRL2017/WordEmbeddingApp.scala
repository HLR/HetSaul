package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.data.CLEFImageReader
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLSensors.phraseConceptToWord
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.{ClefDocument, SpRLDataReader}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator._
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory

import scala.collection.JavaConversions._

/** Created by Taher on 2017-02-12.
  */
object WordEmbeddingApp extends App with Logging {

  val vec = build()
  //val vec = load()
  println(vec.wordsNearest("girl", 5))

  private def load() = WordVectorSerializer.readWord2VecModel("data/clef.bin")

  private def build() = {
    val iter = new CustomSentenceIterator("data/SpRL/CLEF/texts/", ".eng")
    iter.setPreProcessor(new SentencePreProcessor() {
      override def preProcess(s: String): String = s.toLowerCase
    })

    val tokenizer = new DefaultTokenizerFactory()
    tokenizer.setTokenPreProcessor(new CommonPreprocessor())

    val vec = new Word2Vec.Builder()
      .minWordFrequency(5)
      .iterations(1000)
      .layerSize(300) // word vector size
      .windowSize(5)
      .iterate(iter)
      .tokenizerFactory(tokenizer)
      .build()
    vec.fit()
    WordVectorSerializer.writeWord2VecModel(vec, "data/clef.bin")
    vec
  }
}

class CustomSentenceIterator(corpusPath: String, extension: String) extends BaseSentenceIterator {

  private val reader = new SpRLDataReader(corpusPath, classOf[ClefDocument], extension)
  private val CLEFDataSet = new CLEFImageReader("data/mSprl/saiapr_tc-12", "newSprl2017_train", "newSprl2017_gold", true)
  val segments = CLEFDataSet.trainingSegments ++ CLEFDataSet.testSegments
  reader.readData()
  private val docs = reader.documents
  val docTexts = docs.map(x => {
    val imageText = segments.filter(s => x.getImage.endsWith("/" + s.getAssociatedImageID + ".jpg"))
      .flatMap(x => List(getConceptWord(x.getSegmentConcept), x.getSegmentConcept)).distinct.mkString(" ")
    x.getDescription.split(";").map(_.trim).mkString(" ") + imageText
  })
  private val lines = docTexts.filter(_ != "")
  private var iter = lines.toIterator

  private def getConceptWord(x: String) =
    if (!phraseConceptToWord.contains(x))
      x
    else
      phraseConceptToWord(x)

  override def nextSentence(): String = {
    val line = this.iter.next
    if (this.preProcessor != null)
      this.preProcessor.preProcess(line)
    else
      line
  }

  override def hasNext: Boolean = iter.hasNext

  override def reset(): Unit = {
    iter = lines.toIterator
  }
}