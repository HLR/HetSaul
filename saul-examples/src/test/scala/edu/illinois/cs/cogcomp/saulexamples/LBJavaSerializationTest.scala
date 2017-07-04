/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples

import edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures.toyDataGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.SpamClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.SpamDataModel

import org.scalatest.{ Matchers, FlatSpec }

class LBJavaSerializationTest extends FlatSpec with Matchers {

  /** making sure that serialization is working the way it is supposed to be */
  "LBJava serialization " should " work " in {

    val trainData = toyDataGenerator.generateToyDocuments(100)
    val testData = toyDataGenerator.generateToyDocuments(100).toList

    SpamDataModel.email.populate(trainData)
    SpamDataModel.email.populate(testData, train = false)
    SpamClassifier.learn(10)

    val predictionsBeforeSerialization = testData.map(SpamClassifier(_))

    SpamClassifier.save()
    DeserializedSpamClassifier.load(SpamClassifier.lcFilePath, SpamClassifier.lexFilePath)
    val predictionsAfterSerialization = testData.map(DeserializedSpamClassifier(_))
    predictionsAfterSerialization.indices.forall(it => predictionsBeforeSerialization(it) == predictionsAfterSerialization(it)) should be(true)
  }
}
