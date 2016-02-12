package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.lbjava.learn.{ LinearThresholdUnit, Learner }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import scala.reflect.ClassTag
/** Created by Parisa on 5/22/15.
  */
object JoinTrainSparseNetwork {

  /* def testClassifiers(cls : Classifier ,oracle : Classifier, ds : List[AnyRef]) : Unit = {

      val results = ds.map( {
        x =>
          //        println(x)
          val pri = cls.discreteValue(x)
          val truth =  oracle.discreteValue(x)
          //        println((pri,truth))



          (pri,truth)
      })

      val tp = results.count({case (x,y) => x == y && ( x == "true" ) }) * 1.0
      val fp = results.count({case (x,y) => x != y && ( x == "true" ) }) * 1.0

      val tn = results.count({case (x,y) => x == y && ( x == "false" ) }) * 1.0
      val fn = results.count({case (x,y) => x != y && ( x == "false" ) }) * 1.0


      println(s"tp: ${tp} fp: ${fp} tn: ${tn} fn: ${fn} ")
      println( s" accuracy    ${(tp+tn) / (results.size) } " )
      println( s" precision   ${(tp) / (tp+fp) } " )
      println( s" recall      ${(tp) / (tp+fn) } " )
      println( s" f1          ${(2.0*tp) / (  2*tp+fp+fn ) } " )

    }*/

  def apply[HEAD <: AnyRef](
    dm: DataModel,
    cls: List[ConstrainedClassifier[_, HEAD]]
  )(
    implicit
    headTag: ClassTag[HEAD]
  ) = {

    train[HEAD](dm, cls, 1)
  }

  def apply[HEAD <: AnyRef](
    dm: DataModel,
    cls: List[ConstrainedClassifier[_, HEAD]],
    it: Int
  )(
    implicit
    headTag: ClassTag[HEAD]
  ) = {

    train[HEAD](dm, cls, it)
  }

  def train[HEAD <: AnyRef](
    dm: DataModel,
    cls: List[ConstrainedClassifier[_, HEAD]],
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
              case c: ConstrainedClassifier[_, HEAD] => {

                type C = c.LEFT
                //              println("-=-=-=-=-")
                //              println(c.tType)
                //              println(c.headType)
                //              println("-=-=-=-=-")

                val typedC = c.asInstanceOf[ConstrainedClassifier[_, HEAD]]

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

                        //
                        //               if (result.equals("true") && !typedC.classifier.getLabeler.discreteValue(x).equals("true"))
                        val ilearner = typedC.onClassifier.asInstanceOf[Learner].asInstanceOf[SparseNetworkLBP]
                        val lLexicon = typedC.onClassifier.getLabelLexicon
                        var LTU_actual: Int = 0
                        var LTU_predicted: Int = 0
                        for (i <- 0 until lLexicon.size()) {
                          if (lLexicon.lookupKey(i).valueEquals(result))
                            LTU_predicted = i
                          if (lLexicon.lookupKey(i).valueEquals(trueLabel))
                            LTU_actual = i
                        }
                        // The idea is that when the prediction is wrong the LTU of the actual class should be promoted
                        // and the LTU of the predicted class should be demoted.
                        if (!result.equals(trueLabel)) //equals("true") && trueLabel.equals("false")   )
                        {

                          val a = typedC.onClassifier.getExampleArray(x)
                          val a0 = a(0).asInstanceOf[Array[Int]] //exampleFeatures
                          val a1 = a(1).asInstanceOf[Array[Double]] // exampleValues
                          val exampleLabels = a(2).asInstanceOf[Array[Int]]
                          val labelValues = a(3).asInstanceOf[Array[Double]]
                          val label = exampleLabels(0)
                          var N = ilearner.net.size();

                          if (label >= N || ilearner.net.get(label) == null) {
                            ilearner.iConjuctiveLables = ilearner.iConjuctiveLables | ilearner.getLabelLexicon.lookupKey(label).isConjunctive();

                            var ltu: LinearThresholdUnit = ilearner.getbaseLTU
                            ltu.initialize(ilearner.getnumExamples, ilearner.getnumFeatures);
                            ilearner.net.set(label, ltu);
                            N = label + 1;

                          }
                          // test push
                          var ltu_actual: LinearThresholdUnit = ilearner.getLTU(LTU_actual) //.net.get(i).asInstanceOf[LinearThresholdUnit]
                          var ltu_predited: LinearThresholdUnit = ilearner.getLTU(LTU_predicted)
                          if (ltu_actual != null)
                            ltu_actual.promote(a0, a1, 0.1)
                          if (ltu_predited != null)
                            ltu_predited.demote(a0, a1, 0.1)

                          // var l = new Array[Int](1)
                          // for (i<-  0 until N) {
                          //  var ltu: LinearThresholdUnit=  ilearner.net.get(i).asInstanceOf[LinearThresholdUnit]
                          // if (ltu != null) {
                          //   l(0) = if ((i == label)) 1 else 0
                          //   ltu.learn(a0, a1, l, labelValues);}

                          //                      println("demote !")
                          //                      println("promote !")

                          // typedC.onClassifier.asInstanceOf

                          //                      typedC.onClassifier.asInstanceOf[LinearThresholdUnit].demote(a0,a1,0.1)

                          //                    typedC.onClassifier.learn(x)
                          //      typedC.onClassifier.asInstanceOf[LinearThresholdUnit].promote(a0,a1,0.1)

                          //                    typedC.onClassifier.asInstanceOf[LinearThresholdUnit].promote(a0,a1,0.1)

                          //                      typedC.onClassifier.learn(x)
                          //                      println("demote !")
                          //                      println("promote !")
                        } else {

                          /*if ( result.equals("false") && trueLabel.equals("true")   )
                    {
                      //                        println("demote !")
                      //                        println("promote !")

                      val a  = typedC.onClassifier.getExampleArray(x)
                      //.map({
                      val a0 = a(0).asInstanceOf[Array[Int]]
                      val a1 = a(1).asInstanceOf[Array[Double]]
                      //                        typedC.onClassifier.asInstanceOf[LinearThresholdUnit].promote(a0,a1,0.1)
                      //                      typedC.onClassifier.learn(x)
                      typedC.onClassifier.asInstanceOf[LinearThresholdUnit].demote(a0,a1,0.1)
                      //                      println("promote !")
                      //                        typedC.onClassifier.learn(x)

                      //                      typedC.onClassifier.asInstanceOf[LinearThresholdUnit].demote(a0,a1,0.1)
                    }else{
                      //                                              println("Correct !")
                    }
                    //                    typedC.onClassifier.asInstanceOf[LinearThresholdUnit].doneLearning()*/

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
