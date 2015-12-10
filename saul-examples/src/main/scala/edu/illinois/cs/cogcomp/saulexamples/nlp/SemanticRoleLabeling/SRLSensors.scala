package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation, TextAnnotation, TreeView }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree

import scala.collection.JavaConversions._

object SRLSensors {

  def relToPredicate(rel: Relation): Constituent = {
    rel.getSource
  }

  def relToArgument(rel: Relation): Constituent = {
    rel.getTarget
  }

  def textAnnotationToTree(ta: TextAnnotation): Tree[Constituent] = {
    // We assume that there is only 1 sentence per TextAnnotation
    val parseViewName: String = ViewNames.PARSE_GOLD
    ta.getView(parseViewName).asInstanceOf[TreeView].getConstituentTree(0)
  }

  def textAnnotationToRelation(ta: TextAnnotation): List[Relation] = {
    ta.getView(ViewNames.SRL_VERB).getRelations.toList
  }

  def textAnnotationToTokens(ta: TextAnnotation): List[Constituent] = {
    ta.getView(ViewNames.TOKENS).getConstituents.toList
  }

  /** Property sensor */
  def lemmatizer(c: Constituent): String = {
    c.getLabel
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
}
