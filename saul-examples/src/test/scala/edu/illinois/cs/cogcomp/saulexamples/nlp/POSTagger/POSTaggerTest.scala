package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers.{ POSTaggerKnown, BaselineClassifier }
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger._
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator

import org.scalatest._

import scala.collection.JavaConversions._

class POSTaggerTest extends FlatSpec with Matchers {

  import POSDataModel._

  "POSTager feature queries " should " should work. " in {
    val dummyData = DummyTextAnnotationGenerator.generateAnnotatedTextAnnotation(Array(ViewNames.POS), false)
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
    POSDataModel.labelshoul TwoAfter(consConstruction) should be("DT")
    POSDataModel.labelTwoAfter(consOf) should be("NN")

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
    POSDataModel.labelTwoAfter(consOf) should be("NN")
    // prediction labels
    POSTaggerKnown.isTraining = false
    POSDataModel.labelTwoAfter(consThe) should be("IN")
    POSDataModel.labelTwoAfter(consConstruction) should be("DT")
    POSDataModel.labelTwoAfter(consOf) should be("NN")
  }
}
