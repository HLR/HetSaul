package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._
/** Created by Parisa on 12/15/15.
  */
object MWE extends App {
  populateGraphwithTextAnnotation(SRLDataModel, SRLDataModel.sentences)

  // Generate predicate candidates by extracting all verb tokens
  val predicateCandidates = tokens().filter((x: Constituent) => posTag(x).startsWith("VB"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
  predicates.populate(predicateCandidates)

  val argumentCandidates = tokens().filter((x: Constituent) => posTag(x).startsWith("NN"))
    .map(c => c.cloneForNewView(ViewNames.SRL_VERB))
  arguments.populate(argumentCandidates)

  //  generate all candidate relations based on candidate arguments and predicates
  val relationCandidates2 = for {
    x <- predicates()
    y <- arguments()
  } yield new Relation("candidate", x, y, 0.0)

  relations.populate(relationCandidates2)
}
