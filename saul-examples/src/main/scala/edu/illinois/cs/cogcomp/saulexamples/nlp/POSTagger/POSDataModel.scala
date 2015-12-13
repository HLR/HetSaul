package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.collection.JavaConversions._

object POSDataModel extends DataModel {

  val tokens = node[Constituent]

  import POSTTaggerSensors._

  val constituentAfter = edge(tokens, tokens)
  constituentAfter.addSensor( getConstituentAfter _ )

  val constituentBefore = edge(tokens, tokens)
  constituentBefore.addSensor( getConstituentBefore _ )

  val constituentTwoAfter = edge(tokens, tokens)
  constituentTwoAfter.addSensor( getConstituentTwoAfter _ )

  val constituentTwoBefore = edge(tokens, tokens)
  constituentTwoBefore.addSensor( getConstituentTwoBefore _ )

  val posLabel = property[Constituent]("label") {
    x: Constituent => x.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x).get(0).getLabel
  }

  val wordForm = property[Constituent]("wordForm") {
    x: Constituent =>
      val wordFormLabel = x.getTextAnnotation.getView(ViewNames.TOKENS).getConstituentsCovering(x).get(0).getLabel

      if (wordFormLabel.length == 1 && "([{".indexOf(wordFormLabel) != -1) {
        "-LRB-"
      } else if (wordFormLabel.length == 1 && ")]}".indexOf(wordFormLabel) != -1) {
        "-RRB-"
      } else {
        wordFormLabel
      }
  }

  val baselineLabel = property[Constituent]("baselineLabel") {
    x: Constituent => ""
  }

  val labelOneBefore = property[Constituent]("labelOneBefore") { 
    x: Constituent => 
	    val allCons = 	x.getView.getConstituents.toList.filter(cons => cons.getEndSpan <= x.getStartSpan ).maxBy(_.getEndSpan)

    ""
}

  val labelTwoBefore = property[Constituent]("labelTwoBefore") { 
    x: Constituent => ""
  }

  val labelOneAfter = property[Constituent]("labelOneAfter") { 
    x: Constituent => ""
  }

  val labelTwoAfter = property[Constituent]("labelTwoAfter") { 
    x: Constituent => ""
  }

  val L2bL1b = property[Constituent]("label2beforeLabel1beforeConjunction") { 
    x: Constituent => ""
  }

  val L1bL1a = property[Constituent]("label1beforeLabel1afterConjunction") { 
  	x: Constituent => ""
  }

  val L1aL2a = property[Constituent]("labelfterLabel2AfterConjunction") { 
  x: Constituent => ""
  }

}
