package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, Classifier, TestDiscrete }
import edu.illinois.cs.cogcomp.lbjava.infer.{ FirstOrderConstraint, InferenceManager }
import edu.illinois.cs.cogcomp.lbjava.learn.Learner
import edu.illinois.cs.cogcomp.lbjava.parse.Parser
import edu.illinois.cs.cogcomp.saul.classifier.infer.InferenceCondition
import edu.illinois.cs.cogcomp.saul.constraint.LfsConstraint
import edu.illinois.cs.cogcomp.saul.datamodel.edge.Edge
import edu.illinois.cs.cogcomp.saul.lbjrelated.LBJClassifierEquivalent
import edu.illinois.cs.cogcomp.saul.parser.LBJIteratorParserScala

import scala.reflect.ClassTag

abstract class ConstrainedClassifier[T <: AnyRef, HEAD <: AnyRef](val edge: Edge[T, HEAD], val onClassifier: Learner)(
  implicit
  val tType: ClassTag[T],
  implicit val headType: ClassTag[HEAD]
) extends LBJClassifierEquivalent {

  type LEFT = T
  type RIGHT = HEAD

  def __allowableValues: List[String] = "*" :: "*" :: Nil

  def subjectTo: LfsConstraint[HEAD]

  def filter(t: T, head: HEAD): Boolean = true

  val pathToHead: Option[Edge[T, HEAD]] = None

  def findHead(x: T): Option[HEAD] = {
    if (tType.equals(headType)) {
      Some(x.asInstanceOf[HEAD])
    } else {
      val lst = pathToHead match {
        case Some(e) => e.forward.neighborsOf(x)
        case _ => edge.forward.neighborsOf(x)
      }

      val l = lst.toSet.toList

      if (l.isEmpty) None
      else {
        if (l.size != 1) Some(l.head)
        else Some(l.head)
      }
    }
  }

  def getCandidates(head: HEAD): Seq[T] = {
    if (tType.equals(headType)) {
      head.asInstanceOf[T] :: Nil
    } else {
      val l = pathToHead match {
        case Some(e) => e.backward.neighborsOf(head)
        case _ => edge.backward.neighborsOf(head)
      }

      if (l.isEmpty) {
        throw new Exception("Failed to find part")
      } else {
        l.filter(filter(_, head)).toSeq
      }
    }
  }

  def buildWithConstrain(infer: InferenceCondition[T, HEAD], cls: Learner)(t: T): String = {
    findHead(t) match {
      case Some(head) =>
        val name = String.valueOf(infer.subjectTo.hashCode())

        var inference = InferenceManager.get(name, head)

        if (inference == null) {
          inference = infer(head)
          InferenceManager.put(name, inference)
        }

        val result: String = inference.valueOf(cls, t)
        result

      case None =>
        cls.discreteValue(t)
    }
  }

  def buildWithConstrain(infer: InferenceCondition[T, HEAD])(t: T): String = {
    buildWithConstrain(infer, onClassifier)(t)
  }

  def cName: String = this.getClass.getName

  override val classifier = new Classifier() {
    override def classify(o: scala.Any): FeatureVector = new FeatureVector(featureValue(discreteValue(o)))
    override def discreteValue(o: scala.Any): String =
      buildWithConstrain(subjectTo.createInferenceCondition[T].convertToType[T], onClassifier)(o.asInstanceOf[T])
  }

  def learn(it: Int): Unit = {
    val ds = edge.from.getTrainingInstances
    this.learn(it, ds)
  }

  def learn(iteration: Int, data: Iterable[T]): Unit = {
    println()

    val crTokenTest = new LBJIteratorParserScala[T](data)
    crTokenTest.reset()

    def learnAll(crTokenTest: Parser, remainingIteration: Int): Unit = {
      val v = crTokenTest.next
      if (v == null) {
        if (remainingIteration > 0) {
          crTokenTest.reset()
          learnAll(crTokenTest, remainingIteration - 1)
        }
      } else {
        this.onClassifier.learn(v)
        learnAll(crTokenTest, remainingIteration)
      }
    }
    learnAll(crTokenTest, iteration)

  }

  def test(): List[(String, (Double, Double, Double))] = {
    val allHeads: List[HEAD] = edge.to.getTestingInstances.toList

    val data: List[T] = if (tType.equals(headType)) {
      allHeads.map(_.asInstanceOf[T])
    } else {
      this.pathToHead match {
        case Some(path) => allHeads.flatMap(h => path.backward.neighborsOf(h))
        case _ => allHeads.flatMap(h => edge.backward.neighborsOf(h))
      }
    }
    test(data)
  }

  /** Test with given data, use internally
    *
    * @param testData The test data
    * @return List of (label, (f1,precision,recall))
    */
  def test(testData: Iterable[T]): List[(String, (Double, Double, Double))] = {
    println()
    val testReader = new LBJIteratorParserScala[T](testData)
    testReader.reset()
    val tester = TestDiscrete.testDiscrete(classifier, onClassifier.getLabeler, testReader)
    tester.printPerformance(System.out)
    val ret = tester.getLabels.map({
      label => (label, (tester.getF1(label), tester.getPrecision(label), tester.getRecall(label)))
    })

    ret.toList
  }
}

object ConstrainedClassifier {
  val ConstraintManager = scala.collection.mutable.HashMap[Int, LfsConstraint[_]]()

  def constraintOf[HEAD <: AnyRef](f: HEAD => FirstOrderConstraint)(implicit headTag: ClassTag[HEAD]): LfsConstraint[HEAD] = {

    val hash = f.hashCode()

    ConstraintManager.getOrElseUpdate(hash, new LfsConstraint[HEAD] {
      override def makeConstrainDef(x: HEAD): FirstOrderConstraint = f(x)
    }).asInstanceOf[LfsConstraint[HEAD]]
  }
}