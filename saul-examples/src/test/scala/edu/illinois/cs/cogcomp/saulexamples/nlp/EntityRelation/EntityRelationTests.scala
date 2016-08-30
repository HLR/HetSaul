/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._

import org.scalatest._

class EntityRelationTests extends FlatSpec with Matchers {
  val minScore = 0.3
  "entity classifier " should " work. " in {
    sentences.populate(EntityRelationSensors.sentencesSmallSetTest, train = false)
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      PersonClassifier, OrganizationClassifier, LocationClassifier
    )
    val scores = List(PersonClassifier.test(), OrganizationClassifier.test(), LocationClassifier.test())
    scores.foreach { case score => (score.average.f1 > minScore) should be(true) }
    scores.foreach { case score => (score.overall.f1 > minScore) should be(true) }
  }

  "independent relation classifier " should " work. " in {
    sentences.populate(EntityRelationSensors.sentencesSmallSetTest, train = false)
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier
    )
    val scores = List(WorksForClassifier.test(), LivesInClassifier.test(),
      LocatedInClassifier.test(), OrgBasedInClassifier.test())
    scores.foreach { case score => (score.average.f1 > minScore) should be(true) }
    scores.foreach { case score => (score.overall.f1 > minScore) should be(true) }
  }

  "pipeline relation classifiers " should " work. " in {
    sentences.populate(EntityRelationSensors.sentencesSmallSetTest, train = false)
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifierPipeline, LivesInClassifierPipeline
    )
    val scores = List(WorksForClassifierPipeline.test(), LivesInClassifierPipeline.test())
    scores.foreach { case score => (score.average.f1 > minScore) should be(true) }
    scores.foreach { case score => (score.overall.f1 > minScore) should be(true) }
  }

  "L+I entity-relation classifiers " should " work. " in {
    sentences.populate(EntityRelationSensors.sentencesSmallSetTest, train = false)
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier
    )
    val scores = List(PerConstrainedClassifier.test(), WorksFor_PerOrg_ConstrainedClassifier.test())
    scores.foreach { case score => (score.average.f1 > minScore) should be(true) }
    scores.foreach { case score => (score.overall.f1 > minScore) should be(true) }
  }

  "crossValidation on ER " should " work. " in {
    EntityRelationDataModel.clearInstances
    sentences.populate(EntityRelationSensors.sentencesSmallSetTest)
    PersonClassifier.crossValidation(5)
    val results = PersonClassifier.crossValidation(5)
    results.foreach { case score => (score.overall.f1 > minScore) should be(true) }
  }
}