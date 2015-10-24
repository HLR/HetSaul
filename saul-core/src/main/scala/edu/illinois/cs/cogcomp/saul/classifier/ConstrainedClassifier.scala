package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete
import edu.illinois.cs.cogcomp.lbjava.infer.{ FirstOrderConstraint, InferenceManager }
import edu.illinois.cs.cogcomp.lbjava.learn.Learner
import edu.illinois.cs.cogcomp.lbjava.parse.Parser
import edu.illinois.cs.cogcomp.saul.classifier.infer.InferenceCondition
import edu.illinois.cs.cogcomp.saul.constraint.LfsConstraint
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.lbjrelated.LBJClassifierEquivalent
import edu.illinois.cs.cogcomp.saul.parser.LBJIteratorParserScala

import scala.reflect.ClassTag

abstract class ConstrainedClassifier[T <: AnyRef, HEAD <: AnyRef](val dm: DataModel, val onClassifier: Learner)(
  implicit
  val tType: ClassTag[T],
  implicit val headType: ClassTag[HEAD]
) extends LBJClassifierEquivalent {

  type LEFT = T
  type RIGHT = HEAD

  def __allowableValues: List[String] = "*" :: "*" :: Nil

  def subjectTo: LfsConstraint[HEAD]

  def filter(t: T, head: HEAD): Boolean = true

  val pathToHead: Option[Symbol] = None

  def findHead(x: T): Option[HEAD] = {

    if (tType.equals(headType)) {
      Some(x.asInstanceOf[HEAD])
    } else {
      val lst = pathToHead match {
        case Some(s) =>
          //          println(s"Searching via ${s}")
          dm.getFromRelation[T, HEAD](s, x)
        case _ => dm.getFromRelation[T, HEAD](x)
      }

      val l = lst.toSet.toList

      if (l.isEmpty) {

        None
        //        throw new Exception("Failed to find head")

        //        null.asInstanceOf[HEAD]

      } else {
        if (l.size != 1) {

          //        println( l )

          //        throw new Exception("Find too many heads")
          //        throw new Exception("Find too many heads")
          Some(l.head)

        } else {
          // size == 1

          //        println(s"Found head ${l.head} for child ${x}")

          Some(l.head)
        }

      }

    }
  }

  def getCandidates(head: HEAD): Seq[T] = {

    if (tType.equals(headType)) {
      head.asInstanceOf[T] :: Nil
    } else {
      val l = pathToHead match {
        case Some(s) => dm.getFromRelation[HEAD, T](s, head)
        case _ => dm.getFromRelation[HEAD, T](head)
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
          //      println(inference)
          //      println("Inference NULL" + name)

          InferenceManager.put(name, inference)
        }

        val result: String = inference.valueOf(cls, t)
        result

      case None =>

        val name = String.valueOf(infer.subjectTo.hashCode())

        //        var inference = InferenceManager.get(name, head)
        //
        //        if (inference == null) {
        //
        //          inference = infer(head)
        //          //      println(inference)
        //          //      println("Inference NULL" + name)
        //
        //          InferenceManager.put(name, inference)
        //        }
        //
        //
        //        val result: String = inference.valueOf(cls, t)
        //        result

        //        "false"
        cls.discreteValue(t)

    }

  }

  def buildWithConstrain(infer: InferenceCondition[T, HEAD])(t: T): String = {
    buildWithConstrain(infer, onClassifier)(t)
  }

  def cName: String = this.getClass.getName
  // TODO: Pick a better name
  def lbjClassifier = dm.property[T](cName)("*", "*") {
    x: T => buildWithConstrain(subjectTo.createInferenceCondition[T](this.dm).convertToType[T], onClassifier)(x)
  }

  override val classifier = lbjClassifier.classifier

  //  def test() : Unit = {
  //    this.onClassifier.
  //  }

  def learn(it: Int): Unit = {
    val ds = dm.getNodeWithType[T].getTrainingInstances
    this.learn(it, ds)
  }

  def learn(iteration: Int, data: Iterable[T]): Unit = {
    println()
    //    featureExtractor.setDMforAll(this.datamodel)

    val crTokenTest = new LBJIteratorParserScala[T](data)
    crTokenTest.reset()

    def learnAll(crTokenTest: Parser, remainingIteration: Int): Unit = {
      //      println(remainingIteration)
      val v = crTokenTest.next
      if (v == null) {

        if (remainingIteration > 0) {
          crTokenTest.reset()
          learnAll(crTokenTest, remainingIteration - 1)
        }
      } else {
        //        println("Learning with example " + v)
        this.onClassifier.learn(v)
        learnAll(crTokenTest, remainingIteration)
      }

    }

    learnAll(crTokenTest, iteration)

  }

  def test(): List[(String, (Double, Double, Double))] = {

    val allHeads = this.dm.getNodeWithType[HEAD].getTestingInstances
    //    allHeads foreach( t => println(s"  [HEAD]  Using thie head ${t} "))

    val data: List[T] = if (tType.equals(headType)) {
      allHeads.map(_.asInstanceOf[T]).toList
    } else {
      this.pathToHead match {
        case Some(path) => (allHeads map (h => this.dm.getFromRelation[HEAD, T](path, h))).toList.flatten
        case _ => (allHeads map (h => this.dm.getFromRelation[HEAD, T](h))).toList.flatten
      }

    }

    //    val data : List[T] =

    //    data foreach( t => println(s"  Using thie one ${t} "))

    //    val data =

    //    println(data.size)
    test(data)
  }

  /** Test with given data, use internally
    * @param testData
    * @return List of (label, (f1,precision,recall))
    */
  def test(testData: Iterable[T]): List[(String, (Double, Double, Double))] = {
    println()
    val testReader = new LBJIteratorParserScala[T](testData)
    //    println("Here is the test!")
    testReader.reset()

    //    testData.toList.map{
    //     t : T =>
    //       println(s"Eval ${t}")
    //       (t,classifier.discreteValue(t))
    //    }.foreach(println)

    val tester = TestDiscrete.testDiscrete(classifier, onClassifier.getLabeler, testReader)
    tester.printPerformance(System.out)
    val ret = tester.getLabels.map({
      label => (label, (tester.getF1(label), tester.getPrecision(label), tester.getRecall(label)))
    })

    ret toList
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