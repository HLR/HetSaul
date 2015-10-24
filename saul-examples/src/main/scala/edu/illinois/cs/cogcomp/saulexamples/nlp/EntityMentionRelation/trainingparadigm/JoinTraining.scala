package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.trainingparadigm

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationDataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationDataModel._
import edu.illinois.cs.cogcomp.saul.classifier.JointTrain

/** Created by Parisa on 5/6/15.
  */
object joinTraining {

  def trainJoint(preTrainIteration: Int, jointTrainIteration: Int): Unit = {
    println("Joint Training with Pretraint " + preTrainIteration)
    println("Joint Training with iteration " + jointTrainIteration)
    if (preTrainIteration > 0) {
      orgClassifier.learn(preTrainIteration)
      PersonClassifier.learn(preTrainIteration)
      LocClassifier.learn(preTrainIteration)
      //workForClassifier.learn(preTrainIteration)
      // LivesInClassifier.learn(preTrainIteration)
    }

    JointTrain.train[ConllRelation](entityRelationDataModel, PerConstraintClassifier :: orgConstraintClassifier :: LocConstraintClassifier :: P_O_relationClassifier :: LiveIn_P_O_relationClassifier :: Nil, jointTrainIteration)
    //    JointTrain.train[ConllRelation](entityRelationDataModel,  P_O_relationClassifier  :: LiveIn_P_O_relationClassifier ::Nil,it)
  }

  def forgetEverything() = {

    PersonClassifier.forget()
    orgClassifier.forget()
    //    PersonClassifier.forgot()
    workForClassifier.forget()
  }

  def main(args: Array[String]) {

    forgetEverything()
    entityRelationDataModel.readAll()

    val testRels = entityRelationDataModel.getNodeWithType[ConllRelation].getTestingInstances.toList
    val testTokens = entityRelationDataModel.getNodeWithType[ConllRawToken].getTestingInstances.toList

    trainJoint(preTrainIteration = 1, jointTrainIteration = 5)

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
