/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator
import org.scalatest.{ FlatSpec, Matchers }

class DataModelTest extends FlatSpec with Matchers {
  val rm = new SRLConfigurator().getDefaultConfig
  val parseViewName = rm.getString(SRLConfigurator.SRL_PARSE_VIEW)
  val SRLDataModel = new SRLMultiGraphDataModel(parseViewName)
  import SRLDataModel._
  val viewsToAdd = Array(
    ViewNames.LEMMA, ViewNames.POS, ViewNames.SHALLOW_PARSE,
    ViewNames.PARSE_GOLD, ViewNames.SRL_VERB
  )
  val ta = {
    val taTmp = DummyTextAnnotationGenerator.generateAnnotatedTextAnnotation(viewsToAdd, false, 1)
    // included here, in order to make sure population is done before making any queries
    sentences.populate(List(taTmp))
    taTmp
  }

  "graph population" should "be correct" in {
    sentences().size should be(1)
    sentences().head.getText.trim should be("The construction of the John Smith library finished on time .")
    predicates().size should be(1)
    relations().size should be(2)
  }

  "predicate and POS of arg" should "be correct" in {
    (relations() prop predLemmaR).toSet should be(Set("finish", "finish"))
    (relations() prop predPosTag).toSet should be(Set("VBD", "VBD"))
  }

  "voice of pred" should "be correct" in {
    (predicates() prop voice).head should be("A")
  }

  "phrase type of arg" should "be correct" in {
    // TODO According to the Punyakanok et el. paper this should be
    // "the tag of the corresponding constituent in the parse tree"
    //(relations() prop phraseTypeRelation).toSet should be (Set("PP", "NP"))
    // Instead this is what the current feature returns
    (relations() prop phraseTypeRelation).toSet should be(Set(
      "PP,pt:h:finished,pt:h-pos:VBD,pt:VP",
      "NP,pt:h:finished,pt:h-pos:VBD,pt:S"
    ))
  }

  "head word and POS of arg" should "be correct" in {
    (relations() prop headwordRelation).toSet should be(Set("hw:on,h-pos:IN", "hw:construction,h-pos:NN"))
  }

  "position of arg" should "be correct" in {
    (relations() prop linearPosition).toSet should be(Set("A", "B"))
  }

  "path of arg" should "be correct" in {
    (relations() prop pathRelation).toSet should be(Set("VBD^VPvPP,VBD^", "VBD^VP^SvNP,VBD^VP^"))
  }

  "subcat of pred" should "be correct" in {
    (predicates() prop subcategorization).toSet should be(Set("VP>(VBD)PP"))
  }

  "context words of arg" should "be correct" in {
    (relations() prop argWordWindow).toSet should be(Set(
      "context-2:#wd:library,context-1:#wd:finished,context1:#wd:.",
      "context1:#wd:finished,context2:#wd:on"
    ))
  }

  "context POS of arg" should "be correct" in {
    (relations() prop argPOSWindow).toSet should be(Set(
      "context-2:#pos:NN,context-1:#pos:VBD,context1:#pos:.",
      "context1:#pos:VBD,context2:#pos:IN"
    ))
  }

  //  "all the verb classes of pred" should "be correct" in {
  //    (predicates() prop verbClass).toSet should be(Set(List("55.1", "UNKNOWN")))
  //  }

  "lengths of arg" should "be correct" in {
    (relations() prop constituentLength).toSet should be(Set(2, 7))
    (relations() prop chunkLength).toSet should be(Set(2, 3))
  }

  "chunk features of arg" should "be correct" in {
    (relations() prop chunkEmbedding).toSet should be(Set(
      "nchnks-th:2,e:cont-in-PP,e:cont-in-NP,e:has-ovlp-PP,e:has-ovlp-NP,e:=start-PP,e:=end-NP",
      "nchnks-th:many,e:cont-in-NP,e:cont-in-PP,e:has-ovlp-NP,e:has-ovlp-PP,e:=start-NP,e:=end-NP"
    ))
    // TODO This is not correct: the feature generator is not getting the predicate as a second argument
    (relations() prop chunkPathPattern).toSet should be(Set("<empty>*", "*<empty>"))
  }

  "hasNEG and hasMOD features of arg" should "be correct" in {
    (relations() prop containsNEG).toSet should be(Set("", ""))
    (relations() prop containsMOD).toSet should be(Set("", ""))
  }

}
