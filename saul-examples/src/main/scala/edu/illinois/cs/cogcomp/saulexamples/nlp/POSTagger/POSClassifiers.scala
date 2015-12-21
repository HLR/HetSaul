package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.lbj.pos.POSBaselineLearner
import edu.illinois.cs.cogcomp.lbjava.learn.{ SparseAveragedPerceptron, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._

import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSDataModel._

object POSClassifiers {
  /** After POSTaggerKnown and POSTaggerUnknown are trained,
    * this classifier will return the prediction of POSTaggerKnown if
    * the input word was observed during training or of POSTaggerUnknown
    * if it wasn't.
    */
  def POSClassifier(x: Constituent) {
    if (BaselineClassifier.classifier.observed(x.toString)) POSTaggerKnown.classifier.discreteValue(x)
    else POSTaggerUnknown.classifier.discreteValue(x)
  }

  object POSTaggerKnown extends Learnable[Constituent](POSDataModel) {
    def label = POSLabel
    override def feature = using(wordForm, labelOrBaseline, labelTwoBefore, labelOneBefore,
      labelOneAfter, labelTwoAfter, L2bL1b, L1bL1a, L1aL2a)
    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 2
      baseLTU = new SparseAveragedPerceptron(p)
    }
    override val loggging = true
  }

  object POSTaggerUnknown extends Learnable[Constituent](POSDataModel) {
    def label = POSLabel
    override def feature = using(wordForm, labelOrBaselineU, labelTwoBeforeU, labelOneBeforeU,
      labelOneAfterU, labelTwoAfterU, L2bL1bU, L1bL1aU, L1aL2aU)
    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 4
      baseLTU = new SparseAveragedPerceptron(p)
    }
    override val loggging = true
  }

  object BaselineClassifier extends Learnable[Constituent](POSDataModel) {
    def label = POSLabel
    override def feature = using(wordForm)
    override lazy val classifier = new POSBaselineLearner()
    override val loggging = true
  }

  object MikheevClassifier extends Learnable[Constituent](POSDataModel) {
    def label = POSLabel
    override def feature = using(wordForm)
    override lazy val classifier = new MikheevLearner
    override val loggging = true
  }
}
