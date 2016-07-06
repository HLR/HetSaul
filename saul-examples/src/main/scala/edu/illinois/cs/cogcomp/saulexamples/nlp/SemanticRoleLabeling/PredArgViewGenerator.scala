/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, PredicateArgumentView, Relation }
import edu.illinois.cs.cogcomp.saul.datamodel.property.TypedProperty

import scala.collection.JavaConversions._

/** A converter from the SRL graph to a list of `PredicateArgumentView`s (both for gold and predicted relations)
  *
  * @author Christos Christodoulopoulos
  */
object PredArgViewGenerator {

  def toPredArgList(graph: SRLMultiGraphDataModel, labelProp: TypedProperty[Relation, String]): Iterable[PredicateArgumentView] = {
    import graph._
    sentences().map { ta =>
      val predArgView: PredicateArgumentView = new PredicateArgumentView(ViewNames.SRL_VERB, ta)
      (sentences(ta) ~> sentencesToRelations ~> relationsToPredicates).foreach { pred =>
        val predictedRels: Iterable[Relation] = (predicates(pred) ~> -relationsToPredicates).filterNot(rel =>
          labelProp(rel).equals("candidate"))
        val args: List[Constituent] = predictedRels.map(rel => rel.getTarget).toList
        val relLabels: Array[String] = predictedRels.map(rel => labelProp(rel)).toArray
        val scores: Array[Double] = Array.fill(relLabels.length) { 1 }
        predArgView.addPredicateArguments(pred.cloneForNewView(ViewNames.SRL_VERB), args, relLabels, scores)
      }
      predArgView
    }
  }
}
// This is to print to file for standard CoNLL evaluation -commented out for later
//    val goldOutFile = "srl.gold"
//    val goldWriter = new PrintWriter(new File(goldOutFile))
//    val predOutFile = "srl.predicted"
//    val predWriter = new PrintWriter(new File(predOutFile))
//     argumentTypeLearner.test(prediction= typeArgumentPrediction, groundTruth =  argumentLabelGold, exclude="candidate")
//    val predictedViews = predArgViewGenerator.toPredArgList(srlGraphs, typeArgumentPrediction)
//    val goldViews = predArgViewGenerator.toPredArgList(srlGraphs, argumentLabelGold)
//
//    predictedViews.foreach(pav => CoNLLFormatWriter.printPredicateArgumentView(pav, predWriter))
//    goldViews.foreach(pav => CoNLLFormatWriter.printPredicateArgumentView(pav, goldWriter))
//    predWriter.close()
//    goldWriter.close()