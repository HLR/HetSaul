package edu.illinois.cs.cogcomp.examples.nlp.EntityMentionRelation.training_paradigm

import edu.illinois.cs.cogcomp.examples.nlp.EntityMentionRelation.Classifiers._
import edu.illinois.cs.cogcomp.examples.nlp.EntityMentionRelation.ErDataModelExample

/** Created by haowu on 5/6/15.
  */
object PipelineTraining {

  val it = 20

  def trainIndepedentPipe(it: Int): Unit = {
    println("Indepent Training with iteration " + it)
    PersonClassifier.learn(it)
    PersonClassifier.test()
    orgClassifier.learn(it)
    orgClassifier.test()
    LocClassifier.learn(it)
    LocClassifier.test()
    workForClassifierPipe.learn(it)
    workForClassifierPipe.test()
    LivesInClassifierPipe.learn(it)
    LivesInClassifierPipe.test()
  }

  def cv(it: Int): Unit = {

    println("Running CV " + it)

    PersonClassifier.crossValidation(it)
    orgClassifier.crossValidation(it)
    LocClassifier.crossValidation(it)

    workForClassifierPipe.crossValidation(it)
    LivesInClassifierPipe.crossValidation(it)

  }

  def forgotEverything() = {
    PersonClassifier.forgot()
    orgClassifier.forgot()
    //    PersonClassifier.forgot()
    workForClassifierPipe.forgot()
  }

  def main(args: Array[String]) {
    forgotEverything()
    ErDataModelExample.readAll()
    trainIndepedentPipe(it)
  }

}
