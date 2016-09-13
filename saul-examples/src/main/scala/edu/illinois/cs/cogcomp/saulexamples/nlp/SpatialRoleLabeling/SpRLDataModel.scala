/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.edison.features.factory.WordNetFeatureExtractor
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Triplet.{ SpRelation, SpRoleTypes }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLSensors._

import scala.collection.JavaConverters._
import scala.collection.immutable.HashSet
import scala.collection.mutable.ListBuffer

/** Created by taher on 8/10/16.
  */
object SpRLDataModel extends DataModel {

  val undefined = "[undefined]"

  // data model
  val sentences = node[SpRLSentence]
  val relations = node[SpRelation]
  val sentencesToRelations = edge(sentences, relations)

  // sensors
  sentencesToRelations.addSensor(SpRLSensors.sentencesToRelations _)

  // classifier labels
  val relationLabel = property(relations) {
    x: SpRelation => x.getLabel.toString
  }

  // Basic Features
  val BF1 = property(relations) {
    x: SpRelation => x.getTrajector.getText
  }

  val BF2 = property(relations) {
    x: SpRelation => if (x.landmarkIsDefined) x.getLandmark.getText else undefined
  }

  val BF3 = property(relations) {
    x: SpRelation => x.getSpatialIndicator.getText
  }

  val BF4 = property(relations) {
    x: SpRelation => getLemma(x.getTrajector.getFirstConstituent)
  }

  val BF5 = property(relations) {
    x: SpRelation => if (x.landmarkIsDefined()) getLemma(x.getLandmark.getFirstConstituent) else undefined
  }

  val BF6 = property(relations) {
    x: SpRelation =>
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
    x: SpRelation =>
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

  //Supervised2 features
  val JF2_1 = property(relations) {
    x: SpRelation => BF1(x) + "::" + BF3(x) + "::" + BF2(x)
  }

  val JF2_2 = property(relations) {
    x: SpRelation =>
      if (x.landmarkIsDefined()) Dictionaries.spLexicon.exists(s => s.contains(x.getLandmark.getText))
      else false
  }

  val JF2_3 = property(relations) {
    x: SpRelation =>
      {
        val tokens = x.getTextAnnotation.getView(ViewNames.TOKENS)
          .getConstituentsCoveringSpan(x.getFirstArg.getSpan.getSecond, x.getLastArg.getSpan.getFirst)
          .asScala.toList
        tokens.filter(t => !x.isInArgs(t.getSpan)).map(x => x.toString).mkString(",")
      }
  }

  val JF2_4 = property(relations) {
    x: SpRelation => BF3(x) + "::" + BF7(x)
  }

  val JF2_5 = property(relations) {
    x: SpRelation => BF1(x)
  }

  val JF2_6 = property(relations) {
    x: SpRelation => BF7(x)
  }

  val JF2_7 = property(relations) {
    x: SpRelation => BF6(x) + "::" + BF3(x)
  }

  val JF2_8 = property(relations) {
    x: SpRelation =>
      if (x.landmarkIsDefined()) {
        val fex = new WordNetFeatureExtractor
        fex.addFeatureType(WordNetFeatureExtractor.WordNetFeatureClass.hypernymsAllSenses)
        getFeature(x.getLandmark.getFirstConstituent, fex)
      } else
        undefined
  }

  val JF2_9 = property(relations) {
    x: SpRelation =>

      val fex = new WordNetFeatureExtractor
      fex.addFeatureType(WordNetFeatureExtractor.WordNetFeatureClass.hypernymsAllSenses)
      getFeature(x.getTrajector.getFirstConstituent, fex)
  }

  val JF2_10 = property(relations) {
    x: SpRelation =>
      {
        val tokens = x.getTextAnnotation.getView(ViewNames.TOKENS)
          .getConstituentsCoveringSpan(x.getFirstArg.getSpan.getFirst, x.getLastArg.getSpan.getSecond)
          .asScala.toList
        val list = ListBuffer[String]()
        tokens.foreach(t => {
          val arg = x.getCoveringArg(t.getSpan)
          if (arg != null) {
            if (arg.getSpRoleType == SpRoleTypes.INDICATOR) {
              if (!list.contains(SpRoleTypes.INDICATOR.toString)) {
                list += arg.getSpRoleType.toString
              }
            } else
              list += arg.getSpRoleType.toString
          } else {
            list += t.toString.toLowerCase()
          }
        })
        list.mkString("_")
      }
  }

  val JF2_11 = property(relations) {
    x: SpRelation =>
      val t = x.getTrajector.getFirstConstituent
      val i = x.getSpatialIndicator

      val preps = getDependencyRelationsWith(t, "PREP")
        .map(r => if (r.getSource.getSpan == t.getSpan) r.getTarget else r.getSource)
      val otherPrep = preps.find(p => !i.isCovering(p.getSpan))

      otherPrep match {
        case Some(p) => p.toString
        case None => ""
      }
  }

  val JF2_12 = property(relations) {
    x: SpRelation =>
      val view = x.getTextAnnotation.getView(ViewNames.SRL_VERB)
      view match {
        case null =>
          "TRAJECTOR=;INDICATOR=;LANDMARK="
        case _ =>
          val tr = view.getLabelsCovering(x.getTrajector.getFirstConstituent).asScala.mkString
          val lm =
            if (x.landmarkIsDefined())
              view.getLabelsCovering(x.getLandmark.getFirstConstituent).asScala.mkString
            else undefined
          val sp = view.getLabelsCovering(x.getSpatialIndicator.getFirstConstituent).asScala.mkString

          "TRAJECTOR=" + tr + ";INDICATOR=" + sp + ";LANDMARK=" + lm
      }
  }

  val JF2_13 = property(relations) {
    x: SpRelation =>
      !x.landmarkIsDefined() &&
        !x.getSentence.getView(ViewNames.TOKENS).getConstituents.asScala
          .exists(c => !x.isInArgs(c.getSpan) && getPosTag(c).startsWith("NN"))
  }

  val JF2_14 = property(relations) {
    x: SpRelation => BF4(x) + "::" + BF3(x) + "::" + BF5(x)
  }

  val JF2_15 = property(relations) {
    x: SpRelation =>
      val t = x.getTrajector.getFirstConstituent
      val i = x.getSpatialIndicator

      val rels = getDependencyRelationsWith(t, "POBJ")

      val preps = getDependencyRelationsWith(t, "POBJ")
        .map(r => if (r.getSource.getSpan == t.getSpan) r.getTarget else r.getSource)

      val otherPrep = preps.find(p => !i.isCovering(p.getSpan) &&
        (getPosTag(p).startsWith("IN") || Dictionaries.spLexicon.contains(p.toString.toLowerCase)))

      otherPrep.isDefined
  }

  val BH1 = property(relations) {
    x: SpRelation => x.getRelationType
  }

}

