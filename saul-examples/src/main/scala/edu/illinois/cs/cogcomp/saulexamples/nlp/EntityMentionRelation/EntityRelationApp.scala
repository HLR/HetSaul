package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.lbjava.learn.{LinearThresholdUnit, SparseNetworkLearner, SupportVectorMachine}
import edu.illinois.cs.cogcomp.saul.classifier.JointTrain
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationConstrainedClassifiers._

object EntityRelationApp {
  def main(args: Array[String]): Unit = {
    /** Choose the experiment you're interested in by changing the following line */
    val testType = ERExperimentType.IndependentClassifiers

    testType match {
      case ERExperimentType.IndependentClassifiers => trainIndependentClassifiers()
      case ERExperimentType.PipelineTraining => runPipelineTraining()
      case ERExperimentType.LPlusITraining => runLPlusI()
      case ERExperimentType.JointTraining => runJointTraining()
      case ERExperimentType.TestFromModel => testIndependentClassifiers()
    }
  }

  object ERExperimentType extends Enumeration {
    val IndependentClassifiers, LPlusITraining, TestFromModel, JointTraining, PipelineTraining = Value
  }

  /** in this scenario we train and test classifiers independent of each other. In particular, the relation classifier
    * does not know the labels of its entity arguments, and the entity classifier does not know the labels of relations
    * in the sentence either
    */
  def trainIndependentClassifiers(): Unit = {
    val foldSize = 5
    EntityRelationDataModel.populateWithConll()

    println("===============================================")
    println("Person Classifier Evaluation")
    //    PersonClassifier.crossValidation(foldSize)
    PersonClassifier.learn(5)
    println("PersonClassifier.classifier.demandLexicon().size() = " + PersonClassifier.classifier.demandLexicon().size())
    //    println("===============================================")
    //    println("Organization Classifier Evaluation")
    //    OrganizationClassifier.crossValidation(foldSize)
    //    println("===============================================")
    //    println("Location Classifier Evaluation")
    //    LocationClassifier.crossValidation(foldSize)
    //    println("===============================================")

/*
    val p = PersonClassifier.classifier.getParameters.asInstanceOf[SupportVectorMachine.Parameters]
    val c = PersonClassifier.classifier.asInstanceOf[SupportVectorMachine]
    println(c.getWeights.mkString("-"))
    println(p.nonDefaultString())
    println("p.bias = " + p.bias)
    println("p.C = " + p.C)
    println("p.displayLL = " + p.displayLL)
    println("p.epsilon = " + p.epsilon)
    println("p.solverType = " + p.solverType)
    println("PersonClassifier.classifier.getNumClasses = " + PersonClassifier.classifier.getNumClasses)
*/

/*    val p = PersonClassifier.classifier.getParameters.asInstanceOf[SparseNetworkLearner.Parameters]
    println("PersonClassifier.classifier.getNumExamples = " + PersonClassifier.classifier.getNumExamples)
    println("PersonClassifier.classifier.getNumFeatures = " + PersonClassifier.classifier.getNumFeatures)
    println("PersonClassifier.classifier.getNetwork.size() = " + PersonClassifier.classifier.getNetwork.size())
    println("PersonClassifier.classifier.getNetwork.toString = " + PersonClassifier.classifier.getNetwork.toString)
    println("nonDefaultString = " + p.nonDefaultString())
    println("getAllowableValues = " + p.baseLTU.getAllowableValues)
    //val ltuArray = PersonClassifier.classifier.getNetwork.toArray.asInstanceOf[Array[LinearThresholdUnit]]
    val ltu0 = PersonClassifier.classifier.getNetwork.toArray.apply(0).asInstanceOf[LinearThresholdUnit]
    val ltu1 = PersonClassifier.classifier.getNetwork.toArray.apply(1).asInstanceOf[LinearThresholdUnit]


    println("===========\n baseLTU = ")
    printlnLTU(p.baseLTU)
    println("===========")

    println("===========\n ltuArray(0) = ")
    printlnLTU(ltu0)
    println("===========")

    println("===========\n ltuArray(1) = ")
    printlnLTU(ltu1)
    println("===========")

    def printlnLTU(ltu: LinearThresholdUnit): Unit ={
      println("ltu.getBias = " + ltu.getBias)
      println("ltu.getThreshold = " + ltu.getThreshold)
      println("ltu.getInitialWeight = " + ltu.getInitialWeight)
      println("ltu.getPositiveThickness = " + ltu.getPositiveThickness)
      println("ltu.getNegativeThickness = " + ltu.getNegativeThickness)
      println("ltu.getWeightVector.getWeights.size() = " + ltu.getWeightVector.getWeights.size())
      println("ltu.getWeightVector.getWeights. = " + ltu.getWeightVector.getWeights.toArray.mkString("-"))
    }*/

    testEntityModels()
    saveEntityModels()

    // independent relation classifiers
    //    println("=================================")
    //    println("WorksFor Classifier Evaluation")
    //    WorksForClassifier.crossValidation(foldSize)
    //    println("=================================")
    //    println("LivesIn Classifier Evaluation")
    //    LivesInClassifier.crossValidation(foldSize)
    //    println("=================================")
    //
    //    saveIndependentRelationModels()
  }

  def trainCVIndependentClassifiers() = {
    EntityRelationDataModel.populateWithConll()
    PersonClassifier.crossValidation(5, 5, saveModels = true)
    PersonClassifier.test()
  }

  def testIndependentClassifiers() = {
    EntityRelationDataModel.populateWithConll()
    loadIndependentEntityModels()
    //    println(PersonClassifier.classifier.getParameters.nonDefaultString())
    //    println(PersonClassifier.classifier.getParameters.toString)
    //    println()
/*
    val p = PersonClassifier.classifier.getParameters.asInstanceOf[SupportVectorMachine.Parameters]
    val c = PersonClassifier.classifier.asInstanceOf[SupportVectorMachine]
    println(c.getWeights.mkString("-"))
    println(p.nonDefaultString())
    println("p.bias = " + p.bias)
    println("p.C = " + p.C)
    println("p.displayLL = " + p.displayLL)
    println("p.epsilon = " + p.epsilon)
    println("p.solverType = " + p.solverType)
    println("PersonClassifier.classifier.getNumClasses = " + PersonClassifier.classifier.getNumClasses)
*/

/*    val p = PersonClassifier.classifier.getParameters.asInstanceOf[SparseNetworkLearner.Parameters]
    println("PersonClassifier.classifier.getNumExamples = " + PersonClassifier.classifier.getNumExamples)
    println("PersonClassifier.classifier.getNumFeatures = " + PersonClassifier.classifier.getNumFeatures)
    println("PersonClassifier.classifier.getNetwork.size() = " + PersonClassifier.classifier.getNetwork.size())
    println("PersonClassifier.classifier.getNetwork.toString = " + PersonClassifier.classifier.getNetwork.toString)
    println("nonDefaultString = " + p.nonDefaultString())
    println("getAllowableValues = " + p.baseLTU.getAllowableValues)
    //val ltuArray = PersonClassifier.classifier.getNetwork.toArray.asInstanceOf[Array[LinearThresholdUnit]]
    val ltu0 = PersonClassifier.classifier.getNetwork.toArray.apply(0).asInstanceOf[LinearThresholdUnit]
    val ltu1 = PersonClassifier.classifier.getNetwork.toArray.apply(1).asInstanceOf[LinearThresholdUnit]


    println("===========\n baseLTU = ")
    printlnLTU(p.baseLTU)
    println("===========")

    println("===========\n ltuArray(0) = ")
    printlnLTU(ltu0)
    println("===========")

    println("===========\n ltuArray(1) = ")
    printlnLTU(ltu1)
    println("===========")

    def printlnLTU(ltu: LinearThresholdUnit): Unit ={
      println("ltu.getBias = " + ltu.getBias)
      println("ltu.getThreshold = " + ltu.getThreshold)
      println("ltu.getInitialWeight = " + ltu.getInitialWeight)
      println("ltu.getPositiveThickness = " + ltu.getPositiveThickness)
      println("ltu.getNegativeThickness = " + ltu.getNegativeThickness)
      println("ltu.getWeightVector.getWeights.size() = " + ltu.getWeightVector.getWeights.size())
      println("ltu.getWeightVector.getWeights. = " + ltu.getWeightVector.getWeights.toArray.mkString("-"))
    }*/

    tokensTest.slice(0, 20).foreach{tok => println(PersonClassifier.classifier.discreteValue(tok)) }
    testEntityModels()
    println("PersonClassifier.classifier.demandLexicon().size() = " + PersonClassifier.classifier.demandLexicon().size())
  }

  /** in this scenario the named entity recognizers are trained independently, and given to a relation classifier as
    * a tool to extract features (hence the name "pipeline"). This approach first trains an entity classifier, and
    * then uses the prediction of entities in addition to other local features to learn the relation identifier.
    */
  def runPipelineTraining(): Unit = {
    val foldSize = 5
    EntityRelationDataModel.populateWithConll()

    println("Running CV " + foldSize)

    // train independent classifiers
    //    PersonClassifier.crossValidation(foldSize)
    //    OrganizationClassifier.crossValidation(foldSize)
    //    LocationClassifier.crossValidation(foldSize)
    //    saveEntityModels()

    PersonClassifier.load()
    OrganizationClassifier.load()
    LocationClassifier.load()

    // train pipeline relation models, which use the prediction of the entity classifiers
    WorksForClassifierPipeline.crossValidation(foldSize)
    LivesInClassifierPipeline.crossValidation(foldSize)

    savePipelineRelationModels()
  }

  /** In the scenario the classifiers are learned independently but at the test time we use constrained inference to
    * maintain structural consistency (which would justify the naming "Learning Plus Inference" (L+I).
    */
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
    WorksFor_PerOrg_ConstrainedClassifier.test(pairs())
    println("=================================")
    println("LivesIn Classifier Evaluation")
    println("=================================")
    LivesIn_PerOrg_relationConstrainedClassifier.test(pairs())
    println("=================================")
  }

  /** here we meanwhile training classifiers, we use global inference, in order to overcome the poor local
    * classifications and yield accurate global classifications.
    */
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
        WorksFor_PerOrg_ConstrainedClassifier :: LivesIn_PerOrg_relationConstrainedClassifier :: Nil,
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
    JointTrain.testClassifiers(WorksFor_PerOrg_ConstrainedClassifier.classifier, (relationType is "Work_For").classifier, testRels)

    println(Console.BLUE + "Live_In")
    JointTrain.testClassifiers(LivesInClassifier.classifier, (relationType is "Live_In").classifier, testRels)

    println(Console.RED + "Live_In")
    JointTrain.testClassifiers(LivesIn_PerOrg_relationConstrainedClassifier.classifier, (relationType is "Live_In").classifier, testRels)
  }
}
