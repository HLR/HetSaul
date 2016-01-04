package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.annotation.BasicTextAnnotationBuilder
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamClassifiers.spamClassifier
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation
import org.scalatest._

import scala.collection.JavaConversions._

class SpamUnitTests extends FlatSpec with Matchers {

  import spamDataModel._

  /** testing population of collections inside `Node` */
  "spamDataModel.doc" should "have correct number of objects in docs by adding in collection " +
    "from Node" in {
      val data1 = toyDataGenerator.generateToyDocuments(1)
      val oldSize = docs.getAllInstances.toList.size
      docs.populate(data1)
      val newSize = docs.getAllInstances.toList.size
      (newSize - oldSize) should be(data1.length)
    }

  /** testing multiple population of collections inside `Node` */
  "spamDataModel.doc" should "have correct number of objects in docs by adding in collection " +
    "from Node, multiple times " in {
      val data1 = toyDataGenerator.generateToyDocuments(2)
      val data2 = toyDataGenerator.generateToyDocuments(5)
      val oldSize = docs.getAllInstances.toList.size
      docs.populate(data1)
      docs.populate(data2)
      val newSize = docs.getAllInstances.toList.size
      (newSize - oldSize) should be(data1.length + data2.length)
    }

  "getNodeWithType function" should "return the right object, given a type" in {
    docs should be(spamDataModel.getNodeWithType[Document])
  }

  "classifier " should "overfit" in {
    val trainData = toyDataGenerator.generateToyDocuments(20)
    docs populate trainData
    spamClassifier.learn(100)
    spamClassifier.classifier.discreteValue(trainData.head) should be(trainData.head.getLabel)
  }
}

object toyDataGenerator {
  val documentStrings = List("Saul or Soul; that is the question", "when will I graduate?")
  def generateToyDocuments(numDocs: Int): IndexedSeq[Document] = {
    (1 to numDocs).map {
      val randInt = util.Random.nextInt(2)
      _ => new Document(documentStrings(randInt).split(" ").toList, randInt.toString)
    }
  }

  /** Generate toy instances that have the same labels */
  def generateToyDocumentsSingleLabel(numDocs: Int): IndexedSeq[Document] = {
    val label = util.Random.nextInt(2)
    (1 to numDocs).map(_ => new Document(documentStrings(label).split(" ").toList, label.toString))
  }

  def generateToyTextAnnotation(numDocs: Int): IndexedSeq[TextAnnotation] = {
    import scala.collection.JavaConversions._

    (1 to numDocs).map { _ =>
      val numSentences = 5
      val documentsTokenized = (1 to numSentences).map(_ => documentStrings(0).split(" "))
      BasicTextAnnotationBuilder.createTextAnnotationFromTokens(documentsTokenized)
    }
  }

  def main(args: Array[String]): Unit = {
    generateToyTextAnnotation(3)
  }
}
