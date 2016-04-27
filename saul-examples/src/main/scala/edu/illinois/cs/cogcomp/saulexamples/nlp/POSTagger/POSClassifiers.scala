package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.lbj.pos.POSBaselineLearner
import edu.illinois.cs.cogcomp.lbjava.learn.{ SparseAveragedPerceptron, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSDataModel._

object POSClassifiers {
  /** After POSTaggerKnown and POSTaggerUnknown are trained,
    * this classifier will return the prediction of POSTaggerKnown if
    * the input word was observed during training or of POSTaggerUnknown
    * if it wasn't.
    */
  def POSClassifier(x: Constituent): String = {
    if (BaselineClassifier.classifier.observed(wordForm(x)))
      POSTaggerKnown.classifier.valueOf(x, BaselineClassifier.classifier.allowableTags(wordForm(x))).getStringValue
    else
      POSTaggerUnknown.classifier.valueOf(x, MikheevClassifier.classifier.allowableTags(x)).getStringValue
  }

  object POSTaggerKnown extends Learnable[Constituent](tokens) {
    def label = POSLabel
    override def feature = using(wordForm, baselineTarget, labelTwoBefore, labelOneBefore,
      labelOneAfter, labelTwoAfter, L2bL1b, L1bL1a, L1aL2a)
    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 2
      baseLTU = new SparseAveragedPerceptron(p)
    }
    override val logging = true
  }

  object POSTaggerUnknown extends Learnable[Constituent](tokens) {
    def label = POSLabel
    override def feature = using(wordForm, baselineTarget, labelTwoBeforeU, labelOneBeforeU,
      labelOneAfterU, labelTwoAfterU, L2bL1bU, L1bL1aU, L1aL2aU, suffixFeatures)
    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 4
      baseLTU = new SparseAveragedPerceptron(p)
    }
    override val logging = true
  }

  object BaselineClassifier extends Learnable[Constituent](tokens) {
    def label = POSLabel
    override def feature = using(wordForm)
    override lazy val classifier = new POSBaselineLearner()
    override val logging = true
  }

  object MikheevClassifier extends Learnable[Constituent](tokens) {
    def label = POSLabel
    override def feature = using(wordForm)
    override lazy val classifier = new MikheevLearner
    override val logging = true
  }
}
