package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.saulexamples.data.XuPalmerCandidateGenerator

import scala.collection.JavaConversions._

object srlSensors {
  def sentenceToGoldPredicates(ta: TextAnnotation): List[Constituent] = {
    ta.getView(ViewNames.SRL_VERB).asInstanceOf[PredicateArgumentView].getPredicates.toList
  }
  def relToPredicate(rel: Relation): Constituent = {
    rel.getSource
  }

  def relToArgument(rel: Relation): Constituent = {
    rel.getTarget.cloneForNewViewWithDestinationLabel(ViewNames.SRL_VERB, "Argument")
  }

  def textAnnotationToTree(ta: TextAnnotation): Tree[Constituent] = {
    // We assume that there is only 1 sentence per TextAnnotation
    val parseViewName: String = ViewNames.PARSE_GOLD
    ta.getView(parseViewName).asInstanceOf[TreeView].getConstituentTree(0)
  }

  def textAnnotationToStringTree(ta: TextAnnotation): Tree[String] = {
    // We assume that there is only 1 sentence per TextAnnotation
    val parseViewName: String = ViewNames.PARSE_GOLD
    ta.getView(parseViewName).asInstanceOf[TreeView].getTree(0)
  }

  def textAnnotationToRelation(ta: TextAnnotation): List[Relation] = {
    ta.getView(ViewNames.SRL_VERB).getRelations.toList
  }
  def textAnnotationToRelationMatch(ta: TextAnnotation, r: Relation): Boolean = {
    return (ta.getCorpusId + ":" + ta.getId).matches(r.getSource.getTextAnnotation.getCorpusId + ":" + r.getSource.getTextAnnotation.getId)

    // ta.getView(ViewNames.SRL_VERB).getRelations.toList
  }
  /** Returns all the subtrees that are suitable arguments:
    * It excludes punctuations and traces (which have a 0-length span)
    * @param currentSubTrees A list of already defined subtrees
    * @return The `currentSubTrees` list augmented all their subtrees
    */
  def getSubtreeArguments(currentSubTrees: List[Tree[Constituent]]): List[Tree[Constituent]] = {
    val filtered = currentSubTrees.filterNot { sTree =>
      val constituent: Constituent = sTree.getLabel
      constituent.getSurfaceForm.matches("\\p{P}") || (constituent.getSpan.getFirst == constituent.getSpan.getSecond)
    }
    filtered ++ filtered.flatMap { tree =>
      if (!tree.isRoot && tree.getChildren.size() == 1) List()
      else getSubtreeArguments(tree.getChildren.toList)
    }
  }

  def xuPalmerCandidate(x: Constituent, y: Tree[String]): List[Relation] = {
    val t = new XuPalmerCandidateGenerator(null)
    val p = t.generateSaulCandidates(x, y)
    val z = p.map(y => new Relation("candidate", x.cloneForNewView(x.getViewName), y.cloneForNewView(y.getViewName), 0.0))
    z.toList
  }
}
