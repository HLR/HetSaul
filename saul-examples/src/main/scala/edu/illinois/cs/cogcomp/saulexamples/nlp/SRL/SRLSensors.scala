package edu.illinois.cs.cogcomp.saulexamples.nlp.SRL

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

  def textAnnotationToTree(ta: TextAnnotation, parseViewName: String): Tree[String] = {
    // We assume that there is only 1 sentence per TextAnnotation
    ta.getView(parseViewName).asInstanceOf[TreeView].getTree(0)
  }

  def textAnnotationToRelation(ta: TextAnnotation): List[Relation] = {
    ta.getView(ViewNames.SRL_VERB).getRelations.toList
  }

  /** Property sensor
    */
  def lemmatizer(c: Constituent): String = {
    c.getLabel
  }
}
