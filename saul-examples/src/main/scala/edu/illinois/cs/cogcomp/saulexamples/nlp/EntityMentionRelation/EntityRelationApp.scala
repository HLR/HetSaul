package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.core.utilities.configuration.{ ResourceManager, Property, Configurator }
import edu.illinois.cs.cogcomp.saul.classifier.JointTrain
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationConstrainedClassifiers._
import EntityRelationDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSConfigurator._

object EntityRelationApp {
  def main(args: Array[String]): Unit = {
    /** Choose the experiment you're interested in by changing the following line */
    val testType = ERExperimentType.IndependentClassifiers

    testType match {
      case ERExperimentType.IndependentClassifiers => trainIndependentClassifiers()
      case ERExperimentType.PipelineTraining => runPipelineTraining()
      case ERExperimentType.LPlusITraining => runLPlusI()
      case ERExperimentType.JointTraining => runJointTraining()
      case ERExperimentType.TestFromModel => runLPlusI()
    }
  }

  object ERExperimentType extends Enumeration {
    val IndependentClassifiers, LPlusITraining, TestFromModel, JointTraining, PipelineTraining = Value
  }

  object ERConfigurator extends Configurator {
    private val prefix = "../data/EntityMentionRelation/"
    val trainData = new Property("trainData", prefix + "00-18.br")

    override def getDefaultConfig: ResourceManager = {
      val props = Array(trainData, trainDataSmall, trainAndDevData, testData)
      new ResourceManager(generateProperties(props))
    }
  }

  /** in this scenario we train and test classifiers independent of each other */
  def trainIndependentClassifiers(): Unit = {
    val foldSize = 5
    EntityRelationDataModel.populateWithConll()
    // independent entity classifiers
    println("Person Classifier Evaluation")
    println("=================================")
    PersonClassifier.crossValidation(foldSize)
    println("=================================")
    println("Organization Classifier Evaluation")
    println("=================================")
    OrganizationClassifier.crossValidation(foldSize)
    println("=================================")
    println("Location Classifier Evaluation")
    println("=================================")
    LocationClassifier.crossValidation(foldSize)
    println("=================================")

    saveEntityModels()

    // independent relation classifiers
    //    println("WorkFor Classifier Evaluation")
    //    println("=================================")
    //    WorksForClassifier.crossValidation(foldSize)
    //    println("=================================")
    //    println("LivesIn Classifier Evaluation")
    //    println("=================================")
    //    LivesInClassifier.crossValidation(foldSize)
    //    println("=================================")
    //
    //    saveIndependentRelationModels()
  }

  def testIndependentClassifiers() = {
    //    EntityRelationDataModel.populateWithConll()
    //
    //    PersonClassifier.test()
    //    OrganizationClassifier.crossValidation(foldSize)
    //    LocationClassifier.crossValidation(foldSize)
  }

  def runPipelineTraining(): Unit = {
    val foldSize = 5
    EntityRelationDataModel.populateWithConll()

    println("Running CV " + foldSize)

    // train independent classifiers
    PersonClassifier.crossValidation(foldSize)
    OrganizationClassifier.crossValidation(foldSize)
    LocationClassifier.crossValidation(foldSize)

    saveEntityModels()

    // train pipeline relation models, which use the prediction of the entity classifiers
    WorkForClassifierPipeline.crossValidation(foldSize)
    LivesInClassifierPipeline.crossValidation(foldSize)
  }

  def runLPlusI() {
    val foldSize = 5
    EntityRelationDataModel.populateWithConll()

    // independent entity classifiers
    PersonClassifier.learn(foldSize)
    OrganizationClassifier.learn(foldSize)
    LocationClassifier.learn(foldSize)

    // independent relation classifiers
    WorksForClassifier.learn(foldSize)
    LivesInClassifier.learn(foldSize)

    // test using the constraints
    println("Person Classifier Evaluation with training")
    println("=================================")
    PerConstrainedClassifier.test(tokens())
    println("=================================")
    println("Organization Classifier Evaluation")
    println("=================================")
    OrgConstrainedClassifier.test(tokens())
    println("=================================")
    println("Location Classifier Evaluation")
    println("=================================")
    LocConstrainedClassifier.test(tokens())
    println("=================================")
    println("WorkFor Classifier Evaluation")
    println("=================================")
    Work_P_O_relationClassifier.test(pairs())
    println("=================================")
    println("LivesIn Classifier Evaluation")
    println("=================================")
    LiveIn_P_O_relationClassifier.test(pairs())
    println("=================================")
  }

  def runJointTraining() {
    populateWithConll()
    val testRels = pairs.getTrainingInstances.toList
    val testTokens = tokens.getTrainingInstances.toList

    val preTrainIteration = 1
    val jointTrainIteration = 5

    println(s"Pre-train $preTrainIteration iterations.")
    if (preTrainIteration > 0) {
      OrganizationClassifier.learn(preTrainIteration)
      PersonClassifier.learn(preTrainIteration)
      LocationClassifier.learn(preTrainIteration)
    }

    println(s"Joint training $jointTrainIteration iterations. ")
    JointTrain.train[ConllRelation](
      EntityRelationDataModel,
      PerConstrainedClassifier :: OrgConstrainedClassifier :: LocConstrainedClassifier ::
        Work_P_O_relationClassifier :: LiveIn_P_O_relationClassifier :: Nil,
      jointTrainIteration
    )

    println(Console.BLUE + "Peop")
    JointTrain.testClassifiers(PersonClassifier.classifier, PersonClassifier.label.classifier, testTokens)

    println(Console.RED + "Peop")
    JointTrain.testClassifiers(PerConstrainedClassifier.classifier, (entityType is "Peop").classifier, testTokens)

    println(Console.BLUE + "Org")
    JointTrain.testClassifiers(OrganizationClassifier.classifier, (entityType is "Org").classifier, testTokens)

    println(Console.RED + "Org")
    JointTrain.testClassifiers(OrgConstrainedClassifier.classifier, (entityType is "Org").classifier, testTokens)

    println(Console.BLUE + "Loc")
    JointTrain.testClassifiers(LocationClassifier.classifier, (entityType is "Loc").classifier, testTokens)

    println(Console.RED + "Loc")
    JointTrain.testClassifiers(LocConstrainedClassifier.classifier, (entityType is "Loc").classifier, testTokens)

    println(Console.BLUE + "Work_For")
    JointTrain.testClassifiers(WorksForClassifier.classifier, (relationType is "Work_For").classifier, testRels)

    println(Console.RED + "Work_For")
    JointTrain.testClassifiers(Work_P_O_relationClassifier.classifier, (relationType is "Work_For").classifier, testRels)

    println(Console.BLUE + "Live_In")
    JointTrain.testClassifiers(LivesInClassifier.classifier, (relationType is "Live_In").classifier, testRels)

    println(Console.RED + "Live_In")
    JointTrain.testClassifiers(LiveIn_P_O_relationClassifier.classifier, (relationType is "Live_In").classifier, testRels)
  }
}
