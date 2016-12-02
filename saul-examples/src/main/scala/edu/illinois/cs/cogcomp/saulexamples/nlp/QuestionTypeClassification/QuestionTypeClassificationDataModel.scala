/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.QuestionTypeClassification

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, TextAnnotation }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.collection.JavaConverters._

case class QuestionTypeInstance(
  question: String,
  bothLabelsOpt: Option[String],
  coarseLabelOpt: Option[String],
  fineLabelOpt: Option[String],
  textAnnotationOpt: Option[TextAnnotation]
)

object QuestionTypeClassificationDataModel extends DataModel {
  val question = node[QuestionTypeInstance]
  val constituents = node[Constituent]

  // properties
  val bothLabel = property(question) { x: QuestionTypeInstance => x.bothLabelsOpt.get }

  val coarseLabel = property(question) { x: QuestionTypeInstance => x.coarseLabelOpt.get }

  val fineLabel = property(question) { x: QuestionTypeInstance => x.fineLabelOpt.get }

  val surfaceWords = property(question) { x: QuestionTypeInstance =>
    x.textAnnotationOpt.get.getView(ViewNames.TOKENS).getConstituents.asScala.map { _.getSurfaceForm }.toList
  }

  val pos = property(question) { x: QuestionTypeInstance =>
    x.textAnnotationOpt.get.getView(ViewNames.POS).getConstituents.asScala.map { _.getSurfaceForm }.toList
  }

  val lemma = property(question) { x: QuestionTypeInstance =>
    x.textAnnotationOpt.get.getView(ViewNames.LEMMA).getConstituents.asScala.map { _.getSurfaceForm }.toList
  }

  val chunks = property(question) { x: QuestionTypeInstance =>
    x.textAnnotationOpt.get.getView(ViewNames.SHALLOW_PARSE).getConstituents.asScala.map { _.getSurfaceForm }.toList
  }

  // head chunks (e.g., the first noun chunk and the first verb chunk after the question word in a sentence).
  val headChunks = property(question) { x: QuestionTypeInstance =>
    val chunks = x.textAnnotationOpt.get.getView(ViewNames.SHALLOW_PARSE).getConstituents.asScala
    chunks.groupBy(_.getLabel)
    ""
  }

}
