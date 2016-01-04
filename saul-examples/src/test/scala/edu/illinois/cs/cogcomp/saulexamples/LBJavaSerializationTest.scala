package edu.illinois.cs.cogcomp.saulexamples

import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamClassifiers.spamClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.toyDataGenerator

import org.scalatest.{Matchers, FlatSpec}

class LBJavaSerializationTest extends FlatSpec with Matchers {

  import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamDataModel
  import spamDataModel._

  val trainData = toyDataGenerator.generateToyDocuments(50)
  val testData = toyDataGenerator.generateToyDocuments(50)

  spamDataModel.docs populate trainData
  spamClassifier.learn(10)
  val predictionsBeforeSerialization = testData.map( spamClassifier.classifier.discreteValue(_))

  spamClassifier.classifier.save()

  object deserializedSpamClassifier extends Learnable[Document](spamDataModel) {
    def label = spamLabel
    override def algorithm = "SparseNetwork"
    override def feature = using(wordFeature)
  }

  val predictionsAfterSerialization = testData.map( deserializedSpamClassifier.classifier.discreteValue(_))

  /** making sure that serialization is working the way it is supposed to be */
  "LBJava serialization " should " work " in {
    predictionsAfterSerialization.indices.foreach( it => predictionsAfterSerialization(it) should be(predictionsBeforeSerialization(it)) )
  }
}
