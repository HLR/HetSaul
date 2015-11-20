package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saul.classifier.Learnable

object independent_train {
  def apply[T <: AnyRef](c: List[Learnable[T]]) =
    c.foreach((x: Learnable[T]) => x.learn(10))
}

object independent_test {
  def apply[T <: AnyRef](c: List[Learnable[T]]) =
    c.foreach((x: Learnable[T]) => x.test())
}