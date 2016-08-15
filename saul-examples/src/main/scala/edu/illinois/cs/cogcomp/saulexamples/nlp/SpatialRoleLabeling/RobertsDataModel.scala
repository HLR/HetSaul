/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Sentence }
import edu.illinois.cs.cogcomp.edison.features.factory.WordNetFeatureExtractor
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.RobertsElementTypes
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLSensors._

import scala.collection.JavaConverters._
import scala.collection.immutable.HashSet
import scala.collection.mutable.ListBuffer

/** Created by taher on 8/10/16.
  */
object RobertsDataModel extends DataModel {
  val undefined = "[undefined]"
  val parseView = ViewNames.PARSE_STANFORD
  val sentences = node[Sentence]
  val tokens = node[Constituent]((x: Constituent) => getConstituentId(x))
  val relations = node[RobertsRelation]
  val sentencesToTokens = edge(sentences, tokens)
  // Basic Features
  val BF1 = property(relations) {
    x: RobertsRelation => x.getTrajector.getText
  }
  sentencesToTokens.addSensor(sentenceToTokens _)
  val BF2 = property(relations) {
    x: RobertsRelation => x.getLandmark.getText
  }
  val BF3 = property(relations) {
    x: RobertsRelation => x.getSpatialIndicator.getText
  }
  val BF4 = property(relations) {
    x: RobertsRelation => getLemma(x.getTrajector.getFirstConstituent)
  }
  val BF5 = property(relations) {
    x: RobertsRelation => if (x.landmarkIsDefined()) getLemma(x.getLandmark.getFirstConstituent()) else undefined
  }
  val BF6 = property(relations) {
    x: RobertsRelation =>
      {
        val t = x.getTrajector.getFirstConstituent.getStartSpan
        val spStart = x.getSpatialIndicator.getFirstConstituent.getStartSpan
        val spEnd = x.getSpatialIndicator.getLastConstituent.getStartSpan
        if (t < spStart)
          getDependencyPath(x.getTextAnnotation, t, spStart)
        else
          getDependencyPath(x.getTextAnnotation, t, spEnd)
      }
  }
  val BF7 = property(relations) {
    x: RobertsRelation =>
      if (!x.landmarkIsDefined()) undefined
      else {

        val l = x.getLandmark.getFirstConstituent.getStartSpan
        val spStart = x.getSpatialIndicator.getFirstConstituent.getStartSpan
        val spEnd = x.getSpatialIndicator.getLastConstituent.getStartSpan

        if (l < spStart)
          getDependencyPath(x.getTextAnnotation, spStart, l)
        else
          getDependencyPath(x.getTextAnnotation, spEnd, l)
      }
  }
  val JF2_1 = property(relations) {
    x: RobertsRelation => BF1(x) + "::" + BF3(x) + "::" + BF2(x)
  }

  // Supervised1 features
  val JF2_2 = property(relations) {
    x: RobertsRelation =>
      if (x.landmarkIsDefined()) spLexicon.exists(s => s.contains(x.getLandmark.getText))
      else false
  }
  val JF2_3 = property(relations) {
    x: RobertsRelation =>
      {
        val tokens = x.getTextAnnotation.getView(ViewNames.TOKENS)
          .getConstituentsCoveringSpan(x.getFirstArg.getSpan.getSecond, x.getLastArg.getSpan.getFirst)
          .asScala.toList
        tokens.filter(t => !x.isInArgs(t.getSpan)).map(x => x.toString).mkString(",")
      }
  }
  val JF2_4 = property(relations) {
    x: RobertsRelation => BF3(x) + "::" + BF7(x)
  }
  val JF2_5 = property(relations) {
    x: RobertsRelation => BF1(x)
  }
  val JF2_6 = property(relations) {
    x: RobertsRelation => BF7(x)
  }
  val JF2_7 = property(relations) {
    x: RobertsRelation => BF6(x) + "::" + BF3(x)
  }
  val JF2_8 = property(relations) {
    x: RobertsRelation =>
      val fex = new WordNetFeatureExtractor
      fex.addFeatureType(WordNetFeatureExtractor.WordNetFeatureClass.hypernymsAllSenses)
      getFeature(x.getLandmark.getFirstConstituent, fex)
  }
  val JF2_9 = property(relations) {
    x: RobertsRelation =>
      val fex = new WordNetFeatureExtractor
      fex.addFeatureType(WordNetFeatureExtractor.WordNetFeatureClass.hypernymsAllSenses)
      getFeature(x.getTrajector.getFirstConstituent, fex)
  }
  val JF2_10 = property(relations) {
    x: RobertsRelation =>
      {
        val tokens = x.getTextAnnotation.getView(ViewNames.TOKENS)
          .getConstituentsCoveringSpan(x.getFirstArg.getSpan.getFirst, x.getLastArg.getSpan.getSecond)
          .asScala.toList
        val list = ListBuffer[String]()
        tokens.foreach(t => {
          val arg = x.getCoveringArg(t.getSpan)
          if (arg != null) {
            if (arg.getElementType == RobertsElementTypes.INDICATOR) {
              if (!list.contains(RobertsElementTypes.INDICATOR.toString)) {
                list += arg.getElementType.toString
              }
            } else
              list += arg.getElementType.toString
          } else {
            list += t.toString.toLowerCase()
          }
        })
        list.mkString("_")
      }
  }
  val JF2_11 = property(relations) {
    x: RobertsRelation => ""
  }
  val JF2_12 = property(relations) {
    //TODO: Get probank role types of the arguments
    x: RobertsRelation =>
      "TRAJECTOR=" + x.getTrajector.getText + ";INDICATOR=" +
        x.getSpatialIndicator.getText + ";LANDMARK=" + x.getLandmark.getText
  }
  val JF2_13 = property(relations) {
    x: RobertsRelation => "not implemented"
  }
  val JF2_14 = property(relations) {
    x: RobertsRelation => BF4(x) + "::" + BF3(x) + "::" + BF5(x)
  }
  val JF2_15 = property(relations) {
    x: RobertsRelation => "not implemented"
  }
  val BH1 = property(relations) {
    x: RobertsRelation => x.getRelationType
  }
  var spLexicon = HashSet[String]()
}
