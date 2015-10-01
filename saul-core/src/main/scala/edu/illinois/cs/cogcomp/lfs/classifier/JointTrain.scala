package edu.illinois.cs.cogcomp.lfs.classifier

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.lbjava.learn.LinearThresholdUnit
import edu.illinois.cs.cogcomp.lfs.data_model.DataModel

import scala.reflect.ClassTag

/** Created by parisakordjamshidi on 29/01/15.
  */
object JointTrain {

  def testClassifiers(cls: Classifier, oracle: Classifier, ds: List[AnyRef]): Unit = {

    val results = ds.map({
      x =>
        val pri = cls.discreteValue(x)
        val truth = oracle.discreteValue(x)
        (pri, truth)
    })

    val tp = results.count({ case (x, y) => x == y && (x == "true") }) * 1.0
    val fp = results.count({ case (x, y) => x != y && (x == "true") }) * 1.0

    val tn = results.count({ case (x, y) => x == y && (x == "false") }) * 1.0
    val fn = results.count({ case (x, y) => x != y && (x == "false") }) * 1.0

    println(s"tp: $tp fp: $fp tn: $tn fn: $fn ")
    println(s" accuracy    ${(tp + tn) / results.size} ")
    println(s" precision   ${tp / (tp + fp)} ")
    println(s" recall      ${tp / (tp + fn)} ")
    println(s" f1          ${(2.0 * tp) / (2 * tp + fp + fn)} ")

  }

  def apply[HEAD <: AnyRef](
    dm: DataModel,
    cls: List[ConstraintClassifier[_, HEAD]]
  )(
    implicit
    headTag: ClassTag[HEAD]
  ) = {

    train[HEAD](dm, cls, 1)
  }

  def apply[HEAD <: AnyRef](
    dm: DataModel,
    cls: List[ConstraintClassifier[_, HEAD]],
    it: Int
  )(
    implicit
    headTag: ClassTag[HEAD]
  ) = {

    train[HEAD](dm, cls, it)
  }

  def train[HEAD <: AnyRef](
    dm: DataModel,
    cls: List[ConstraintClassifier[_, HEAD]],
    it: Int
  )(
    implicit
    headTag: ClassTag[HEAD]
  ): Unit = {
    // forall members in collection of the head (dm.t) do

    println("Training iteration: " + it)
    if (it == 0) {
      // Done
    } else {
      val allHeads = dm.getNodeWithType[HEAD].getTrainingInstances

      allHeads foreach {
        h =>
          {
            cls.foreach {
              case c: ConstraintClassifier[_, HEAD] => {

                type C = c.LEFT
                //              println("-=-=-=-=-")
                //              println(c.tType)
                //              println(c.headType)
                //              println("-=-=-=-=-")

                val typedC = c.asInstanceOf[ConstraintClassifier[_, HEAD]]

                //              println(Console.RED + typedC + Console.RESET)

                val oracle = typedC.onClassifier.getLabeler

                typedC.getCandidates(h) foreach {
                  x =>
                    {
                      //                  println(x)

                      //                  typedC.onClassifier.learn(x)

                      def trainOnce() = {

                        val result = typedC.classifier.discreteValue(x)
                        //                  val result =  typedC.classifier.discreteValue(x)
                        //
                        //                                      println(s"${typedC.onClassifier.scores(x).getScore("true")}")
                        //                                      println(s"${typedC.onClassifier.scores(x).getScore("false")}")

                        val trueLabel = oracle.discreteValue(x)

                        //                  val classifierToTrain = typedC.classifier

                        //                    if(result.equals(trueLabel)){
                        //                      print(Console.GREEN)
                        //                    }else{
                        //                      print(Console.RED)
                        //                    }
                        //                    print(result + "  ??? " + trueLabel)
                        //                    println(Console.RESET)

                        //                  if (result.equals("true") && !typedC.classifier.getLabeler.discreteValue(x).equals("true"))

                        if (result.equals("true") && trueLabel.equals("false")) {
                          //                      println("demote !")
                          //                      println("promote !")
                          val a = typedC.onClassifier.getExampleArray(x)
                          val a0 = a(0).asInstanceOf[Array[Int]]
                          val a1 = a(1).asInstanceOf[Array[Double]]

                          //                      typedC.onClassifier.asInstanceOf[LinearThresholdUnit].demote(a0,a1,0.1)

                          //                    typedC.onClassifier.learn(x)
                          typedC.onClassifier.asInstanceOf[LinearThresholdUnit].promote(a0, a1, 0.1)
                          //                    typedC.onClassifier.asInstanceOf[LinearThresholdUnit].promote(a0,a1,0.1)

                          //                      typedC.onClassifier.learn(x)
                          //                      println("demote !")
                          //                      println("promote !")
                        } else {

                          if (result.equals("false") && trueLabel.equals("true")) {
                            //                        println("demote !")
                            //                        println("promote !")

                            val a = typedC.onClassifier.getExampleArray(x)
                            //.map({
                            val a0 = a(0).asInstanceOf[Array[Int]]
                            val a1 = a(1).asInstanceOf[Array[Double]]
                            //                        typedC.onClassifier.asInstanceOf[LinearThresholdUnit].promote(a0,a1,0.1)
                            //                      typedC.onClassifier.learn(x)
                            typedC.onClassifier.asInstanceOf[LinearThresholdUnit].demote(a0, a1, 0.1)
                            //                      println("promote !")
                            //                        typedC.onClassifier.learn(x)

                            //                      typedC.onClassifier.asInstanceOf[LinearThresholdUnit].demote(a0,a1,0.1)
                          } else {
                            //                                              println("Correct !")
                          }
                          //                    typedC.onClassifier.asInstanceOf[LinearThresholdUnit].doneLearning()

                        }

                      }

                      //                  (0 to 10).foreach{
                      //                    x =>
                      //                      print(x)
                      //                      trainOnce()
                      //                  }
                      //                  println("-----------Start----------------")
                      trainOnce()
                      //                  println("-----------END----------------")
                      //end if demote
                      //                  if (result.equals("false") && oracle.discreteValue(x).equals("true"))
                    }
                }
              }
            }
          }
      }
      train(dm, cls, it - 1)
    }

  }

}
