/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.QuestionTypeClassification

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
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

  def classifySampleQuestions() = {
    val coarseClassifier = new CoarseTypeClassifier(propertyList)
    coarseClassifier.load()
    val fineClassifier = new FineTypeClassifier(propertyList)
    fineClassifier.load()
    import QuestionTypeClassificationSensors._
    val rawQuestions = Seq(
      "How's the weather in Champaign-Urbana?",
      "How far is Champaign to Chicago?",
      "Who found dinasours?", "Which day is Christmas?",
      "What can be cured by cheap pizza?",
      "What can be cured by cheese pizza?",
      "Who is Michael?",
      "When is Easter in 2017?"
    )
    rawQuestions.foreach { q =>
      val ta = pipeline.createBasicTextAnnotation("", "", q)
      pipeline.addView(ta, ViewNames.LEMMA)
      pipeline.addView(ta, ViewNames.POS)
      pipeline.addView(ta, ViewNames.SHALLOW_PARSE)
      pipeline.addView(ta, ViewNames.NER_CONLL)
      val questioin = QuestionTypeInstance(q, None, None, Some(ta))
      println(q)
      println(coarseClassifier(questioin))
      println(fineClassifier(questioin))
    }
  }

  val propertyList = List(
    QuestionTypeClassificationDataModel.surfaceWords,
    QuestionTypeClassificationDataModel.lemma,
    QuestionTypeClassificationDataModel.pos,
    QuestionTypeClassificationDataModel.chunks,
    QuestionTypeClassificationDataModel.headChunks,
    QuestionTypeClassificationDataModel.ner,
    QuestionTypeClassificationDataModel.containsFoodterm,
    QuestionTypeClassificationDataModel.containsMountain,
    QuestionTypeClassificationDataModel.containsProfession,
    QuestionTypeClassificationDataModel.numberNormalizer,
    QuestionTypeClassificationDataModel.wordnetSynsetsFirstSense,
    QuestionTypeClassificationDataModel.wordnetSynsetsAllSenses,
    QuestionTypeClassificationDataModel.wordnetLexicographerFileNamesFirstSense,
    QuestionTypeClassificationDataModel.wordnetLexicographerFileNamesAllSenses,
    QuestionTypeClassificationDataModel.wordnetHypernymFirstSenseLexicographerFileNames,
    QuestionTypeClassificationDataModel.wordnetHypernymAllSensesLexicographerFileNames,
    QuestionTypeClassificationDataModel.wordnetHypernymsFirstSense,
    QuestionTypeClassificationDataModel.wordnetHypernymsAllSenses,
    QuestionTypeClassificationDataModel.wordnetPointersFirstSense,
    QuestionTypeClassificationDataModel.wordnetSynonymsFirstSense,
    QuestionTypeClassificationDataModel.wordnetSynonymsAllSenses,
    QuestionTypeClassificationDataModel.wordnetSynonymsAllSenses,
    QuestionTypeClassificationDataModel.wordGroups
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
      case 3 => classifySampleQuestions()
    }
  }
}
