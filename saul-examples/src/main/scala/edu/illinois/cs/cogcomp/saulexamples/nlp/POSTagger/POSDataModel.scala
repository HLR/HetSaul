package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.lbj.pos.POSLabeledUnknownWordParser
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

  val POSLabel = property(tokens) { x: Constituent =>
    x.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x).get(0).getLabel
  }

  val wordForm = property(tokens, cache = true) { x: Constituent =>
    val wordFormLabel = x.toString
    if (wordFormLabel.length == 1 && "([{".indexOf(wordFormLabel) != -1)
      "-LRB-"
    else if (wordFormLabel.length == 1 && ")]}".indexOf(wordFormLabel) != -1)
      "-RRB-"
    else wordFormLabel
  }

  val baselineTarget = property(tokens, cache = true) { x: Constituent =>
    BaselineClassifier.classifier.discreteValue(x)
  }

  val labelOrBaseline = property(tokens, cache = true) { x: Constituent =>
    if (POSTaggerKnown.isTraining)
      POSLabel(x)
    else if (BaselineClassifier.classifier.observed(wordForm(x)))
      BaselineClassifier.classifier.discreteValue(x)
    else ""
  }

  val labelOrBaselineU = property(tokens, cache = true) { x: Constituent =>
    if (POSTaggerUnknown.isTraining)
      POSLabel(x)
    else if (BaselineClassifier.classifier.observed(wordForm(x)))
      BaselineClassifier.classifier.discreteValue(x)
    else ""
  }

  val labelOneBefore = property(tokens, cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentBefore).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      if (POSTaggerKnown.isTraining)
        POSLabel(cons)
      else
        POSTaggerKnown.classifier.discreteValue(cons)
    } else ""
  }

  val labelOneBeforeU = property(tokens, cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentBefore).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      if (POSTaggerUnknown.isTraining)
        POSLabel(cons)
      else
        POSTaggerUnknown.classifier.discreteValue(cons)
    } else ""
  }

  val labelTwoBefore = property(tokens, cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentTwoBefore).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      if (POSTaggerKnown.isTraining) {
        POSLabel(cons)
      } else {
        POSTaggerKnown.classifier.discreteValue(cons)
      }
    } else ""
  }

  val labelTwoBeforeU = property(tokens, cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentTwoBefore).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      if (POSTaggerUnknown.isTraining) {
        POSLabel(cons)
      } else {
        POSTaggerUnknown.classifier.discreteValue(cons)
      }
    } else ""
  }

  val labelOneAfter = property(tokens, cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentAfter).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      labelOrBaseline(cons)
    } else ""
  }

  val labelOneAfterU = property(tokens, cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentAfter).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      labelOrBaseline(cons)
    } else ""
  }

  val labelTwoAfter = property(tokens, cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentTwoAfter).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      labelOrBaseline(cons)
    } else ""
  }

  val labelTwoAfterU = property(tokens, cache = true) { x: Constituent =>
    val cons = (tokens(x) ~> constituentTwoAfter).head
    // make sure the spans are different. Otherwise it is not valid
    if (cons.getSpan != x.getSpan) {
      labelOrBaseline(cons)
    } else ""
  }

  // label2beforeLabel1beforeConjunction
  val L2bL1b = property(tokens) { x: Constituent =>
    labelTwoBefore(x) + "-" + labelOneBefore(x)
  }

  // label2beforeLabel1beforeConjunctionU
  val L2bL1bU = property(tokens) { x: Constituent =>
    labelTwoBeforeU(x) + "-" + labelOneBeforeU(x)
  }

  // label1beforeLabel1afterConjunction
  val L1bL1a = property(tokens) { x: Constituent =>
    labelOneBefore(x) + "-" + labelOneAfter(x)
  }

  // label1beforeLabel1afterConjunctionU
  val L1bL1aU = property(tokens) { x: Constituent =>
    labelOneBeforeU(x) + "-" + labelOneAfterU(x)
  }

  // labelfterLabel2AfterConjunction
  val L1aL2a = property(tokens) { x: Constituent =>
    labelOneAfter(x) + "-" + labelTwoAfter(x)
  }

  // labelfterLabel2AfterConjunctionU
  val L1aL2aU = property(tokens) { x: Constituent =>
    labelOneAfterU(x) + "-" + labelTwoAfterU(x)
  }

  /** When baselineTarget has not observed the given word during
    * training, this classifier extracts suffixes of the word of various
    * lengths.
    */
  val suffixFeatures = property(tokens) { x: Constituent =>
    val word = wordForm(x)
    val length = word.length
    val unknown = (POSTaggerUnknown.isTraining &&
      BaselineClassifier.classifier.observedCount(word) <= POSLabeledUnknownWordParser.threshold) ||
      (!POSTaggerUnknown.isTraining && BaselineClassifier.classifier.discreteValue(x).equals("UNKNOWN"))

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
