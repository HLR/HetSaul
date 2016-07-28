/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.datamodel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import org.scalatest.{ Matchers, FlatSpec }

class DocumentStructureTest extends FlatSpec with Matchers {

  val maxSentLength = 10
  val maxNumSents = 10

  trait DocumentModel extends DataModel {
    val docs = node[String]
  }

  case class Sentence(idx: Int, str: String)

  trait SentenceModel extends DocumentModel {
    val sents = node[Sentence]

    val docToSen = edge(docs, sents)
    docToSen.addSensor((d: String) => d.split("\n").toSeq.zipWithIndex.map { case (s, i) => Sentence(i, s) })

    val sent = (0 until maxNumSents).map(idx => {
      val e = edge(docs, sents)
      e.addSensor((d, s) => s.idx == idx)
      e
    })
  }

  case class Token(pos: Int, str: String, sidx: Int)

  trait TokenModel extends SentenceModel {
    val toks = node[Token]

    val senToTok = edge(sents, toks)
    senToTok.addSensor((s: Sentence) => s.str.split("[\\s.]+").toSeq.zipWithIndex.map { case (t, p) => Token(p, t, s.idx) })

    val next = edge(toks, toks)
    next.addSensor((a, b) => a.sidx == b.sidx && a.pos == b.pos - 1)
    val prev = -next

    val pos = (0 until maxSentLength).map(l => {
      val e = edge(sents, toks)
      e.addSensor((s, t) => s.idx == t.sidx && t.pos == l)
      e
    })

    val sameString = edge(toks, toks)
    sameString.addSensor((a, b) => a.str == b.str)
  }

  object TestObject extends TokenModel {
    docs.populate(Seq(
      """Barack Obama was born in Hawaii.
        |But Obama is not a normal Hawaii name.
        |However it is not common in Kenya as well.""".stripMargin
    ))
  }

  "document.populate" should "populate sentences" in {
    import TestObject._
    docs().size should be(1)
    sents().size should be(3)
    toks().size should be(23)
  }

  "document.populate" should "populate tokens" in {
    import TestObject._
    toks().size should be(23)
  }

  "document.populate" should "link sentences to docs" in {
    import TestObject._
    (docs() ~> docToSen).size should be(3)
    (docs() ~> sent(0)).size should be(1)
    (docs() ~> sent(1)).size should be(1)
    (docs() ~> sent(2)).size should be(1)
    (docs() ~> sent(3)).size should be(0)
  }

  "document.populate" should "link tokens to sentences" in {
    import TestObject._
    (sents() ~> senToTok).size should be(23)
    (docs() ~> sent(0) ~> senToTok).size should be(6)
    (docs() ~> sent(1) ~> senToTok).size should be(8)
    (docs() ~> sent(2) ~> senToTok).size should be(9)

    (docs() ~> sent(0) ~> pos(0)) should be(Set(Token(0, "Barack", 0)))
    (docs() ~> sent(0) ~> pos(0) ~> next) should be(Set(Token(1, "Obama", 0)))
    (docs() ~> sent(0) ~> pos(1) ~> prev) should be(Set(Token(0, "Barack", 0)))
    (docs() ~> sent(0) ~> pos(0) ~> next ~> next ~> next).toSet should be(Set(Token(3, "born", 0)))

    (docs() ~> sent(0) ~> pos(0) ~> sameString) should be(Set(Token(0, "Barack", 0)))
    (docs() ~> sent(0) ~> pos(1) ~> sameString) should be(Set(Token(1, "Obama", 0), Token(1, "Obama", 1)))
    (docs() ~> sent(0) ~> pos(5) ~> sameString) should be(Set(Token(5, "Hawaii", 0), Token(6, "Hawaii", 1)))
  }
}
