package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Constituent, Relation}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Christos on 1/10/2016.
  */
class SRLRelationTest  extends FlatSpec with Matchers {
  "relations" should "increase in size" in {
    populateGraphwithTextAnnotation(srlDataModel, srlDataModel.sentences)

    (relations(relations().head) ~> relationsToArguments).size should be (2)

    val x1: Constituent = predicates().head.cloneForNewView(predicates().head.getViewName)
    val x2: Constituent = arguments().head.cloneForNewView(arguments().head.getViewName)
    new Relation("candidate", x1, x2, 0.0)

    (relations(relations().head) ~> relationsToArguments).size should be (3)
  }

}
