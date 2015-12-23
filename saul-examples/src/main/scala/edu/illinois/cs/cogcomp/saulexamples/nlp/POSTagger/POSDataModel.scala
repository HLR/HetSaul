package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.lbj.pos.POSLabeledUnknownWordParser
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers.{ POSTaggerUnknown, POSTaggerKnown, BaselineClassifier, MikheevClassifier }

object POSDataModel extends DataModel {

  val tokens = node[Constituent]

  val featureCacheMap = collection.mutable.HashMap[String, collection.mutable.HashMap[Constituent, String]]()

  def getOrUpdate(property: String, cons: Constituent, discreteValue: () => String): String = {
    if (featureCacheMap.contains(property) == false)
      featureCacheMap(property) = collection.mutable.HashMap[Constituent, String]()

    featureCacheMap(property).get(cons) match {
      case None =>
        featureCacheMap(property)(cons) = discreteValue()
        featureCacheMap(property)(cons)
      case _ => featureCacheMap(property)(cons)
    }
  }

  def getKnownResultValue(cons: Constituent): String = {
    POSTaggerKnown.classifier.valueOf(
      cons,
      BaselineClassifier.classifier.allowableTags(wordForm(cons))
    ).getStringValue
  }

  def getUnknownResultValue(cons: Constituent): String = {
    POSTaggerUnknown.classifier.valueOf(
      cons,
      MikheevClassifier.classifier.allowableTags(wordForm(cons))
    ).getStringValue
  }

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
      getOrUpdate("wordForm", x, () => {
        val wordFormLabel = x.toString
        if (wordFormLabel.length == 1 && "([{".indexOf(wordFormLabel) != -1)
          "-LRB-"
        else if (wordFormLabel.length == 1 && ")]}".indexOf(wordFormLabel) != -1)
          "-RRB-"
        else wordFormLabel
      })
  }

  val labelOrBaseline = property[Constituent]("labelOrBaseline") {
    x: Constituent =>
      getOrUpdate("labelOrBaseline", x, () => {
        if (POSTaggerKnown.isTraining)
          POSLabel(x)
        else if (BaselineClassifier.classifier.observed(x.toString))
          BaselineClassifier.classifier.discreteValue(x)
        else ""
      })
  }

  val labelOrBaselineU = property[Constituent]("labelOrBaselineU") {
    x: Constituent =>
      getOrUpdate("labelOrBaselineU", x, () => {
        if (POSTaggerUnknown.isTraining)
          POSLabel(x)
        else if (BaselineClassifier.classifier.observed(x.toString))
          BaselineClassifier.classifier.discreteValue(x)
        else ""
      })
  }

  val labelOneBefore = property[Constituent]("labelOneBefore") {
    x: Constituent =>
      getOrUpdate("labelOneBefore", x, () => {
        val cons = (tokens(x) ~> constituentBefore).head
        // make sure the spans are different. Otherwise it is not valid
        if (cons.getSpan != x.getSpan) {
          if (POSTaggerKnown.isTraining)
            POSLabel(cons)
          else
            getKnownResultValue(cons)
          //            POSTaggerKnown.classifier.discreteValue(cons)
        } else ""
      })
  }

  val labelOneBeforeU = property[Constituent]("labelOneBeforeU") {
    x: Constituent =>
      getOrUpdate("labelOneBeforeU", x, () => {
        val cons = (tokens(x) ~> constituentBefore).head
        // make sure the spans are different. Otherwise it is not valid
        if (cons.getSpan != x.getSpan) {
          if (POSTaggerUnknown.isTraining)
            POSLabel(cons)
          else
            getUnknownResultValue(cons)
          //            POSTaggerUnknown.classifier.discreteValue(cons)
        } else ""
      })
  }

  val labelTwoBefore = property[Constituent]("labelTwoBefore") {
    x: Constituent =>
      getOrUpdate("labelTwoBefore", x, () => {
        val cons = (tokens(x) ~> constituentTwoBefore).head
        // make sure the spans are different. Otherwise it is not valid
        if (cons.getSpan != x.getSpan) {
          if (POSTaggerKnown.isTraining) {
            //          println(s"training one before for index ${tokens(x)}")
            POSLabel(cons)
          } else {
            getKnownResultValue(cons)
            //            POSTaggerKnown.classifier.discreteValue(cons)
          }
        } else ""
      })
  }

  val labelTwoBeforeU = property[Constituent]("labelTwoBeforeU") {
    x: Constituent =>
      getOrUpdate("labelTwoBeforeU", x, () => {
        val cons = (tokens(x) ~> constituentTwoBefore).head
        // make sure the spans are different. Otherwise it is not valid
        if (cons.getSpan != x.getSpan) {
          if (POSTaggerUnknown.isTraining) {
            //          println("Training ")
            POSLabel(cons)
          } else {
            //          println("testing ")
            getUnknownResultValue(cons)
            //            POSTaggerUnknown.classifier.discreteValue(cons)
          }
        } else ""
      })
  }

  val labelOneAfter = property[Constituent]("labelOneAfter") {
    x: Constituent =>
      getOrUpdate("labelOneAfter", x, () => {
        val cons = (tokens(x) ~> constituentAfter).head
        // make sure the spans are different. Otherwise it is not valid
        if (cons.getSpan != x.getSpan) {
          labelOrBaseline(cons)
        } else ""
      })
  }

  // TODO: same as `labelOneAfter`. Remove this?
  val labelOneAfterU = property[Constituent]("labelOneAfterU") {
    x: Constituent =>
      getOrUpdate("labelOneAfterU", x, () => {
        val cons = (tokens(x) ~> constituentAfter).head
        // make sure the spans are different. Otherwise it is not valid
        if (cons.getSpan != x.getSpan) {
          labelOrBaseline(cons)
        } else ""
      })
  }

  val labelTwoAfter = property[Constituent]("labelTwoAfter") {
    x: Constituent =>
      getOrUpdate("labelTwoAfter", x, () => {
        val cons = (tokens(x) ~> constituentTwoAfter).head
        // make sure the spans are different. Otherwise it is not valid
        if (cons.getSpan != x.getSpan) {
          labelOrBaseline(cons)
        } else ""
      })
  }

  // TODO: same as `labelTwoAfter`. Remove this?
  val labelTwoAfterU = property[Constituent]("labelTwoAfterU") {
    x: Constituent =>
      getOrUpdate("labelTwoAfterU", x, () => {
        val cons = (tokens(x) ~> constituentTwoAfter).head
        // make sure the spans are different. Otherwise it is not valid
        if (cons.getSpan != x.getSpan) {
          labelOrBaseline(cons)
        } else ""
      })
  }

  val L2bL1b = property[Constituent]("label2beforeLabel1beforeConjunction") {
    x: Constituent => labelTwoBefore(x) + "-" + labelOneBefore(x)
  }

  val L2bL1bU = property[Constituent]("label2beforeLabel1beforeConjunctionU") {
    x: Constituent => labelTwoBeforeU(x) + "-" + labelOneBeforeU(x)
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
