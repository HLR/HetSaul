package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import EntityRelationClassifiers._

object EntityRelationConstraints {

  val relationArgumentConstraints = ConstrainedClassifier.constraint[ConllRelation] {
    x: ConllRelation =>
      worksForConstraint(x) and livesInConstraint(x) and worksForImpliesNotLivesIn(x)
  }

  val livesInConstraint = ConstrainedClassifier.constraint[ConllRelation] {
    x: ConllRelation =>
      ((LivesInClassifier on x) isTrue) ==> (
        ((PersonClassifier on x.e1) isTrue)
        and ((LocationClassifier on x.e2) isTrue)
      )
  }

  val worksForConstraint = ConstrainedClassifier.constraint[ConllRelation] {
    x: ConllRelation =>
      {
        ((WorksForClassifier on x) isTrue) ==>
          (((OrganizationClassifier on x.e2) isTrue) and
            ((PersonClassifier on x.e1) isTrue))
      }
  }

  val worksForImpliesNotLivesIn = ConstrainedClassifier.constraint[ConllRelation] {
    x: ConllRelation =>
      ((WorksForClassifier on x isTrue) ==> (LivesInClassifier on x isNotTrue)) and
        ((LivesInClassifier on x isTrue) ==> (WorksForClassifier on x isNotTrue))

  }

  //TODO these commented out codes probably should be used in new examples
  //  val Per_Org=ConstraintClassifier.constraintOf[ConllRelation]  {
  //    x:ConllRelation=>{
  //      {OrgWorkFor on x &&& PersonWorkFor(x)}
  //    }
  //  }

  //
  //  val LocatedInConstrint = ConstraintClassifier.constraintOf[ConllRelation] {
  //    x: ConllRelation => {
  //      ((locatedInClassifier on x) isTrue) ==> (
  //        ((PersonClassifier on x) is "Loc") ||| ((orgClassifier on x) is "Org")
  //          &&& ((LocClassifier on x) is "Loc"))
  //    }
  //  }
  //  val Org_basedConstrint = ConstraintClassifier.constraintOf[ConllRelation] {
  //    x: ConllRelation => {
  //      ((org_baseClassifier on x) is "OrgBased_In") ==> (
  //        ((orgClassifier on x) is "Org")
  //          &&& ((LocClassifier on x) is "Loc"))
  //    }
  //  }
  //
  //  val workForSentenceLevel = ConstraintClassifier.constraintOf[ConllRawSentence] {
  //    x: ConllRawSentence => {
  //      x.relations _forAll {
  //        n: ConllRelation => {
  //          Per_Org(n)
  //        }
  //      }
  //    }
  //  }

}

