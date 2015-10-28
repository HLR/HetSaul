package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.trainingparadigm

import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationDataModel

/** Created by haowu on 5/6/15.
  */
object pipelineTraining {

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
    PersonClassifier.forget()
    orgClassifier.forget()
    //    PersonClassifier.forgot()
    workForClassifierPipe.forget()
  }

  def main(args: Array[String]) {
    forgotEverything()
    entityRelationDataModel.readAll()
    trainIndepedentPipe(it)
  }

}
