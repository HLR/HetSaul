package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers.{ LocationClassifier, OrganizationClassifier, PersonClassifier }
import org.scalatest._

class EntityMentionRelationTests extends FlatSpec with Matchers {
  EntityRelationDataModel.populateWithConllSmallSet()

  "entity classifier " should " should work. " in {
    PersonClassifier.load()
    OrganizationClassifier.load()
    LocationClassifier.load()

    PersonClassifier.test()
    OrganizationClassifier.test()
    LocationClassifier.test()

  }

  "independent relation classifier " should " should work. " in {

  }

  "pipeline relation classifiers " should " should work. " in {

  }
}