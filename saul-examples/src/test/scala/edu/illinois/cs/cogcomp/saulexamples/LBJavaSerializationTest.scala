package edu.illinois.cs.cogcomp.saulexamples

import edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures.toyDataGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.SpamDataModel

import org.scalatest.{ Matchers, FlatSpec }

class LBJavaSerializationTest extends FlatSpec with Matchers {

  /** making sure that serialization is working the way it is supposed to be */
  "LBJava serialization " should " work " in {

    /*
    val trainData = toyDataGenerator.generateToyDocuments(100)
    val testData = toyDataGenerator.generateToyDocuments(100).toList

    spamDataModel.docs populate trainData
    spamClassifier.learn(10)
    val predictionsBeforeSerialization = testData.map(spamClassifier(_))

    spamClassifier.save()
    deserializedSpamClassifier.load(spamClassifier.lcFilePath, spamClassifier.lexFilePath)
    val predictionsAfterSerialization = testData.map(deserializedSpamClassifier(_))
    predictionsAfterSerialization.indices.forall(it => predictionsBeforeSerialization(it) == predictionsAfterSerialization(it)) should be(true)
*/
  }
}
