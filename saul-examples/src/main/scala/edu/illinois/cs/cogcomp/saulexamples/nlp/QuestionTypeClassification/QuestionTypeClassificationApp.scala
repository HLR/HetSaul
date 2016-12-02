/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.QuestionTypeClassification

import edu.illinois.cs.cogcomp.saulexamples.nlp.QuestionTypeClassification.QuestionTypeClassificationClassifiers._
import org.rogach.scallop._

object QuestionTypeClassificationApp {

  class ArgumentParser(args: Array[String]) extends ScallopConf(args) {
    val experimentType: ScallopOption[Int] = opt[Int]("type", descr = "Experiment type", required = true)
    verify()
  }

  def bothLabelClassifierWithBOW(): Unit = {
    val classifier = new CoarseFineTypeClassifier(QuestionTypeClassificationDataModel.surfaceWords)
    
  }

  def main(args: Array[String]): Unit = {
    val parser = new ArgumentParser(args)
    parser.experimentType() match {
      case 1 =>
      case 2 =>
      case 3 =>
    }
  }
}
