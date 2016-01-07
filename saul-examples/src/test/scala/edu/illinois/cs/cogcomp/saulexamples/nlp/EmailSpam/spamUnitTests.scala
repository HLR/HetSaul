package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures.toyDataGenerator
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamClassifiers.spamClassifier
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

  "getNodeWithType function" should "return the right object, given a type" in {
    object DummySpamDataModel extends DataModel { val docs = node[Document] }
    DummySpamDataModel.docs should be(DummySpamDataModel.getNodeWithType[Document])
  }

  "classifier " should "overfit" in {
    val trainData = toyDataGenerator.generateToyDocuments(20)
    spamDataModel.docs populate trainData
    spamClassifier.learn(100)
    spamClassifier(trainData.head) should be(trainData.head.getLabel)
  }
}

