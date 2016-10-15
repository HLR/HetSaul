package ssvm

import edu.illinois.cs.cogcomp.lbjava.learn.SupportVectorMachine
import edu.illinois.cs.cogcomp.saul.classifier.Learnable

/** Created by Parisa on 10/14/16.
  */
object randomClassifiers {
  import randomDataModel._
  object bClassifier extends Learnable[String](randomNode) {
    def label = randomLabel
    override def feature = using(randomProperty)
    override lazy val classifier = new SupportVectorMachine()
    override val useCache = true
  }
}
