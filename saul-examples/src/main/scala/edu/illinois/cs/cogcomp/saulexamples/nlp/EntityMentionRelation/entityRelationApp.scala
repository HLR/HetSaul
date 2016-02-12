package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saul.classifier.JointTrain
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationDataModel._

/** Experiment workspace for playing with language feature. */

object myConfiguration {
  val iterations = 20
  val pipeLine = true
  val fold = 4
}

object entityRelationApp {

  def trainIndepedent(it: Int): Unit = {
    println("Indepent Training with iteration " + it)
    PersonClassifier.learn(it)
    PersonClassifier.test()

    orgClassifier.learn(it)
    orgClassifier.test()

    LocClassifier.learn(it)
    LocClassifier.test()

    workForClassifier.learn(it)
    workForClassifier.test()

    LivesInClassifier.learn(it)
    LivesInClassifier.test()
  }

  def cv(it: Int): Unit = {
    println("Running CV " + it)

    PersonClassifier.crossValidation(it)
    orgClassifier.crossValidation(it)
    LocClassifier.crossValidation(it)

    workForClassifier.crossValidation(it)
    LivesInClassifier.crossValidation(it)

  }

  def trainJoint(preIt: Int, it: Int): Unit = {
    println("Joint Training with Pretraint " + preIt)
    println("Joint Training with iteration " + it)
    orgClassifier.learn(8)
    PersonClassifier.learn(8)
    LocClassifier.learn(8)

    JointTrain.train[ConllRelation](pairedRelations, PerConstraintClassifier :: orgConstraintClassifier :: LocConstraintClassifier :: P_O_relationClassifier :: LiveIn_P_O_relationClassifier :: Nil, it)
  }

  def forgotEverything() = {
    PersonClassifier.forget()
    orgClassifier.forget()
    workForClassifier.forget()
  }

  val pipeLine = myConfiguration.pipeLine

  def main(args: Array[String]) {

    val fold = myConfiguration.fold

    if (pipeLine) {
      println("using pipeline feature")
    }

    val it = myConfiguration.iterations

    forgotEverything()
    entityRelationDataModel.read(fold)

    val testRels = pairedRelations.getTestingInstances.toList
    val testTokens = tokens.getTestingInstances.toList

    trainIndepedent(it)

    println(Console.BLUE + "Peop")
    JointTrain.testClassifiers(PersonClassifier.classifier, (entityType is "Peop").classifier, testTokens)
    println(Console.RED + "Peop")
    JointTrain.testClassifiers(PerConstraintClassifier.classifier, (entityType is "Peop").classifier, testTokens)

    println(Console.BLUE + "Org")
    JointTrain.testClassifiers(orgClassifier.classifier, (entityType is "Org").classifier, testTokens)
    println(Console.RED + "Org")
    JointTrain.testClassifiers(orgConstraintClassifier.classifier, (entityType is "Org").classifier, testTokens)

    println(Console.BLUE + "Loc")
    JointTrain.testClassifiers(LocClassifier.classifier, (entityType is "Loc").classifier, testTokens)
    println(Console.RED + "Loc")
    JointTrain.testClassifiers(LocConstraintClassifier.classifier, (entityType is "Loc").classifier, testTokens)

    println(Console.BLUE + "Work_For")
    JointTrain.testClassifiers(workForClassifier.classifier, (relationType is "Work_For").classifier, testRels)
    println(Console.RED + "Work_For")
    JointTrain.testClassifiers(P_O_relationClassifier.classifier, (relationType is "Work_For").classifier, testRels)

    println(Console.BLUE + "Live_In")
    JointTrain.testClassifiers(LivesInClassifier.classifier, (relationType is "Live_In").classifier, testRels)
    println(Console.RED + "Live_In")
    JointTrain.testClassifiers(LiveIn_P_O_relationClassifier.classifier, (relationType is "Live_In").classifier, testRels)
  }
}