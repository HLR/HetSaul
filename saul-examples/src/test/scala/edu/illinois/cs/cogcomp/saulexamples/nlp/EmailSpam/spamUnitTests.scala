package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamClassifiers.spamClassifier
import org.scalatest._

import scala.collection.JavaConversions._

class SpamUnitTests extends FlatSpec with Matchers {
  /** testing population of collections inside `Node` */
  "spamDataModel.doc" should "have correct number of objects in docs by adding in collection " +
    "from Node" in {
      val data1 = toyDataGeneratorObject.generateToyDocuments(1)
      val oldSize = spamDataModel.docs.getAllInstances.toList.size
      spamDataModel.docs.populate(data1)
      val newSize = spamDataModel.docs.getAllInstances.toList.size
      (newSize - oldSize) should be(data1.length)
    }

  /** testing multiple population of collections inside `Node` */
  "spamDataModel.doc" should "have correct number of objects in docs by adding in collection " +
    "from Node, multiple times " in {
      val data1 = toyDataGeneratorObject.generateToyDocuments(2)
      val data2 = toyDataGeneratorObject.generateToyDocuments(5)
      val oldSize = spamDataModel.docs.getAllInstances.toList.size
      spamDataModel.docs.populate(data1)
      spamDataModel.docs.populate(data2)
      val newSize = spamDataModel.docs.getAllInstances.toList.size
      (newSize - oldSize) should be(data1.length + data2.length)
    }

  "getNodeWithType function" should "return the right object, given a type" in {
    spamDataModel.docs should be(spamDataModel.getNodeWithType[Document])
  }

  "classifier " should "overfit" in {
    val trainData = toyDataGeneratorObject.generateToyDocuments(1)
    spamDataModel.docs populate trainData
    spamClassifier.learn(30)
    spamClassifier.classifier.discreteValue(trainData.head) should be(trainData.head.getLabel)
  }
}

object toyDataGeneratorObject {
  def generateToyDocuments(numDocs: Int): IndexedSeq[Document] = {
    val documentString = "Saul or Soul; it is the question"
    (0 to numDocs).map(_ => new Document(documentString.split(" ").toList, util.Random.nextInt(2).toString))
  }
}
