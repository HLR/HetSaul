package edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData
import edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData.randomClassifiers.bClassifier

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
}
