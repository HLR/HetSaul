/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.ace

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

/** @author sameer
  * @since 12/24/15.
  */
object Types {

  case class Token(string: String, start: Int = 0, end: Int = 0, attrs: Map[String, String] = Map.empty)

  case class Mention(startSpan: Int, endSpan: Int)

  case class Sentence(sid: Int, ments: Seq[Mention], toks: Seq[Token], span: (Int, Int) = -1 -> -1)

  case class Document(id: String, sents: Seq[Sentence])

  case class MentionPair(src: Mention, target: Mention)

  case class MentionLabel(m: Mention, var label: String = "O", var gold: String = "O")
  case class RelationLabel(m: MentionPair, var label: String = "O", var gold: String = "O")
  case class CorefLabel(m: MentionPair, var label: Boolean = false, var gold: Boolean = false)
}

class ACEModel extends DataModel {
  import Types._
  // observed
  val docs = node[Document]
  val sentences = node[Sentence]
  val mentions = node[Mention]
  val tokens = node[Token]
  val relationMentions = node[MentionPair]
  val corefMentions = node[MentionPair]

  // to predict
  val mentionLabels = node[MentionLabel]
  val relationLabels = node[RelationLabel]
  val corefLabels = node[CorefLabel]

  // edges
  val docSentences = edge(docs, sentences)
  val sentenceTokens = edge(sentences, tokens)
  val sentenceMentions = edge(sentences, mentions)
  val sentenceRelations = edge(sentences, relationMentions)
  // val mentionTokens = edge(mentions, tokens)
  val docCoreferences = edge(docs, corefMentions)
  val mentionToLabel = edge(mentions, mentionLabels)
  val relationToLabel = edge(relationMentions, relationLabels)
  val corefToLabel = edge(corefMentions, corefLabels)

  // sensors
  docSentences.addSensor((d: Document) => d.sents)
  sentenceTokens.addSensor((s: Sentence) => s.toks)
  sentenceMentions.addSensor((s: Sentence) => s.ments)
  sentenceRelations.addSensor((s: Sentence) => for (m1 <- s.ments; m2 <- s.ments; if m1 != m2) yield MentionPair(m1, m2))
  // mentionTokens.addSensor((m: Mention) => (mentions(m) ~> -sentenceMentions ~> sentenceTokens).filter(t => m.startSpan <= t.start && m.endSpan >= t.end))
  docCoreferences.addSensor((d: Document) => for (m1 <- d.sents.flatMap(_.ments); m2 <- d.sents.flatMap(_.ments); if m1 != m2) yield MentionPair(m1, m2))
  mentionToLabel.addSensor((m: Mention) => MentionLabel(m))
  relationToLabel.addSensor((m: MentionPair) => RelationLabel(m))
  corefToLabel.addSensor((m: MentionPair) => CorefLabel(m))

}

object TestACEModel extends ACEModel with App {
  import Types._

  val d = Document("test", Seq(
    Sentence(0, Seq(Mention(0, 11), Mention(26, 33)), Seq(
      Token("Barack"), Token("Obama"), Token("is"), Token("married"), Token("to"), Token("Michelle"), Token(".")
    ), 0 -> 35),
    Sentence(1, Seq(Mention(36, 38), Mention(46, 49), Mention(56, 59), Mention(72, 78)), Seq(
      Token("He"), Token("leads"), Token("USA"), Token(","), Token("while"), Token("she"), Token("inspires"), Token("the"), Token("country"), Token(".")
    ), 36 -> 80)
  ))

  docs.populate(Seq(d))

  implicit def itr2string[T](iterable: Iterable[T]): String = "(%d) %s".format(iterable.size, iterable.take(10).mkString(", "))

  println("---")
  println("docs : %s".format(itr2string(docs())))
  println("sents: %s".format(itr2string(sentences())))
  println("toks : %s".format(itr2string(sentences())))
  println("ments: %s".format(itr2string(sentences())))
  println("mentsLabels: %s".format(itr2string(mentionLabels())))
  println("relationM  : %s".format(itr2string(relationMentions())))
  println("relationL  : %s".format(itr2string(relationLabels())))
  println("corefM     : %s".format(itr2string(relationMentions())))
  println("corefL     : %s".format(itr2string(relationLabels())))
  println("---")
  println("s(0).ments  : %s".format(itr2string(sentences(d.sents(0)) ~> sentenceMentions)))
  println("s(0).rments : %s".format(itr2string(sentences(d.sents(0)) ~> sentenceRelations ~> relationToLabel)))
  println("s(1).ments  : %s".format(itr2string(sentences(d.sents(1)) ~> sentenceMentions)))
  println("s(1).rments : %s".format(itr2string(sentences(d.sents(0)) ~> sentenceRelations ~> relationToLabel)))
  println("doc.corefMs : %s".format(itr2string(docs(d) ~> docCoreferences ~> corefToLabel)))

}
