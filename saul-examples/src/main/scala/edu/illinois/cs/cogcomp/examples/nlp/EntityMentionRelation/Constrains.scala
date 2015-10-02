package edu.illinois.cs.cogcomp.examples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.er_task.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.lfs.classifier.ConstraintClassifier
import edu.illinois.cs.cogcomp.lfs.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.examples.nlp.EntityMentionRelation.Classifiers._

/** Created by haowu on 1/27/15.
  */
object Constrains {

  val Per_Org = ConstraintClassifier.constraintOf[ConllRelation] {
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

  val LiveInConstrint = ConstraintClassifier.constraintOf[ConllRelation] {
    x: ConllRelation =>
      {
        ((LivesInClassifier on x) isTrue) ==> (
          ((PersonClassifier on x.e1) isTrue)
          &&& ((LocClassifier on x.e2) isTrue)
        )
      }
  }

  val PersonWorkFor = ConstraintClassifier.constraintOf[ConllRelation] {
    x: ConllRelation =>
      {
        ((workForClassifier on x) isTrue) ==>
          ((PersonClassifier on x.e1) isTrue)
      }
  }

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

