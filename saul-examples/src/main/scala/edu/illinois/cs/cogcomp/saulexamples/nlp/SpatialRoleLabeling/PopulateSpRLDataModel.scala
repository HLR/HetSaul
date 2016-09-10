/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saul.util.Logging

import java.lang.Boolean
import scala.collection.JavaConverters._
import scala.collection.immutable.HashSet

/** Created by taher on 7/28/16.
  */
object PopulateSpRLDataModel extends Logging {
  def apply(path: String, isTraining: Boolean, dataVersion: String, modelName: String, savedLexicon: HashSet[String]) = {

    modelName match {
      case "Relation" =>
        val getLex: (List[SpRLSentence]) => HashSet[String] = if (isTraining) getLexicon else (x) => savedLexicon
        val sentences: List[SpRLSentence] = SpRLDataModelReader.read(path, dataVersion)

        Dictionaries.spLexicon = getLex(sentences)
        SpRLDataModel.sentences.populate(sentences, train = isTraining)
    }
  }

  def getLexicon(docs: List[SpRLSentence]): HashSet[String] = {
    val dic: Seq[String] = Dictionaries.prepositions.toSeq
    val indicators: Seq[String] = docs.flatMap(d => d.getRelations.
      asScala.map(s => s.getSpatialIndicator.getText.toLowerCase.trim))

    HashSet[String](dic ++ indicators: _*)
  }

}
