package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers.{ POSTaggerKnown, BaselineClassifier }

import scala.collection.JavaConversions._

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

  val labelOrBaseline = property[Constituent]("baselineLabel") {
    x: Constituent =>
      if (POSTaggerKnown.isTraining) {
        println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> training ")
        POSLabel(x)
      } else {
        println(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< testing  ")
        BaselineClassifier.classifier.discreteValue(x)
      }
  }

  val labelOneBefore = property[Constituent]("labelOneBefore") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentBefore).head
      if (POSTaggerKnown.isTraining)
        POSLabel(cons)
      else
        POSTaggerKnown.classifier.discreteValue(cons)
  }

  val labelTwoBefore = property[Constituent]("labelTwoBefore") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentTwoBefore).head
      if (POSTaggerKnown.isTraining)
        POSLabel(cons)
      else
        POSTaggerKnown.classifier.discreteValue(cons)
  }

  val labelOneAfter = property[Constituent]("labelOneAfter") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentAfter).head
      if (POSTaggerKnown.isTraining)
        POSLabel(cons)
      else
        POSTaggerKnown.classifier.discreteValue(cons)
  }

  val labelTwoAfter = property[Constituent]("labelTwoAfter") {
    x: Constituent =>
      val cons = (tokens(x) ~> constituentTwoAfter).head
      if (POSTaggerKnown.isTraining)
        POSLabel(cons)
      else
        POSTaggerKnown.classifier.discreteValue(cons)
  }

  val L2bL1b = property[Constituent]("label2beforeLabel1beforeConjunction") {
    x: Constituent =>
      val beforeCons = (tokens(x) ~> constituentBefore).head
      val twoBeforeCons = (tokens(x) ~> constituentTwoBefore).head
      val (before, twoBefore) = if (POSTaggerKnown.isTraining)
        (POSLabel(beforeCons), POSLabel(twoBeforeCons))
      else
        (POSTaggerKnown.classifier.discreteValue(beforeCons), POSTaggerKnown.classifier.discreteValue(twoBeforeCons))
      before + "-" + twoBefore
  }

  val L1bL1a = property[Constituent]("label1beforeLabel1afterConjunction") {
    x: Constituent =>
      val beforeCons = (tokens(x) ~> constituentBefore).head
      val afterCons = (tokens(x) ~> constituentAfter).head
      val (before, after) = if (POSTaggerKnown.isTraining)
        (POSLabel(beforeCons), POSLabel(afterCons))
      else
        (POSTaggerKnown.classifier.discreteValue(beforeCons), POSTaggerKnown.classifier.discreteValue(afterCons))
      before + "-" + after
  }

  val L1aL2a = property[Constituent]("labelfterLabel2AfterConjunction") {
    x: Constituent =>
      val afterCons = (tokens(x) ~> constituentAfter).head
      val twoAfterCons = (tokens(x) ~> constituentTwoAfter).head
      val (after, twoAfter) = if (POSTaggerKnown.isTraining)
        (POSLabel(afterCons), POSLabel(twoAfterCons))
      else
        (POSTaggerKnown.classifier.discreteValue(afterCons), POSTaggerKnown.classifier.discreteValue(twoAfterCons))
      after + "-" + twoAfter
  }
}
