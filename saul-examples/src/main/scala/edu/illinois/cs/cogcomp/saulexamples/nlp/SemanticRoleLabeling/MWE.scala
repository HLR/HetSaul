package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Constituent, Relation}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._

/** Created by Parisa on 12/15/15.
  */
object MWE extends App {

  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)

  println("\n" + (relations(relations().head) ~> relationsToArguments).size)

  val x1: Constituent= predicates().head.cloneForNewView(predicates().head.getViewName)
  val x2: Constituent= arguments().head.cloneForNewView(arguments().head.getViewName)
  val x = new Relation("candidate", x1,x2, 0.0)

  println((relations(relations().head) ~> relationsToArguments).size)

  print("bye!")
}
