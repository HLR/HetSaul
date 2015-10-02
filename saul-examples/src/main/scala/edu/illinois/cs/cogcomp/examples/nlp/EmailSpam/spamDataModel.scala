package edu.illinois.cs.cogcomp.examples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.lfs.data_model.DataModel
import edu.illinois.cs.cogcomp.lfs.data_model.DataModel._
import edu.illinois.cs.cogcomp.lfs.data_model.attribute.Attribute
import edu.illinois.cs.cogcomp.lfs.data_model.edge.Edge
import edu.illinois.cs.cogcomp.lfs.data_model.node.Node
import edu.illinois.cs.cogcomp.tutorial_related.Document

import scala.collection.JavaConversions._
import scala.collection.mutable.{ Map => MutableMap }

object spamDataModel extends DataModel {

  val docs = node[Document]
  val NODES: List[Node[_]] = ~~(docs)

  val wordFeature = discreteAttributesGeneratorOf[Document]('wordF) {
    x: Document =>
      {
        val words: List[String] = x.getWords.toList
        words
      }
  }

  val bigramFeature = discreteAttributesGeneratorOf[Document]('bigram) {

    x: Document =>
      {
        val words = x.getWords.toList
        var big: List[String] = List()
        for (i <- 0 until words.size - 1)
          big = (words.get(i) + "-" + words.get(i + 1)) :: big
        big
      }
  }

  val spamLable = discreteAttributeOf[Document]('label) {
    x: Document =>
      {
        x.getLabel
      }
  }
  val PROPERTIES: List[Attribute[_]] = List(wordFeature, bigramFeature)
  val EDGES: List[Edge[_, _]] = Nil
}
