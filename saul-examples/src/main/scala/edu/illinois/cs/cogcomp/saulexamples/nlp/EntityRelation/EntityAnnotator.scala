/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.annotation.Annotator
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation._
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawSentence, ConllRawToken }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._

import scala.collection.JavaConversions._
import scala.collection.mutable

object EntityAnnotator {
  private val entityClassifiers = List(PersonClassifier, OrganizationClassifier, LocationClassifier)

  /** Get the prediction for the entity from the set of binary entity classifiers.
    *
    * Note: Assumes that the token is already present in the populated data model.
    */
  private def getEntityPrediction(token: ConllRawToken): String = {
    val predictions = entityClassifiers.flatMap({ clf: Learnable[ConllRawToken] =>
      val scores = clf.classifier.scores(token)
      val highScoreLabel = clf.classifier.discreteValue(token)
      val highScore = scores.getScore(highScoreLabel).score

      if (highScoreLabel.equalsIgnoreCase("true")) {
        clf match {
          case PersonClassifier => Some(("PER", highScore))
          case OrganizationClassifier => Some(("ORG", highScore))
          case LocationClassifier => Some(("LOC", highScore))
        }
      } else {
        None
      }
    })

    // Get the most confident positive prediction
    if (predictions.nonEmpty) predictions.maxBy(_._2)._1 else ""
  }
}

class EntityAnnotator(val finalViewName: String)
  extends Annotator(finalViewName, Array(ViewNames.TOKENS, ViewNames.POS)) {

  override def initialize(rm: ResourceManager): Unit = {}

  override def addView(ta: TextAnnotation): Unit = {
    val tokensView = ta.getView(ViewNames.TOKENS)
    val posView = ta.getView(ViewNames.POS)
    val entityView = new SpanLabelView(finalViewName, "EntityAnnotator", ta, 1.0)

    // HashMap to make adding entity view easier.
    val tokenToConstituentMap = mutable.HashMap[ConllRawToken, Constituent]()

    val allSentences = ta.sentences().map({ sentence: Sentence =>
      val rawSentence = new ConllRawSentence(sentence.getSentenceId)

      tokensView.getConstituentsCoveringSpan(sentence.getStartSpan, sentence.getEndSpan)
        .foreach({ cons: Constituent =>
          val posTag = posView.getLabelsCovering(cons).head
          val sentenceToken = new ConllRawToken()
          sentenceToken.setPhrase(cons.getSurfaceForm)
          sentenceToken.setPOS(posTag)
          sentenceToken.wordId = cons.getStartSpan
          sentenceToken.sentId = rawSentence.sentId

          rawSentence.addTokens(sentenceToken)
          tokenToConstituentMap.put(sentenceToken, cons)
        })

      rawSentence
    })

    // Populate the data model with sentences in TA.
    EntityRelationDataModel.clearInstances()
    EntityRelationDataModel.sentences.populate(allSentences, train = false)

    // Get predictions and populate the output view.
    EntityRelationDataModel.tokens
      .getTestingInstances
      .foreach({ token =>
        tokenToConstituentMap.get(token)
          .foreach({ cons: Constituent =>
            val label = EntityAnnotator.getEntityPrediction(token)

            if (label.nonEmpty) {
              val entityCons = cons.cloneForNewViewWithDestinationLabel(finalViewName, label)
              entityView.addConstituent(entityCons)
            }
          })
      })

    ta.addView(finalViewName, entityView)
  }
}
