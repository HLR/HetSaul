package edu.illinois.cs.cogcomp.saulexamples.TestClassifier

import TestClassifierDataModel._
import scala.collection.JavaConversions._
import TestClassifiers._

object TestClassifierApp extends App {

  val trainData = new TestClassifierReader("data/TestClassifier/train.txt").getDatafromFile()
  val testData = new TestClassifierReader("data/TestClassifier/test.txt").getDatafromFile()

  tcData.populate(trainData)
  tcData.populate(testData, false)

  //  TestClassifierSVM.learn(5)
  //  TestClassifierSVM.test()

  TestClassifierSN2.learn(5)
  TestClassifierSN2.test()

  TestClassifierSN.learn(5)
  TestClassifierSN.test()
}