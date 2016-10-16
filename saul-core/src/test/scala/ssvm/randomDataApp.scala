package ssvm
import breeze.linalg._
import breeze.plot.Figure
import breeze.plot._
import edu.illinois.cs.cogcomp.lbjava.learn.LinearThresholdUnit
import ssvm.randomClassifiers.bClassifier

/** Created by Parisa on 10/13/16.
  */
object randomDataApp extends App {

  import randomDataModel._
  for (i <- 1 to 100) {
    randomNode.addInstance(i.toString)
  }
  val ex = randomNode.getAllInstances
  val graphCacheFile = "models/temp.model"
  randomDataModel.deriveInstances()
  randomDataModel.write(graphCacheFile)
  bClassifier.learn(30)
  bClassifier.test(ex)
  val w = bClassifier.classifier.getLTU(1).asInstanceOf[LinearThresholdUnit].getWeightVector

  val p2 = (randomNode() prop randomProperty) zip (randomNode() prop randomLabel)
  val x2 = DenseVector(p2.filter(x => x._2.equals("1")).map(x => x._1(0)).toArray)
  val y2 = DenseVector(p2.filter(x => x._2.equals("1")).map(x => x._1(1)).toArray)
  val x_minus = DenseVector(p2.filter(x => x._2.equals("-1")).map(x => x._1(0)).toArray)
  val y_minus = DenseVector(p2.filter(x => x._2.equals("-1")).map(x => x._1(1)).toArray)

  val f = Figure()
  val p = f.subplot(0)
  p.ylim(-3.0, 3)
  p.xlim(-3, 3)
  val x = linspace(0.0, 1.0)
  p += plot(x2, y2, '.')
  p += plot(x_minus, y_minus, '.')
  p += plot(DenseVector(0, w.getWeight(0)), DenseVector(0, w.getWeight(1)))
  p += plot(DenseVector(10 * w.getWeight(1), (-10) * w.getWeight(1)), DenseVector(-10 * w.getWeight(0), 10 * w.getWeight(0)))
  p.xlabel = "x axis"
  p.ylabel = "y axis"
  f.saveas("lines.png")
}
