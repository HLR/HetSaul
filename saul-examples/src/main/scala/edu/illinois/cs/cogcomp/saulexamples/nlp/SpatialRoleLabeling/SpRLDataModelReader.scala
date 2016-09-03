/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.IntPair
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SpRL2013Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.SpRL2015Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory
import org.apache.commons.lang.NotImplementedException

import scala.collection.JavaConverters._
import scala.collection.immutable.HashSet
import scala.collection.mutable.ListBuffer

/** Created by taher on 8/6/16.
  */
object SpRLDataModelReader extends Logging {

  def read[T](path: String, isTraining: Boolean, version: String,
    getRelations: (Sentence, SpRL2013Document, HashSet[String], IntPair) => List[T],
    getLexicon: (List[SpRL2013Document]) => HashSet[String]): (List[Sentence], List[T], HashSet[String]) = {

    def readSpRL2013(): (List[Sentence], List[T], HashSet[String]) = {

      val reader = new SpRLDataReader(path, classOf[SpRL2013Document])
      reader.readData()

      val sentences = ListBuffer[Sentence]()
      val rels = ListBuffer[T]()

      var lexicon = HashSet[String]()
      if (getLexicon != null) {
        lexicon = getLexicon(reader.documents.asScala.toList)
      }

      reader.documents.asScala.foreach(doc => {

        logger.info("working on " + doc.getFilename + " ...")
        val sentenceOffsetList = getDocumentSentences(doc)

        sentenceOffsetList.foreach(s => {
          //logger.info("sentence: '" + s._1 + "'")
          assert(s._1 == doc.getTEXT.getContent.substring(s._2.getFirst, s._2.getSecond))
          val ta = TextAnnotationFactory.createTextAnnotation(version, doc.getFilename + s._2, s._1)
          sentences += ta.sentences.get(0)
          assert(ta.sentences.size() == 1)
          rels ++= getRelations(ta.sentences.get(0), doc, lexicon, s._2)
        })
      })

      (sentences.toList, rels.toList, lexicon)
    }

    def getSentenceOffset(s: Sentence): IntPair = {
      val sc = s.getSentenceConstituent
      val start = sc.getInclusiveStartCharOffset()
      val end = sc.getEndCharOffset()
      new IntPair(start, end)
    }

    def getDocumentSentences(doc: SpRL2013Document): List[(String, IntPair)] = {
      version match {
        case "2012" => {
          // 2012 sentences end with '.\n\n' so
          // split by '.\n\n' and keep the delimiter with the previous sentence
          val sentences = doc.getTEXT.getContent.split("(?<=.\\n\\n)")
          var start = 0
          sentences.map(s => {
            val pair = (s, new IntPair(start, start + s.length))
            start = start + s.length
            pair
          }).toList
        }
        case "2013" => {
          // 2013 sentences are more complex to split compared to 2012 version, so we let the TextAnnotationFactory
          // do it
          val ta = TextAnnotationFactory.createBasicTextAnnotation(version, doc.getFilename, doc.getTEXT().getContent)
          ta.sentences().asScala.map(s => (s.getText, getSentenceOffset(s))).toList
        }
      }
    }

    //TODO: generate TextAnnotations from SpRLDocuments
    def readSpRL2015(): (List[Sentence], List[T], HashSet[String]) = {
      val reader = new SpRLDataReader(path, classOf[SpRL2015Document])
      reader.readData()
      throw new NotImplementedException
    }

    version match {
      case "2013" | "2012" => readSpRL2013
      case _ => readSpRL2015
    }
  }
}
