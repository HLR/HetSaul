package edu.illinois.cs.cogcomp.saulexamples.nlp.ace

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

/** @author sameer
  * @since 12/24/15.
  */
class ACEModel extends DataModel {

  case class Token(string: String, attrs: Map[String, String] = Map.empty)

  case class Mention(start: Int, length: Int)

  case class Sentence(sid: Int, ments: Seq[Mention], toks: Seq[Token])

  case class Document(id: String, sents: Seq[Sentence])

  case class MentionPair(src: Mention, target: Mention)

  type MentionTypeLabel = (Mention, String)
  type RelationTypeLabel = (MentionPair, String)
  type CorefTypeLabel = (MentionPair, Boolean)

  // observed
  val docs = node[Document]
  val sentences = node[Sentence]
  val mentions = node[Mention]
  val tokens = node[Token]
  val relationMentions = node[MentionPair]
  val corefMentions = node[MentionPair]

  // to predict
  val mentionLabels = node[MentionTypeLabel]
  val relationLabels = node[RelationTypeLabel]
  val corefLabels = node[CorefTypeLabel]

  // edges
  val docSentences = edge(docs, sentences)
  val sentenceMentions = edge(sentences, mentions)
  val sentenceRelations = edge(sentences, relationMentions)
  val docCoreferences = edge(docs, corefMentions)
  val mentionToLabel = edge(mentions, mentionLabels)
  val relationToLabel = edge(relationMentions, relationLabels)
  val corefToLabel = edge(corefMentions, corefLabels)

  // sensors
  docSentences.addSensor((d: Document) => d.sents)
  sentenceMentions.addSensor((s: Sentence) => s.ments)
  sentenceRelations.addSensor((s: Sentence) => for (m1 <- s.ments; m2 <- s.ments; if m1 != m2) yield MentionPair(m1, m2))
  docCoreferences.addSensor((d: Document) => for (m1 <- d.sents.flatMap(_.ments); m2 <- d.sents.flatMap(_.ments); if m1 != m2) yield MentionPair(m1, m2))
  mentionToLabel.addSensor((m: Mention) => m -> "O")
  relationToLabel.addSensor((m: MentionPair) => m -> "O")
  corefToLabel.addSensor((m: MentionPair) => m -> false)

}

object TestACEModel extends ACEModel with App {

  val d = Document("test", Seq(
    Sentence(0, Seq(Mention(0, 2), Mention(5, 1)), Seq(
      Token("Barack"), Token("Obama"), Token("is"), Token("married"), Token("to"), Token("Michelle"), Token(".")
    )),
    Sentence(1, Seq(Mention(0, 1), Mention(2, 1), Mention(5, 1), Mention(8, 1)), Seq(
      Token("He"), Token("leads"), Token("USA"), Token(","), Token("while"), Token("she"), Token("inspires"), Token("the"), Token("country"), Token(".")
    ))
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
