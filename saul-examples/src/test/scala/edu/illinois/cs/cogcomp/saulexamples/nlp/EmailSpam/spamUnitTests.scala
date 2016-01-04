package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.annotation.BasicTextAnnotationBuilder
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures.toyDataGenerator
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
    spamClassifier(trainData.head) should be(trainData.head.getLabel)
  }
}

