/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, FeatureVector, TestDiscrete }
import edu.illinois.cs.cogcomp.infer.ilp.{ GurobiHook, ILPSolver, OJalgoHook }
import edu.illinois.cs.cogcomp.lbjava.infer.{ BalasHook, FirstOrderConstraint, InferenceManager }
import edu.illinois.cs.cogcomp.lbjava.learn.Learner
import edu.illinois.cs.cogcomp.saul.classifier.infer.InferenceCondition
import edu.illinois.cs.cogcomp.saul.constraint.LfsConstraint
import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge
import edu.illinois.cs.cogcomp.saul.lbjrelated.{ LBJClassifierEquivalent, LBJLearnerEquivalent }
import edu.illinois.cs.cogcomp.saul.parser.IterableToLBJavaParser
import edu.illinois.cs.cogcomp.saul.test.TestWithStorage
import edu.illinois.cs.cogcomp.saul.util.Logging
import scala.reflect.ClassTag

/** The input to a ConstrainedClassifier is of type `T`. However given an input, the inference is based upon the
  * head object of type `HEAD` corresponding to the input (of type `T`).
  *
  * @tparam T the object type given to the classifier as input
  * @tparam HEAD the object type inference is based upon
  */
abstract class ConstrainedClassifier[T <: AnyRef, HEAD <: AnyRef](val onClassifier: LBJLearnerEquivalent)(
  implicit
  val tType: ClassTag[T],
  implicit val headType: ClassTag[HEAD]
) extends LBJClassifierEquivalent with Logging {

  type LEFT = T
  type RIGHT = HEAD

  def className: String = this.getClass.getName

  def getClassSimpleNameForClassifier = this.getClass.getSimpleName

  def __allowableValues: List[String] = "*" :: "*" :: Nil

  def subjectTo: LfsConstraint[HEAD]

  def solver: ILPSolver = new GurobiHook()

  /** The function is used to filter the generated candidates from the head object; remember that the inference starts
    * from the head object. This function finds the objects of type `T` which are connected to the target object of
    * type `HEAD`. If we don't define `filter`, by default it returns all objects connected to `HEAD`.
    * The filter is useful for the `JointTraining` when we go over all global objects and generate all contained object
    * that serve as examples for the basic classifiers involved in the `JoinTraining`. It is possible that we do not
    * want to use all possible candidates but some of them, for example when we have a way to filter the negative
    * candidates, this can come in the filter.
    */
  def filter(t: T, head: HEAD): Boolean = true

  /** The `pathToHead` returns only one object of type HEAD, if there are many of them i.e. `Iterable[HEAD]` then it
    * simply returns the `head` of the `Iterable`
    */
  val pathToHead: Option[Edge[T, HEAD]] = None

  /** syntactic sugar to create simple calls to the function */
  def apply(example: AnyRef): String = classifier.discreteValue(example: AnyRef)

  def findHead(x: T): Option[HEAD] = {
    if (tType.equals(headType) || pathToHead.isEmpty) {
      Some(x.asInstanceOf[HEAD])
    } else {
      val l = pathToHead.get.forward.neighborsOf(x).toSet.toList

      if (l.isEmpty) {
        logger.error("Warning: Failed to find head")
        None
      } else if (l.size != 1) {
        logger.warn("Find too many heads")
        Some(l.head)
      } else {
        logger.info(s"Found head ${l.head} for child $x")
        Some(l.head)
      }
    }
  }

  def getCandidates(head: HEAD): Seq[T] = {
    if (tType.equals(headType) || pathToHead.isEmpty) {
      head.asInstanceOf[T] :: Nil
    } else {
      val l = pathToHead.get.backward.neighborsOf(head)

      if (l.isEmpty) {
        logger.error("Failed to find part")
        Seq.empty[T]
      } else {
        l.filter(filter(_, head)).toSeq
      }
    }
  }

  def buildWithConstraint(infer: InferenceCondition[T, HEAD], cls: Learner)(t: T): String = {
    findHead(t) match {
      case Some(head) =>
        val name = String.valueOf(infer.subjectTo.hashCode())
        var inference = InferenceManager.get(name, head)
        if (inference == null) {
          inference = infer(head)
          logger.warn(s"Inference ${name} has not been cached; running inference . . . ")
          InferenceManager.put(name, inference)
        }
        inference.valueOf(cls, t)

      case None =>
        cls.discreteValue(t)
    }
  }

  def buildWithConstraint(inferenceCondition: InferenceCondition[T, HEAD])(t: T): String = {
    buildWithConstraint(inferenceCondition, onClassifier.classifier)(t)
  }

  private def getSolverInstance = solver match {
    case _: OJalgoHook => () => new OJalgoHook()
    case _: GurobiHook => () => new GurobiHook()
    case _: BalasHook => () => new BalasHook()
  }

  override val classifier = new Classifier() {
    override def classify(o: scala.Any) = new FeatureVector(featureValue(discreteValue(o)))
    override def discreteValue(o: scala.Any): String =
      buildWithConstraint(
        subjectTo.createInferenceCondition[T](getSolverInstance()).convertToType[T],
        onClassifier.classifier
      )(o.asInstanceOf[T])
  }

  /** Derives test instances from the data model
    *
    * @return Iterable of test instances for this classifier
    */
  private def deriveTestInstances: Iterable[T] = {
    pathToHead.map(edge => edge.from)
      .orElse({
        onClassifier match {
          case clf: Learnable[T] => Some(clf.node)
          case _ => logger.error("pathToHead is not provided and the onClassifier is not a Learnable!"); None
        }
      })
      .map(node => node.getTestingInstances)
      .getOrElse(Iterable.empty)
  }

  /** Test Constrained Classifier with automatically derived test instances.
    *
    * @return List of (label, (f1,precision,recall))
    */
  def test(): Results = {
    test(deriveTestInstances)
  }

  /** Test with given data, use internally
    *
    * @param testData if the collection of data (which is and Iterable of type T) is not given it is derived from the data model based on its type
    * @param exclude it is the label that we want to exclude for evaluation, this is useful for evaluating the multi-class classifiers when we need to measure overall F1 instead of accuracy and we need to exclude the negative class
    * @param outFile The file to write the predictions (can be `null`)
    * @return List of (label, (f1,precision,recall))
    */
  def test(testData: Iterable[T] = null, outFile: String = null, outputGranularity: Int = 0, exclude: String = ""): Results = {
    println()

    val testReader = new IterableToLBJavaParser[T](if (testData == null) deriveTestInstances else testData)
    testReader.reset()

    val tester: TestDiscrete = new TestDiscrete()
    TestWithStorage.test(tester, classifier, onClassifier.getLabeler, testReader, outFile, outputGranularity, exclude)
    val perLabelResults = tester.getLabels.map {
      label =>
        ResultPerLabel(label, tester.getF1(label), tester.getPrecision(label), tester.getRecall(label),
          tester.getAllClasses, tester.getLabeled(label), tester.getPredicted(label), tester.getCorrect(label))
    }
    val overalResultArray = tester.getOverallStats()
    val overalResult = OverallResult(overalResultArray(0), overalResultArray(1), overalResultArray(2))
    Results(perLabelResults, ClassifierUtils.getAverageResults(perLabelResults), overalResult)
  }
}

object ConstrainedClassifier {
  val ConstraintManager = scala.collection.mutable.HashMap[Int, LfsConstraint[_]]()
  def constraint[HEAD <: AnyRef](f: HEAD => FirstOrderConstraint)(implicit headTag: ClassTag[HEAD]): LfsConstraint[HEAD] = {
    val hash = f.hashCode()
    ConstraintManager.getOrElseUpdate(hash, new LfsConstraint[HEAD] {
      override def makeConstrainDef(x: HEAD): FirstOrderConstraint = f(x)
    }).asInstanceOf[LfsConstraint[HEAD]]
  }
}
