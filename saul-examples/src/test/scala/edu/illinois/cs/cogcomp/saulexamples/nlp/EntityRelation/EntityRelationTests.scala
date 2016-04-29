package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers.{ WorksFor_PerOrg_ConstrainedClassifier, OrgConstrainedClassifier, PerConstrainedClassifier }
import org.scalatest._

class EntityRelationTests extends FlatSpec with Matchers {
  val minScore = 0.3
  EntityRelationDataModel.populateWithConllSmallSet()

  "entity classifier " should " should work. " in {
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      PersonClassifier, OrganizationClassifier, LocationClassifier
    )
    val scores = PersonClassifier.test() ++ OrganizationClassifier.test() ++ LocationClassifier.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }

  "independent relation classifier " should " should work. " in {
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier
    )
    val scores = WorksForClassifier.test() ++ LivesInClassifier.test() ++
      LocatedInClassifier.test() ++ OrgBasedInClassifier.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }

  "pipeline relation classifiers " should " should work. " in {
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifierPipeline, LivesInClassifierPipeline
    )
    val scores = WorksForClassifierPipeline.test() ++ LivesInClassifierPipeline.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }

  "L+I entity-relation classifiers " should " should work. " in {
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier
    )
    val scores = PerConstrainedClassifier.test() ++ WorksFor_PerOrg_ConstrainedClassifier.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }
}