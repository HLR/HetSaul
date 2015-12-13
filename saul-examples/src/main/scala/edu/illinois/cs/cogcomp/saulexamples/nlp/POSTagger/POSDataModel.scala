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
    x: Constituent =>  (tokens(x) ~> constituentBefore).head	   
    ""
  }

  val labelTwoBefore = property[Constituent]("labelTwoBefore") { 
    x: Constituent =>  (tokens(x) ~> constituentTwoBefore).head
  	""
  }

  val labelOneAfter = property[Constituent]("labelOneAfter") { 
    x: Constituent =>  (tokens(x) ~> constituentAfter).head
    "" 
  }

  val labelTwoAfter = property[Constituent]("labelTwoAfter") { 
    x: Constituent =>  (tokens(x) ~> constituentTwoAfter).head
  	""
  }

  val L2bL1b = property[Constituent]("label2beforeLabel1beforeConjunction") { 
    x: Constituent => 
	    val before = (tokens(x) ~> constituentBefore).head
	    val twoBefore = (tokens(x) ~> constituentTwoBefore).head
	    ""
  }

  val L1bL1a = property[Constituent]("label1beforeLabel1afterConjunction") { 
  	x: Constituent => 
	    val before = (tokens(x) ~> constituentBefore).head
	    val after = (tokens(x) ~> constituentAfter).head
	    ""
  }

  val L1aL2a = property[Constituent]("labelfterLabel2AfterConjunction") { 
  x: Constituent =>
	    val after = (tokens(x) ~> constituentAfter).head
	    val twoAfter = (tokens(x) ~> constituentTwoAfter).head
	    ""
  }

}
