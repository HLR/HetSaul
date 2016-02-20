package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import EntityRelationClassifiers._
import EntityRelationClassifiers.orgClassifier

object EntityRelationConstraints {

  val relationArgumentConstraints = ConstrainedClassifier.constraint[ConllRelation] {
    x: ConllRelation =>
      {
        workForConstraint(x) and liveInConstraint(x) and worksForImpliesNotLivesIn(x)
      }
  }

  val liveInConstraint = ConstrainedClassifier.constraint[ConllRelation] {
    x: ConllRelation =>
      {
        ((livesInClassifier on x) isTrue) ==> (
          ((personClassifier on x.e1) isTrue)
          and ((locationClassifier on x.e2) isTrue)
        )
      }
  }

  val workForConstraint = ConstrainedClassifier.constraint[ConllRelation] {
    x: ConllRelation =>
      {
        ((worksForClassifier on x) isTrue) ==>
          (((orgClassifier on x.e2) isTrue) and
            ((personClassifier on x.e1) isTrue))
      }
  }

  val worksForImpliesNotLivesIn = ConstrainedClassifier.constraint[ConllRelation] {
    x: ConllRelation =>
      ((worksForClassifier on x isTrue) ==> (livesInClassifier on x isNotTrue)) and
        ((livesInClassifier on x isTrue) ==> (worksForClassifier on x isNotTrue))

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

