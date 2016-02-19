package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saul.classifier.JointTrain
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationConstrainedClassifiers._

object JoinTraining {

  def trainJoint(preTrainIteration: Int, jointTrainIteration: Int): Unit = {
    println("Joint Training with Pretraint " + preTrainIteration)
    println("Joint Training with iteration " + jointTrainIteration)
    if (preTrainIteration > 0) {
      orgClassifier.learn(preTrainIteration)
      personClassifier.learn(preTrainIteration)
      locationClassifier.learn(preTrainIteration)
    }

    JointTrain.train[ConllRelation](
      EntityRelationDataModel,
      perConstraintClassifier :: orgConstraintClassifier :: locConstraintClassifier :: work_P_O_relationClassifier :: liveIn_P_O_relationClassifier :: Nil,
      jointTrainIteration
    )
  }

  def main(args: Array[String]) {
    import EntityRelationDataModel._
    populateWithConll()
    val testRels = pairs.getTrainingInstances.toList
    val testTokens = tokens.getTrainingInstances.toList

    trainJoint(preTrainIteration = 1, jointTrainIteration = 5)

    println(Console.BLUE + "Peop")
    JointTrain.testClassifiers(personClassifier.classifier, personClassifier.label.classifier, testTokens)
    println(Console.RED + "Peop")
    JointTrain.testClassifiers(perConstraintClassifier.classifier, (entityType is "Peop").classifier, testTokens)

    println(Console.BLUE + "Org")
    JointTrain.testClassifiers(orgClassifier.classifier, (entityType is "Org").classifier, testTokens)
    println(Console.RED + "Org")
    JointTrain.testClassifiers(orgConstraintClassifier.classifier, (entityType is "Org").classifier, testTokens)

    println(Console.BLUE + "Loc")
    JointTrain.testClassifiers(locationClassifier.classifier, (entityType is "Loc").classifier, testTokens)
    println(Console.RED + "Loc")
    JointTrain.testClassifiers(locConstraintClassifier.classifier, (entityType is "Loc").classifier, testTokens)

    println(Console.BLUE + "Work_For")
    JointTrain.testClassifiers(worksForClassifier.classifier, (relationType is "Work_For").classifier, testRels)
    println(Console.RED + "Work_For")
    JointTrain.testClassifiers(work_P_O_relationClassifier.classifier, (relationType is "Work_For").classifier, testRels)

    println(Console.BLUE + "Live_In")
    JointTrain.testClassifiers(livesInClassifier.classifier, (relationType is "Live_In").classifier, testRels)
    println(Console.RED + "Live_In")
    JointTrain.testClassifiers(liveIn_P_O_relationClassifier.classifier, (relationType is "Live_In").classifier, testRels)

  }
}
