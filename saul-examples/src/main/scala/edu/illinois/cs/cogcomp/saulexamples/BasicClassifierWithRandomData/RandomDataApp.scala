package edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData
import edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData.RandomClassifiers.BinaryClassifier

object RandomDataApp extends App {

  import RandomDataModel._
  for (i <- 1 to 100) {
    randomNode.addInstance(i.toString)
  }
  val examples = randomNode.getAllInstances
  val graphCacheFile = "models/temp.model"
  RandomDataModel.deriveInstances()
  RandomDataModel.write(graphCacheFile)
  BinaryClassifier.learn(30)
  BinaryClassifier.test(examples)
}
