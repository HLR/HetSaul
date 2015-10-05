package edu.illinois.cs.cogcomp.examples.nlp.EntityMentionRelation.training_paradigm

import edu.illinois.cs.cogcomp.examples.nlp.EntityMentionRelation.Classifiers._
import edu.illinois.cs.cogcomp.examples.nlp.EntityMentionRelation.ErDataModelExample

/** Created by Parisa on 5/20/15.
  */
object L_and_I_model {
  val it = 5
  def trainIndepedent(it: Int): Unit = {
    println("Indepent Training with iteration " + it)
    PersonClassifier.learn(it)
    orgClassifier.learn(it)
    LocClassifier.learn(it)

    workForClassifier.learn(it)
    LivesInClassifier.learn(it)

    // Parisa: the data for evalution has not been set here.
    P_O_relationClassifier.test()
    orgConstraintClassifier.test()
    PerConstraintClassifier.test()
    LocConstraintClassifier.test()

    LiveIn_P_O_relationClassifier.test()
  }

  def cv(it: Int): Unit = {

    println("Running CV " + it)

    PersonClassifier.crossValidation(it)
    orgClassifier.crossValidation(it)
    LocClassifier.crossValidation(it)

    workForClassifier.crossValidation(it)
    LivesInClassifier.crossValidation(it)

  }

  def forgotEverything() = {
    PersonClassifier.forget()
    orgClassifier.forget()
    //    PersonClassifier.forgot()
    workForClassifier.forget()
  }

  def main(args: Array[String]) {
    forgotEverything()
    ErDataModelExample.readAll()
    trainIndepedent(it)
  }

}
