package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.lbj.pos.POSLabeledUnknownWordParser
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers.{ POSTaggerUnknown, POSTaggerKnown, BaselineClassifier }

object POSDataModel extends DataModel {

  val tokens = node[Constituent]

  import POSTTaggerSensors._

  val constituentAfter = edge(tokens, tokens)
  constituentAfter.addSensor(getConstituentAfter _)

  val constituentBefore = edge(tokens, tokens)
  constituentBefore.addSensor(getConstituentBefore _)

  val constituentTwoAfter = edge(tokens, tokens)
  constituentTwoAfter.addSensor(getConstituentTwoAfter _)

  val constituentTwoBefore = edge(tokens, tokens)
  constituentTwoBefore.addSensor(getConstituentTwoBefore _)

  val POSLabel = property[Constituent]("label") {
    x: Constituent => x.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x).get(0).getLabel
  }

  val wordForm = property[Constituent]("wordForm") {
    x: Constituent =>
      val wordFormLabel = x.toString
      if (wordFormLabel.length == 1 && "([{".indexOf(wordFormLabel) != -1) {
        "-LRB-"
      } else if (wordFormLabel.length == 1 && ")]}".indexOf(wordFormLabel) != -1) {
        "-RRB-"
      } else {
        wordFormLabel
      }
  }

  val labelOrBaseline = property[Constituent]("labelOrBaseline") {
    x: Constituent =>
      if (POSTaggerKnown.isTraining)
        POSLabel(x)
      else if (BaselineClassifier.classifier.observed(x.toString))
        BaselineClassifier.classifier.discreteValue(x)
      else
        "UNKNOWN"
  }

  val labelOrBaselineU = property[Constituent]("labelOrBaselineU") {
    x: Constituent =>
      if (POSTaggerUnknown.isTraining)
        POSLabel(x)
      else if (BaselineClassifier.classifier.observed(x.toString))
        BaselineClassifier.classifier.discreteValue(x)
      else
        "UNKNOWN"
  }

  val labelOneBefore = property[Constituent]("labelOneBefore") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentBefore).head
      // make sure the spans are different. Otherwise it is not valid
      if (cons.getSpan != x.getSpan) {
        if (POSTaggerKnown.isTraining)
          POSLabel(cons)
        else
          POSTaggerKnown.classifier.discreteValue(cons)
      } else ""
  }

  val labelOneBeforeU = property[Constituent]("labelOneBeforeU") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentBefore).head
      // make sure the spans are different. Otherwise it is not valid
      if (cons.getSpan != x.getSpan) {
        if (POSTaggerUnknown.isTraining)
          POSLabel(cons)
        else
          POSTaggerUnknown.classifier.discreteValue(cons)
      } else ""
  }

  val labelTwoBefore = property[Constituent]("labelTwoBefore") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentTwoBefore).head
      // make sure the spans are different. Otherwise it is not valid
      if (cons.getSpan != x.getSpan) {
        if (POSTaggerKnown.isTraining) {
          //          println("Training ")
          POSLabel(cons)
        } else {
          //          println("testing ")
          POSTaggerKnown.classifier.discreteValue(cons)
        }
      } else ""
  }

  val labelTwoBeforeU = property[Constituent]("labelTwoBeforeU") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentTwoBefore).head
      // make sure the spans are different. Otherwise it is not valid
      if (cons.getSpan != x.getSpan) {
        if (POSTaggerUnknown.isTraining) {
          //          println("Training ")
          POSLabel(cons)
        } else {
          //          println("testing ")
          POSTaggerUnknown.classifier.discreteValue(cons)
        }
      } else ""
  }

  val labelOneAfter = property[Constituent]("labelOneAfter") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentAfter).head
      // make sure the spans are different. Otherwise it is not valid
      if (cons.getSpan != x.getSpan) {
        labelOrBaseline(cons)
      } else ""
  }

  // TODO: same as `labelOneAfter`. Remove this?
  val labelOneAfterU = property[Constituent]("labelOneAfterU") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentAfter).head
      // make sure the spans are different. Otherwise it is not valid
      if (cons.getSpan != x.getSpan) {
        labelOrBaseline(cons)
      } else ""
  }

  val labelTwoAfter = property[Constituent]("labelTwoAfter") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentTwoAfter).head
      // make sure the spans are different. Otherwise it is not valid
      if (cons.getSpan != x.getSpan) {
        labelOrBaseline(cons)
      } else ""
  }

  // TODO: same as `labelTwoAfter`. Remove this?
  val labelTwoAfterU = property[Constituent]("labelTwoAfterU") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentTwoAfter).head
      // make sure the spans are different. Otherwise it is not valid
      if (cons.getSpan != x.getSpan) {
        labelOrBaseline(cons)
      } else ""
  }

  val L2bL1b = property[Constituent]("label2beforeLabel1beforeConjunction") {
    x: Constituent => labelOneBefore(x) + "-" + labelTwoBefore(x)
  }

  val L2bL1bU = property[Constituent]("label2beforeLabel1beforeConjunctionU") {
    x: Constituent => labelOneBeforeU(x) + "-" + labelTwoBeforeU(x)
  }

  val L1bL1a = property[Constituent]("label1beforeLabel1afterConjunction") {
    x: Constituent => labelOneBefore(x) + "-" + labelOneAfter(x)
  }

  val L1bL1aU = property[Constituent]("label1beforeLabel1afterConjunctionU") {
    x: Constituent => labelOneBeforeU(x) + "-" + labelOneAfterU(x)
  }

  val L1aL2a = property[Constituent]("labelfterLabel2AfterConjunction") {
    x: Constituent => labelOneAfter(x) + "-" + labelTwoAfter(x)
  }

  val L1aL2aU = property[Constituent]("labelfterLabel2AfterConjunctionU") {
    x: Constituent => labelOneAfterU(x) + "-" + labelTwoAfterU(x)
  }

  /** When baselineTarget has not observed the given word during
    * training, this classifier extracts suffixes of the word of various
    * lengths.
    */
  // TODO simplify this
  val suffixFeatures = property[Constituent]("suffixFeatures") {
    x: Constituent =>
      x.toString

      val length = x.toString.length()
      val unknown = POSTaggerUnknown.isTraining &&
        BaselineClassifier.classifier.observedCount(x.toString) <= POSLabeledUnknownWordParser.threshold ||
        !POSTaggerUnknown.isTraining && BaselineClassifier.classifier.discreteValue(x).equals("UNKNOWN")
      val (c, d) = if (unknown && length > 3 && Character.isLetter(x.toString.charAt(length - 1))) {
        val a = x.toString.substring(length - 2).toLowerCase()
        val b = if (length > 4 && Character.isLetter(x.toString.charAt(length - 3)))
          x.toString.substring(length - 3).toLowerCase()
        else ""
        (a, b)
      } else
        ("", "")
      c + "-" + d
  }
}
