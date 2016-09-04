/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation, TextAnnotation, TreeView }
import edu.illinois.cs.cogcomp.edison.features.Feature
import edu.illinois.cs.cogcomp.edison.features.helpers.PathFeatureHelper
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors

import scala.collection.JavaConverters._

/** Created by taher on 7/28/16.
  */
object SpRLSensors {
  val dependencyView = ViewNames.DEPENDENCY_STANFORD

  // helper methods
  def getDependencyPath(ta: TextAnnotation, t1: Int, t2: Int): String = {

    def getRelationName(relations: List[Relation], c1: Constituent, c2: Constituent, dir: String): String = {
      val r = relations.find(x => (x.getSource == c1 && x.getTarget == c2) || (x.getSource == c2 && x.getTarget == c1))
      r match {
        case Some(r) => dir + r.getRelationName
        case None => ""
      }
    }

    val c1 = ta.getView(dependencyView).getConstituentsCoveringToken(t1).get(0)
    val c2 = ta.getView(dependencyView).getConstituentsCoveringToken(t2).get(0)

    val parse: TreeView = ta.getView(dependencyView).asInstanceOf[TreeView]

    val relations = parse.getRelations.asScala.toList
    val paths = PathFeatureHelper.getPathsToCommonAncestor(c1, c2, 400)

    val up = paths.getFirst.asScala.toList
    val down = paths.getSecond.asScala.toList

    val path: StringBuilder = new StringBuilder
    var i = 0;
    while (i < up.size - 1) {
      path.append(getRelationName(relations, up(i), up(i + 1), "↑"))
      i += 1
    }
    i = down.size - 1
    while (i > 0) {
      path.append(getRelationName(relations, down(i), down(i - 1), "↓"))
      i -= 1
    }

    path.toString.toUpperCase
  }

  def getDependencyRelationsWith(c: Constituent, relationName: String): List[Relation] = {
    getDependencyRelations(c.getTextAnnotation)
      .filter(y => y.getRelationName.equalsIgnoreCase(relationName.toLowerCase) &&
        (y.getSource.getSpan == c.getSpan || y.getTarget.getSpan == c.getSpan))
  }

  def getDependencyRelations(ta: TextAnnotation): List[Relation] = {
    val parse: TreeView = ta.getView(dependencyView).asInstanceOf[TreeView]

    parse.getRelations.asScala.toList
  }

  def getConstituentId(x: Constituent): String =
    x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSentenceId + ":" + x.getSpan

  def getUniqueSentenceId(x: Constituent): String =
    x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSentenceId

  def getPairFeatures(source: Constituent, target: Constituent,
    func: (Constituent) => java.util.Set[Feature]): java.util.Set[Feature] = {

    val r = new Relation("r", source, target, 0.1)
    val result = func(target)
    source.getOutgoingRelations.remove(r)
    result
  }

}
