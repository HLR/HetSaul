package edu.illinois.cs.cogcomp.saulexamples.nlp

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ Document, Sentence }
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import org.scalatest.{ FlatSpec, Matchers }

/** Created by Taher on 2017-01-11.
  */
class LanguageBaseTypeSensorTests extends FlatSpec with Matchers {

  "Extracted features for 'I received the book.'" should "be correct" in {
    val text = "I received the book."
    val document = new Document("doc1", 0, text.length, text)
    val sentence = new Sentence(document, "s1", 0, text.length, text)
    val tokens = sentenceToTokenGenerating(sentence)

    tokens.length should be(5)

    tokens(0).getDocument.getId should be("doc1")
    tokens(1).getDocument.getId should be("doc1")
    tokens(2).getDocument.getId should be("doc1")
    tokens(3).getDocument.getId should be("doc1")
    tokens(4).getDocument.getId should be("doc1")

    tokens(0).getSentence.getId should be("s1")
    tokens(1).getSentence.getId should be("s1")
    tokens(2).getSentence.getId should be("s1")
    tokens(3).getSentence.getId should be("s1")
    tokens(4).getSentence.getId should be("s1")

    tokens(0).getText should be("I")
    tokens(1).getText should be("received")
    tokens(2).getText should be("the")
    tokens(3).getText should be("book")
    tokens(4).getText should be(".")

    getPos(tokens(0)).mkString should be("PRP")
    getPos(tokens(1)).mkString should be("VBD")
    getPos(tokens(2)).mkString should be("DT")
    getPos(tokens(3)).mkString should be("NN")
    getPos(tokens(4)).mkString should be(".")

    getLemma(tokens(0)).mkString should be("i")
    getLemma(tokens(1)).mkString should be("receive")
    getLemma(tokens(2)).mkString should be("the")
    getLemma(tokens(3)).mkString should be("book")
    getLemma(tokens(4)).mkString should be(".")

    getDependencyRelation(tokens(0)).mkString should be("nsubj")
    getDependencyRelation(tokens(1)).mkString should be("root")
    getDependencyRelation(tokens(2)).mkString should be("det")
    getDependencyRelation(tokens(3)).mkString should be("dobj")
    getDependencyRelation(tokens(4)).mkString should be("")

    getSemanticRole(tokens(0)).mkString should be("")
    getSemanticRole(tokens(1)).mkString should be("")
    getSemanticRole(tokens(2)).mkString should be("")
    getSemanticRole(tokens(3)).mkString should be("")
    getSemanticRole(tokens(4)).mkString should be("")

    getSubCategorization(tokens(0)).mkString should be("S>(NP)VP.")
    getSubCategorization(tokens(1)).mkString should be("VP>(VBD)NP")
    getSubCategorization(tokens(2)).mkString should be("NP>(DT)NN")
    getSubCategorization(tokens(3)).mkString should be("NP>DT(NN)")
    getSubCategorization(tokens(4)).mkString should be("S>NPVP(.)")

  }

  "Extracted features for 'I am going to eat lunch.'" should "be correct" in {
    val text = "I am going to eat lunch."
    val document = new Document("doc1", 0, text.length, text)
    val sentence = new Sentence(document, "s2", 0, text.length, text)
    val tokens = sentenceToTokenGenerating(sentence)

    tokens.length should be(7)

    tokens(0).getText should be("I")
    tokens(1).getText should be("am")
    tokens(2).getText should be("going")
    tokens(3).getText should be("to")
    tokens(4).getText should be("eat")
    tokens(5).getText should be("lunch")
    tokens(6).getText should be(".")

    getPos(tokens(0)).mkString should be("PRP")
    getPos(tokens(1)).mkString should be("VBP")
    getPos(tokens(2)).mkString should be("VBG")
    getPos(tokens(3)).mkString should be("TO")
    getPos(tokens(4)).mkString should be("VB")
    getPos(tokens(5)).mkString should be("NN")
    getPos(tokens(6)).mkString should be(".")

    getLemma(tokens(0)).mkString should be("i")
    getLemma(tokens(1)).mkString should be("be")
    getLemma(tokens(2)).mkString should be("go")
    getLemma(tokens(3)).mkString should be("to")
    getLemma(tokens(4)).mkString should be("eat")
    getLemma(tokens(5)).mkString should be("lunch")
    getLemma(tokens(6)).mkString should be(".")

    getDependencyRelation(tokens(0)).mkString should be("nsubj")
    getDependencyRelation(tokens(1)).mkString should be("aux")
    getDependencyRelation(tokens(2)).mkString should be("root")
    getDependencyRelation(tokens(3)).mkString should be("aux")
    getDependencyRelation(tokens(4)).mkString should be("xcomp")
    getDependencyRelation(tokens(5)).mkString should be("dobj")
    getDependencyRelation(tokens(6)).mkString should be("")

    getSubCategorization(tokens(0)).mkString should be("S>(NP)VP.")
    getSubCategorization(tokens(1)).mkString should be("VP>(VBP)VP")
    getSubCategorization(tokens(2)).mkString should be("VP>(VBG)S")
    getSubCategorization(tokens(3)).mkString should be("VP>(TO)VP")
    getSubCategorization(tokens(4)).mkString should be("VP>(VB)NP")
    getSubCategorization(tokens(5)).mkString should be("VP>VB(NP)")
    getSubCategorization(tokens(6)).mkString should be("S>NPVP(.)")

  }
}
