package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saulexamples.data.DocumentReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamClassifiers.{ spamClassifierWithCache, deserializedSpamClassifier, spamClassifier }

import scala.collection.JavaConversions._

object SpamApp {

  val trainData = new DocumentReader("../data/EmailSpam/train").docs.toList
  val testData = new DocumentReader("../data/EmailSpam/test").docs.toList

  object SpamExperimentType extends Enumeration {
    val TrainAndTest, CacheGraph, TestUsingGraphCache, TestSerializatin = Value
  }

  def main(args: Array[String]): Unit = {
    /** Choose the experiment you're interested in by changing the following line */
    val testType = SpamExperimentType.TrainAndTest

    testType match {
      case SpamExperimentType.TrainAndTest => TrainAndTestSpamClassifier()
      case SpamExperimentType.CacheGraph => SpamClassifierWithGraphCache()
      case SpamExperimentType.TestUsingGraphCache => SpamClassifierFromCache()
      case SpamExperimentType.TestSerializatin => SpamClassifierWithSerialization()
    }
  }

  /** A standard method for testing the Spam Classification problem. Simply training and testing the resulting model.*/
  def TrainAndTestSpamClassifier(): Unit = {
    /** Defining the data and specifying it's location  */
    spamDataModel.docs populate trainData
    spamClassifier.learn(30)
    spamDataModel.testWith(testData)
    spamClassifier.test(testData)
  }

  /** Spam Classifcation, followed by caching the data-model graph. */
  val graphCacheFile = "models/temp.model"
  def SpamClassifierWithGraphCache(): Unit = {
    /** Defining the data and specifying it's location  */
    spamDataModel.docs populate trainData
    spamDataModel.deriveInstances()
    spamDataModel.write(graphCacheFile)

    spamClassifierWithCache.learn(30)
    spamClassifierWithCache.learn(30)
    spamDataModel.testWith(testData)
    spamClassifierWithCache.test(testData)
  }

  /** Testing the functionality of the cache. `SpamClassifierWithCache` produces the temporary model file need for
    * this methdd to run.
    */
  def SpamClassifierFromCache() {
    spamDataModel.load(graphCacheFile)
    spamClassifierWithCache.learn(30)
    spamClassifierWithCache.learn(30)
    spamDataModel.testWith(testData)
    spamClassifierWithCache.test(testData)
  }

  /** Testing the serialization functionality of the model. We first train a model and save it. Then we load the model
    * and use it for prediction. We later check whether the predictions of the deserialized model are the same as the
    * predictions before serialization.
    */
  def SpamClassifierWithSerialization(): Unit = {
    spamDataModel.docs populate trainData
    spamClassifier.learn(30)

    spamClassifier.save()

    println(deserializedSpamClassifier.classifier.getPrunedLexiconSize)
    deserializedSpamClassifier.load(spamClassifier.lcFilePath, spamClassifier.lexFilePath)

    val predictionsBeforeSerialization = testData.map(spamClassifier(_))
    val predictionsAfterSerialization = testData.map(deserializedSpamClassifier(_))
    println(predictionsBeforeSerialization.mkString("/"))
    println(predictionsAfterSerialization.mkString("/"))
    println(predictionsAfterSerialization.indices.forall(it => predictionsBeforeSerialization(it) == predictionsAfterSerialization(it)))
  }
}
