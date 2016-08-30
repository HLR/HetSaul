/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures.toyDataGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.SpamClassifiers._
import org.scalatest._

class SpamUnitTests extends FlatSpec with Matchers {

  /** testing population of collections inside `Node` */
  "spamDataModel.doc" should "have correct number of objects in docs by adding in collection " +
    "from Node" in {
      object DummySpamDataModel extends DataModel { val docs = node[Document] }
      val data = toyDataGenerator.generateToyDocuments(1)
      val oldSize = DummySpamDataModel.docs.getAllInstances.toList.size
      DummySpamDataModel.docs.populate(data)
      val newSize = DummySpamDataModel.docs.getAllInstances.toList.size
      (newSize - oldSize) should be(data.length)
    }

  /** testing multiple population of collections inside `Node` */
  "spamDataModel.doc" should "have correct number of objects in docs by adding in collection " +
    "from Node, multiple times " in {
      object DummySpamDataModel extends DataModel { val docs = node[Document] }
      val data1 = toyDataGenerator.generateToyDocuments(2)
      val data2 = toyDataGenerator.generateToyDocuments(5)
      val oldSize = DummySpamDataModel.docs.getAllInstances.toList.size
      DummySpamDataModel.docs.populate(data1)
      DummySpamDataModel.docs.populate(data2)
      val newSize = DummySpamDataModel.docs.getAllInstances.toList.size
      (newSize - oldSize) should be(data1.length + data2.length)
    }

  "classifier " should "overfit" in {
    val trainData = toyDataGenerator.generateToyDocuments(20)
    SpamDataModel.docs populate trainData
    SpamClassifier.learn(100)
    SpamClassifier(trainData.head) should be(trainData.head.getLabel)
  }
}
