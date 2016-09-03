/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.pos.POSLabeledUnknownWordParser
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers.{ POSTaggerUnknown, POSTaggerKnown, BaselineClassifier }

object POSDataModel extends DataModel {

  val tokens = node[Constituent]

  import POSTaggerSensors._

  val constituentAfter = edge(tokens, tokens)
  constituentAfter.addSensor(getConstituentAfter _)

  val constituentBefore = edge(tokens, tokens)
  constituentBefore.addSensor(getConstituentBefore _)

  val constituentTwoAfter = edge(tokens, tokens)
  constituentTwoAfter.addSensor(getConstituentTwoAfter _)

  val constituentTwoBefore = edge(tokens, tokens)
  constituentTwoBefore.addSensor(getConstituentTwoBefore _)

  val POSLabel = property(tokens, "label") { x: Constituent =>
    x.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x).get(0).getLabel
  }

  val wordForm = property(tokens, "wordForm", cache = true) { x: Constituent =>
    val wordFormLabel = x.toString
    if (wordFormLabel.length == 1 && "([{".indexOf(wordFormLabel) != -1)
      "-LRB-"
    else if (wordFormLabel.length == 1 && ")]}".indexOf(wordFormLabel) != -1)
      "-RRB-"
    else wordFormLabel
  }

  val baselineTarget = property(tokens, "baselineTarget", cache = true) { x: Constituent =>
    BaselineClassifier(x)
  }

  val labelOrBaseline = property(tokens, "labelOrBaseline", cache = true) { x: Constituent =>
    if (POSTaggerKnown.isTraining)
      POSLabel(x)
    else if (BaselineClassifier.classifier.observed(wordForm(x)))
      BaselineClassifier(x)
    else ""
  }

  val labelOrBaselineU = property(tokens, "labelOrBaselineU", cache = true) { x: Constituent =>
    if (POSTaggerUnknown.isTraining)
      POSLabel(x)
    else if (BaselineClassifier.classifier.observed(wordForm(x)))
      BaselineClassifier(x)
    else ""
  }

  val labelOneBefore = property(tokens, "labelOneBefore", cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentBefore).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      if (POSTaggerKnown.isTraining)
        POSLabel(cons)
      else
        POSTaggerKnown(cons)
    } else ""
  }

  val labelOneBeforeU = property(tokens, "labelOneBeforeU", cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentBefore).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      if (POSTaggerUnknown.isTraining)
        POSLabel(cons)
      else
        POSTaggerUnknown(cons)
    } else ""
  }

  val labelTwoBefore = property(tokens, "labelTwoBefore", cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentTwoBefore).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      if (POSTaggerKnown.isTraining) {
        POSLabel(cons)
      } else {
        POSTaggerKnown(cons)
      }
    } else ""
  }

  val labelTwoBeforeU = property(tokens, "labelTwoBeforeU", cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentTwoBefore).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      if (POSTaggerUnknown.isTraining) {
        POSLabel(cons)
      } else {
        POSTaggerUnknown(cons)
      }
    } else ""
  }

  val labelOneAfter = property(tokens, "labelOneAfter", cache = true) {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentAfter).head
      // make sure the spans are different. Otherwise it is not valid
      if (cons.getSpan != x.getSpan) {
        labelOrBaseline(cons)
      } else ""
  }

  val labelOneAfterU = property(tokens, "labelOneAfterU", cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentAfter).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      labelOrBaseline(cons)
    } else ""
  }

  val labelTwoAfter = property(tokens, "labelTwoAfter", cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentTwoAfter).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      labelOrBaseline(cons)
    } else ""
  }

  val labelTwoAfterU = property(tokens, "labelTwoAfterU", cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentTwoAfter).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      labelOrBaseline(cons)
    } else ""
  }

  val L2bL1b = property(tokens, "label2beforeLabel1beforeConjunction") { x: Constituent =>
    labelTwoBefore(x) + "-" + labelOneBefore(x)
  }

  val L2bL1bU = property(tokens, "label2beforeLabel1beforeConjunctionU") { x: Constituent =>
    labelTwoBeforeU(x) + "-" + labelOneBeforeU(x)
  }

  val L1bL1a = property(tokens, "label1beforeLabel1afterConjunction") { x: Constituent =>
    labelOneBefore(x) + "-" + labelOneAfter(x)
  }

  val L1bL1aU = property(tokens, "label1beforeLabel1afterConjunctionU") { x: Constituent =>
    labelOneBeforeU(x) + "-" + labelOneAfterU(x)
  }

  val L1aL2a = property(tokens, "labelfterLabel2AfterConjunction") { x: Constituent =>
    labelOneAfter(x) + "-" + labelTwoAfter(x)
  }

  val L1aL2aU = property(tokens, "labelfterLabel2AfterConjunctionU") { x: Constituent =>
    labelOneAfterU(x) + "-" + labelTwoAfterU(x)
  }

  /** When baselineTarget has not observed the given word during
    * training, this classifier extracts suffixes of the word of various
    * lengths.
    */
  val suffixFeatures = property(tokens, "suffixFeatures") { x: Constituent =>
    val word = wordForm(x)
    val length = word.length
    val unknown = (POSTaggerUnknown.isTraining &&
      BaselineClassifier.classifier.observedCount(word) <= POSLabeledUnknownWordParser.threshold) ||
      (!POSTaggerUnknown.isTraining && BaselineClassifier(x).equals("UNKNOWN"))

    val (r, s, t) = if (unknown && length > 3 && Character.isLetter(word.charAt(length - 1))) {
      val a = word.substring(length - 1).toLowerCase()

      val b = if (Character.isLetter(word.charAt(length - 2))) word.substring(length - 2).toLowerCase()
      else ""

      val c = if (length > 4 && Character.isLetter(word.charAt(length - 3)))
        word.substring(length - 3).toLowerCase()
      else ""

      (a, b, c)
    } else ("", "", "")
    r + "-" + s + "-" + t
  }
}
