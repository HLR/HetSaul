/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers._
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator

import org.scalatest._

import scala.collection.JavaConversions._

class POSTaggerTest extends FlatSpec with Matchers {

  "POSTager feature queries " should " work. " in {
    val dummyData = DummyTextAnnotationGenerator.generateAnnotatedTextAnnotation(Array(ViewNames.POS), false, 1)
    val cons = dummyData.getView(ViewNames.TOKENS).getConstituents
    POSDataModel.tokens populate cons

    val consThe = cons(0)
    val consConstruction = cons(1)
    val consOf = cons(2)

    // wordForm
    POSDataModel.wordForm(consThe) should be("The")
    POSDataModel.wordForm(consConstruction) should be("construction")
    POSDataModel.wordForm(consOf) should be("of")

    // label
    POSDataModel.POSLabel(consThe) should be("DT")
    POSDataModel.POSLabel(consConstruction) should be("NN")
    POSDataModel.POSLabel(consOf) should be("IN")

    // label or baseline
    BaselineClassifier.learn(1)
    POSDataModel.labelOrBaseline(consThe) should be("DT")
    POSDataModel.labelOrBaseline(consConstruction) should be("NN")
    POSDataModel.labelOrBaseline(consOf) should be("IN")

    POSTaggerKnown.isTraining = true
    POSTaggerKnown.learn(10)

    // labelOneBefore
    // gold labels
    POSTaggerKnown.isTraining = true
    POSDataModel.labelOneBefore(consThe) should be("")
    POSDataModel.labelOneBefore(consConstruction) should be("DT")
    POSDataModel.labelOneBefore(consOf) should be("NN")
    // prediction labels
    POSTaggerKnown.isTraining = false
    POSDataModel.labelOneBefore(consThe) should be("")
    POSDataModel.labelOneBefore(consConstruction) should be("DT")
    POSDataModel.labelOneBefore(consOf) should be("NN")

    // labelTwoBefore
    // gold labels
    POSTaggerKnown.isTraining = true
    POSDataModel.labelTwoBefore(consThe) should be("")
    POSDataModel.labelTwoBefore(consConstruction) should be("")
    POSDataModel.labelTwoBefore(consOf) should be("DT")
    // prediction labels
    POSTaggerKnown.isTraining = false
    POSDataModel.labelTwoAfter(consThe) should be("IN")
    POSDataModel.labelTwoAfter(consConstruction) should be("DT")
    POSDataModel.labelTwoAfter(consOf) should be("NNP")

    // labelOneAfter
    // gold labels
    POSTaggerKnown.isTraining = true
    POSDataModel.labelOneAfter(consThe) should be("NN")
    POSDataModel.labelOneAfter(consConstruction) should be("IN")
    POSDataModel.labelOneAfter(consOf) should be("DT")
    // prediction labels
    POSTaggerKnown.isTraining = false
    POSDataModel.labelOneAfter(consThe) should be("NN")
    POSDataModel.labelOneAfter(consConstruction) should be("IN")
    POSDataModel.labelOneAfter(consOf) should be("DT")

    // labelTwoAfter
    // gold labels
    POSTaggerKnown.isTraining = true
    POSDataModel.labelTwoAfter(consThe) should be("IN")
    POSDataModel.labelTwoAfter(consConstruction) should be("DT")
    POSDataModel.labelTwoAfter(consOf) should be("NNP")
    // prediction labels
    POSTaggerKnown.isTraining = false
    POSDataModel.labelTwoAfter(consThe) should be("IN")
    POSDataModel.labelTwoAfter(consConstruction) should be("DT")
    POSDataModel.labelTwoAfter(consOf) should be("NNP")
  }

  val labelMap = Map("The" -> "DT", "construction" -> "NN", "of" -> "IN", "the" -> "DT",
    "John" -> "NNP", "Smith" -> "NNP", "library" -> "NN", "finished" -> "VBD", "on" -> "IN",
    "time" -> "NN", "." -> ".")
  val dummyData = DummyTextAnnotationGenerator.generateAnnotatedTextAnnotation(Array(ViewNames.POS), false, 1)
  val toyConstituents = {
    val cons = dummyData.getView(ViewNames.TOKENS).getConstituents

    // population in the datamodel and loading the models are included here,
    // to make sure they will run before making predictions on any of the constituents
    POSDataModel.tokens.populate(cons, train = false)
    ClassifierUtils.LoadClassifier(
      POSConfigurator.jarModelPath,
      BaselineClassifier, MikheevClassifier, POSTaggerKnown, POSTaggerUnknown
    )
    cons
  }

  "POSBaseline " should " work. " in {
    val score = toyConstituents.map { cons =>
      val pred = BaselineClassifier(cons)
      if (pred == labelMap.getOrElse(cons.getSurfaceForm, pred)) 1.0 else 0.0
    }.sum / toyConstituents.length
    score should be(0.95 +- 0.05)
  }

  "POSUnknown " should " work. " in {
    val score = toyConstituents.map { cons =>
      val pred = POSTaggerUnknown(cons)
      if (pred == labelMap.getOrElse(cons.getSurfaceForm, pred)) 1.0 else 0.0
    }.sum / toyConstituents.length
    score should be(0.95 +- 0.05)
  }

  "POS combined classifier " should "  work. " in {
    val score = toyConstituents.map { cons =>
      val pred = POSClassifiers.POSClassifier(cons)
      if (pred == labelMap.getOrElse(cons.getSurfaceForm, pred)) 1.0 else 0.0
    }.sum / toyConstituents.length
    score should be(0.95 +- 0.05)
  }
}
