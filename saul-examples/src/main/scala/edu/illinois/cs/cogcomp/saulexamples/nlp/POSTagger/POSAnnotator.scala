/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.annotation.Annotator
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, TextAnnotation, TokenLabelView }
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager

import scala.collection.JavaConversions._

class POSAnnotator(val finalViewName: String) extends Annotator(finalViewName, Array(ViewNames.TOKENS)) {

  override def initialize(rm: ResourceManager): Unit = {}

  /** Adds the POS view to a TextAnnotation
    * Note: Assumes that the classifiers are populated with required models
    * @param ta TextAnnotation instance
    */
  override def addView(ta: TextAnnotation): Unit = {
    POSDataModel.tokens.clear()

    val tokens = ta.getView(ViewNames.TOKENS).getConstituents

    POSDataModel.tokens.populate(tokens, train = false)

    val posView = new TokenLabelView(finalViewName, "POSAnnotator", ta, 1.0)

    tokens.foreach({ cons: Constituent =>
      val label = POSClassifiers.POSClassifier(cons)
      val posCons = cons.cloneForNewViewWithDestinationLabel(finalViewName, label)
      posView.addConstituent(posCons)
    })

    ta.addView(finalViewName, posView)
  }
}
