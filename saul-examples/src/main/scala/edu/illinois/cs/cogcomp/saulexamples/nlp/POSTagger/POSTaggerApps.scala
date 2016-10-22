/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.core.utilities.configuration.{ Configurator, Property, ResourceManager }
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete
import edu.illinois.cs.cogcomp.nlp.corpusreaders.PennTreebankPOSReader
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder
import edu.illinois.cs.cogcomp.pos.POSLabeledUnknownWordParser
import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saul.parser.IterableToLBJavaParser
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors

import scala.collection.JavaConversions._
import scala.io.StdIn

object POSConfigurator extends Configurator {
  private val prefix = "../data/POS/"
  val trainData = new Property("trainData", prefix + "00-18.br")
  val trainDataSmall = new Property("trainDataSmall", prefix + "00-18_small.br")
  val trainAndDevData = new Property("trainAndDevData", prefix + "00-21.br")
  val testData = new Property("testData", prefix + "22-24.br")
  // models from the "saul-pos-tagger-models" jar package
  val jarModelPath = "models/edu/illinois/cs/cogcomp/saulexamples/nlp/POSTagger/models/"
  override def getDefaultConfig: ResourceManager = {
    val props = Array(trainData, trainDataSmall, trainAndDevData, testData)
    new ResourceManager(generateProperties(props))
  }
}

object POSTaggerApp {
  object POSExperimentType extends Enumeration {
    val TrainAndTest, TestFromModel, Interactive = Value
  }

  def main(args: Array[String]): Unit = {
    /** Choose the experiment you're interested in by changing the following line */
    val testType = POSExperimentType.Interactive

    testType match {
      case POSExperimentType.TrainAndTest => trainAndTest()
      case POSExperimentType.TestFromModel => testWithPretrainedModels()
      case POSExperimentType.Interactive => interactiveWithPretrainedModels()
    }
  }

  /** Reading test and train data */
  lazy val (trainData, testData) = {
    val trainDataReader = new PennTreebankPOSReader("train")
    trainDataReader.readFile(POSConfigurator.trainAndDevData.value)

    var sentenceId = 0
    val trainData = trainDataReader.getTextAnnotations.flatMap(p => {
      val cons = CommonSensors.textAnnotationToTokens(p)
      sentenceId += 1
      //      Adding a dummy attribute so that hashCode is different for each constituent
      cons.foreach(c => c.addAttribute("SentenceId", sentenceId.toString))
      cons
    })

    val testDataReader = new PennTreebankPOSReader("test")
    testDataReader.readFile(POSConfigurator.testData.value)
    val testData = testDataReader.getTextAnnotations.flatMap(p => {
      val cons = CommonSensors.textAnnotationToTokens(p)
      sentenceId += 1
      //      Adding a dummy attribute so that hashCode is different for each constituent
      cons.foreach(c => c.addAttribute("SentenceId", sentenceId.toString))
      cons
    })
    (trainData, testData)
  }

  def trainAndTest(): Unit = {
    POSDataModel.tokens populate trainData
    POSDataModel.tokens.populate(testData, train = false)

    /** pre-process the baseline systems */
    BaselineClassifier.learn(1)
    MikheevClassifier.learn(1)

    /** train the learning models */
    val unknownTrainData = trainData.filter(x => BaselineClassifier.classifier.observedCount(wordForm(x)) <= POSLabeledUnknownWordParser.threshold)
    POSTaggerKnown.learn(50)
    POSTaggerUnknown.learn(50, unknownTrainData)

    /** test the resulting model */
    testPOSTagger()

    // saving all the models
    ClassifierUtils.SaveClassifiers(BaselineClassifier, MikheevClassifier, POSTaggerKnown, POSTaggerUnknown)
  }

  /** Loading the serialized models as a dependency */
  def testWithPretrainedModels(): Unit = {
    POSDataModel.tokens.populate(testData, train = false)
    ClassifierUtils.LoadClassifier(
      POSConfigurator.jarModelPath,
      BaselineClassifier, MikheevClassifier, POSTaggerKnown, POSTaggerUnknown
    )
    testPOSTagger()
  }

  def testPOSTagger(): Unit = {
    val tester = new TestDiscrete
    val testReader = new IterableToLBJavaParser[Constituent](testData)
    testReader.reset()

    testReader.data.foreach(cons => {
      val gold = POSDataModel.POSLabel(cons)
      val predicted = POSClassifiers.POSClassifier(cons)
      tester.reportPrediction(predicted, gold)
    })

    tester.printPerformance(System.out)
  }

  /** Load Pretrained models into classifiers and return a POSAnnotator
    */
  def getPretrainedAnnotator(finalViewName: String = ViewNames.POS): POSAnnotator = {
    // Load POSClassifier.
    ClassifierUtils.LoadClassifier(
      POSConfigurator.jarModelPath,
      BaselineClassifier, MikheevClassifier, POSTaggerKnown, POSTaggerUnknown
    )

    val annotator = new POSAnnotator(finalViewName)
    val resourceManager = POSConfigurator.getDefaultConfig
    annotator.initialize(resourceManager)

    annotator
  }

  /** Interactive model to annotate input sentences with Pre-trained models
    */
  def interactiveWithPretrainedModels(): Unit = {
    val annotator = getPretrainedAnnotator()
    val taBuilder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer())

    while (true) {
      println("Enter a sentence to annotate (or Press Enter to exit)")
      val input = StdIn.readLine()

      input match {
        case sentence: String if sentence.trim.nonEmpty =>
          // Create a Text Annotation with the current input sentence.
          val ta = taBuilder.createTextAnnotation(sentence.trim)
          annotator.addView(ta)
          println(ta.getView(ViewNames.POS).toString)
        case _ => return
      }
    }
  }
}
