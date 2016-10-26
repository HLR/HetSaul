package edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData
import edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData.RandomClassifiers.bClassifier

/** Created by Parisa on 10/13/16.
  */
object RandomDataApp extends App {

  import RandomDataModel._
  for (i <- 1 to 100) {
    randomNode.addInstance(i.toString)
  }
  val ex = randomNode.getAllInstances
  val graphCacheFile = "models/temp.model"
  RandomDataModel.deriveInstances()
  RandomDataModel.write(graphCacheFile)
  bClassifier.learn(30)
  bClassifier.test(ex)
}
