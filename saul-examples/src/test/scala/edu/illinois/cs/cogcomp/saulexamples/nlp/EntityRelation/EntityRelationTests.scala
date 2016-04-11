package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import org.scalatest._

class EntityRelationTests extends FlatSpec with Matchers {
  val minScore = 0.3
  EntityRelationDataModel.populateWithConllSmallSet()

  "entity classifier " should " should work. " in {
    EntityRelationClassifiers.loadIndependentEntityModels()
    val scores = PersonClassifier.test() ++ OrganizationClassifier.test() ++ LocationClassifier.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }

  "independent relation classifier " should " should work. " in {
    EntityRelationClassifiers.loadIndependentRelationModels()
    val scores = WorksForClassifier.test() ++ LivesInClassifier.test() ++
      LocatedInClassifier.test() ++ OrgBasedInClassifier.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }

  "pipeline relation classifiers " should " should work. " in {
    EntityRelationClassifiers.loadIndependentEntityModels()
    EntityRelationClassifiers.loadPipelineRelationModels()
    val scores = WorksForClassifierPipeline.test() ++ LivesInClassifierPipeline.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }
}