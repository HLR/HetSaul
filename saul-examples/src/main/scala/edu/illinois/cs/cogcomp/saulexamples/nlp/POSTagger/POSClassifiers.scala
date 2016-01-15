package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.lbj.pos.POSBaselineLearner
import edu.illinois.cs.cogcomp.lbjava.learn.{ SparseAveragedPerceptron, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
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

  // Loads learned models from the "saul-pos-tagger-models" jar package
  def loadModelsFromPackage(): Unit = {
    val jarModelPath = "edu/illinois/cs/cogcomp/saulexamples/nlp/POSTagger/models/"

    def loadModel(x: Learnable[Constituent]): Unit = {
      val prefix = jarModelPath + x.getClassNameForClassifier
      x.load(prefix + ".lc", prefix + ".lex")
    }

    loadModel(BaselineClassifier)
    loadModel(MikheevClassifier)
    loadModel(POSTaggerKnown)
    loadModel(POSTaggerUnknown)
  }

  def loadSavedModels(): Unit = {
    BaselineClassifier.load()
    MikheevClassifier.load()
    POSTaggerKnown.load()
    POSTaggerUnknown.load()
  }

  def saveModels(): Unit = {
    BaselineClassifier.save()
    MikheevClassifier.save()
    POSTaggerKnown.save()
    POSTaggerUnknown.save()
  }

  object POSTaggerKnown extends Learnable[Constituent](POSDataModel) {
    def label = POSLabel
    override def feature = using(wordForm, baselineTarget, labelTwoBefore, labelOneBefore,
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
    override def feature = using(wordForm, baselineTarget, labelTwoBeforeU, labelOneBeforeU,
      labelOneAfterU, labelTwoAfterU, L2bL1bU, L1bL1aU, L1aL2aU, suffixFeatures)
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
