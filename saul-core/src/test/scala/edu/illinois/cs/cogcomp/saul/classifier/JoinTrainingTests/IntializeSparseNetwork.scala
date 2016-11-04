package edu.illinois.cs.cogcomp.saul.classifier.JoinTrainingTests

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.lbjava.infer.FirstOrderConstant
import edu.illinois.cs.cogcomp.lbjava.learn.{ LinearThresholdUnit, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.classifier.{ JointTrainSparseNetwork, ClassifierUtils, ConstrainedClassifier, Learnable }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{ FlatSpec, Matchers }

/** Created by Parisa on 9/18/16.
  */
class InitializeSparseNetwork extends FlatSpec with Matchers {

  // Testing the original functions with real classifiers
  "integration test" should "work" in {
    // Initialize toy model
    import TestModel._
    object TestClassifier extends Learnable(tokens) {
      def label = testLabel
      override def feature = using(word)
      override lazy val classifier = new SparseNetworkLearner()
    }
    object TestClassifierWithExtendedFeatures extends Learnable(tokens) {
      def label = testLabel
      override def feature = using(word, biWord)
      override lazy val classifier = new SparseNetworkLearner()
    }
    object TestConstraintClassifier extends ConstrainedClassifier[String, String](TestClassifier) {
      def subjectTo = ConstrainedClassifier.constraint { _ => new FirstOrderConstant(true) }
      override val solver = new OJalgoHook
    }
    object TestConstraintClassifierWithExtendedFeatures extends ConstrainedClassifier[String, String](TestClassifierWithExtendedFeatures) {
      def subjectTo = ConstrainedClassifier.constraint { _ => new FirstOrderConstant(true) }
      override val solver = new OJalgoHook
    }

    val words = List("this", "is", "a", "test", "candidate", ".")
    tokens.populate(words)

    val cls = List(TestConstraintClassifier, TestConstraintClassifierWithExtendedFeatures)

    TestConstraintClassifier.onClassifier.classifier.getLexicon.size() should be(0)
    TestConstraintClassifierWithExtendedFeatures.onClassifier.classifier.getLexicon.size() should be(0)
    TestConstraintClassifier.onClassifier.classifier.getLabelLexicon.size() should be(0)
    TestConstraintClassifierWithExtendedFeatures.onClassifier.classifier.getLabelLexicon.size() should be(0)

    val clNet1 = TestConstraintClassifier.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
    val clNet2 = TestConstraintClassifierWithExtendedFeatures.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]

    clNet1.getNetwork.size() should be(0)
    clNet2.getNetwork.size() should be(0)

    ClassifierUtils.InitializeClassifiers(tokens, cls: _*)

    TestConstraintClassifier.onClassifier.classifier.getLexicon.size() should be(6)
    TestConstraintClassifierWithExtendedFeatures.onClassifier.classifier.getLexicon.size() should be(12)
    TestConstraintClassifier.onClassifier.classifier.getLabelLexicon.size() should be(2)
    TestConstraintClassifierWithExtendedFeatures.onClassifier.classifier.getLabelLexicon.size() should be(2)

    clNet1.getNetwork.size() should be(2)
    clNet2.getNetwork.size() should be(2)

    val wv1 = clNet1.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getWeightVector
    val wv2 = clNet2.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getWeightVector

    wv1.size() should be(0)
    wv2.size() should be(0)
    TestClassifierWithExtendedFeatures.learn(2)
    JointTrainSparseNetwork.train(tokens, cls, 5, false)

    val wv1After = clNet1.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getWeightVector
    val wv2After = clNet2.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getWeightVector

    wv1After.size() should be(5)
    wv2After.size() should be(12)
  }

  object TestModel extends DataModel {
    val tokens = node[String]
    val iEdge = edge(tokens, tokens)
    val testLabel = property(tokens) { x: String => x.equals("candidate") }
    val word = property(tokens) { x: String => x }
    val biWord = property(tokens) { x: String => x + "-" + x }
  }
}

