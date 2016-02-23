package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, PredicateArgumentView, Relation }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty

import scala.collection.JavaConversions._

/** A converter from the SRL graph to a list of `PredicateArgumentView`s (both for gold and predicted relations)
  *
  * @author Christos Christodoulopoulos
  */
object predArgViewGenerator {

  def toPredArgList(graph: srlMultiGraph, labelProp: TypedProperty[Relation, String]): Iterable[PredicateArgumentView] = {
    import graph._
    sentences().map { ta =>
      val predArgView: PredicateArgumentView = new PredicateArgumentView(ViewNames.SRL_VERB, ta)
      (sentences(ta) ~> sentencesToRelations ~> relationsToPredicates).foreach { pred =>
        val predictedRels: Iterable[Relation] = (predicates(pred) ~> -relationsToPredicates).filterNot(rel =>
          (relations(rel) prop labelProp).head.equals("candidate"))
        val args: List[Constituent] = predictedRels.map(rel => rel.getTarget).toList
        val relLabels: Array[String] = predictedRels.map(rel => rel.getRelationName).toArray
        val scores: Array[Double] = Array.fill(relLabels.length) { 1 }
        predArgView.addPredicateArguments(pred.cloneForNewView(ViewNames.SRL_VERB), args, relLabels, scores)
      }
      predArgView
    }
  }
}