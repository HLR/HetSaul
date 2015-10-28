package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationClassifiers._

/** Created by haowu on 1/27/15.
  */
object entityRelationConstraints {

  val Per_Org = ConstrainedClassifier.constraintOf[ConllRelation] {
    x: ConllRelation =>
      {
        (((workForClassifier on x) isTrue) ==>
          (((orgClassifier on x.e2) isTrue) &&&
            ((PersonClassifier on x.e1) isTrue))) &&& (
              ((LivesInClassifier on x) isTrue) ==> (
                ((PersonClassifier on x.e1) isTrue)
                &&& ((LocClassifier on x.e2) isTrue)
              )
            ) &&& ((workForClassifier on x isTrue) ==> (LivesInClassifier on x isNotTrue)) &&& ((LivesInClassifier on x isTrue) ==> (workForClassifier on x isNotTrue))
      }
  }

  val LiveInConstrint = ConstrainedClassifier.constraintOf[ConllRelation] {
    x: ConllRelation =>
      {
        ((LivesInClassifier on x) isTrue) ==> (
          ((PersonClassifier on x.e1) isTrue)
          &&& ((LocClassifier on x.e2) isTrue)
        )
      }
  }

  val PersonWorkFor = ConstrainedClassifier.constraintOf[ConllRelation] {
    x: ConllRelation =>
      {
        ((workForClassifier on x) isTrue) ==>
          ((PersonClassifier on x.e1) isTrue)
      }
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

