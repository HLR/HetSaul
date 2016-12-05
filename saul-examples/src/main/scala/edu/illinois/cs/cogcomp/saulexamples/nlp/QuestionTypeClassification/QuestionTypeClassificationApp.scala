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

  def populateInstances() = {
    QuestionTypeClassificationDataModel.question.populate(QuestionTypeClassificationSensors.trainInstances)
    QuestionTypeClassificationDataModel.question.populate(QuestionTypeClassificationSensors.testInstances, train = false)
  }

  def evaluate(classifier: TypeClassifier) = {
    populateInstances()
    classifier.learn(20)
    classifier.test()
  }

  val propertyList = List(
    //    QuestionTypeClassificationDataModel.surfaceWords ,
    //        QuestionTypeClassificationDataModel.lemma
//    QuestionTypeClassificationDataModel.pos //,
//      QuestionTypeClassificationDataModel.chunks//,
//      QuestionTypeClassificationDataModel.headChunks //,
//      QuestionTypeClassificationDataModel.ner//,
//      QuestionTypeClassificationDataModel.containsFoodterm,
//      QuestionTypeClassificationDataModel.containsMountain,
//      QuestionTypeClassificationDataModel.containsProfession
  //    QuestionTypeClassificationDataModel.numberNormalizer,
      QuestionTypeClassificationDataModel.wordnetSynsetsFirstSense//,
  //    QuestionTypeClassificationDataModel.wordnetLexicographerFileNamesFirstSense,
  //    QuestionTypeClassificationDataModel.wordnetHypernymFirstSenseLexicographerFileNames,
  //    QuestionTypeClassificationDataModel.wordnetHypernymsFirstSense,
  //    QuestionTypeClassificationDataModel.wordnetMemberHolonymsFirstSense,
  //    QuestionTypeClassificationDataModel.wordnetPartHolonymsFirstSenseLexicographerFileNames,
  //    QuestionTypeClassificationDataModel.wordnetPartHolonymsFirstSense,
  //    QuestionTypeClassificationDataModel.wordnetPointersFirstSense,
  //    QuestionTypeClassificationDataModel.wordnetSubstanceHolonymsFirstSense,
  //    QuestionTypeClassificationDataModel.wordnetSynonymsFirstSense,
  //    QuestionTypeClassificationDataModel.wordnetVerbFramesFirstSenses,
  //    QuestionTypeClassificationDataModel.wordGroups
  //
  )

  def coarseClassifier(): Unit = {
    val classifier = new CoarseTypeClassifier(propertyList)
    evaluate(classifier)
  }

  def fineClassifier(): Unit = {
    val classifier = new FineTypeClassifier(propertyList)
    evaluate(classifier)
  }

  def main(args: Array[String]): Unit = {
    val parser = new ArgumentParser(args)
    parser.experimentType() match {
      case 1 => coarseClassifier()
      case 2 => fineClassifier()
      case 3 =>
    }
  }
}
