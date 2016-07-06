/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

object graphQueries extends App {

  val model = new DataModel {
    val token = node[String]
    val sentence = node[String]
    val document = node[String]
    val corpus = node[String]

    val t2s = edge(token, sentence)
    val s2d = edge(sentence, document)
    val d2c = edge(document, corpus)
  }

  import model._

  println((token() ~> t2s).node == sentence)
  println((token() ~> t2s ~> -t2s).node == token)
  println("---")
  println((token() ~> t2s ~> s2d).node == document)
  println((token() ~> t2s ~> s2d ~> -s2d).node == sentence)
  println((token() ~> t2s ~> s2d ~> -s2d ~> -t2s).node == token)
  println("---")
  println((token() ~> t2s ~> s2d ~> d2c).node == corpus)
  println((token() ~> t2s ~> s2d ~> d2c ~> -d2c ~> -s2d ~> -t2s).node == token)
}
