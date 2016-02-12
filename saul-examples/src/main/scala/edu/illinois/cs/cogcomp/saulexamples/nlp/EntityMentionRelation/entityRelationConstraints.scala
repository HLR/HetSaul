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
}

