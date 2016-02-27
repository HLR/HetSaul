package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saul.classifier.JointTrain
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationConstrainedClassifiers._

object EntityRelationApp {
  def main(args: Array[String]): Unit = {
    /** Choose the experiment you're interested in by changing the following line */
    val testType = ERExperimentType.TestFromModel

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
    println("WorksFor Classifier Evaluation")
    println("=================================")
    WorksForClassifier.crossValidation(foldSize)
    println("=================================")
    println("LivesIn Classifier Evaluation")
    println("=================================")
    LivesInClassifier.crossValidation(foldSize)
    println("=================================")

    saveIndependentRelationModels()
  }

  def testIndependentClassifiers() = {
    EntityRelationDataModel.populateWithConll()
    //    loadIndependentEntityModels()

    PersonClassifier.learn(5)
    //    PersonClassifier.save()

    //    PersonClassifier.load()

    //    PersonClassifier.load()
    //    OrganizationClassifier.load()
    //    LocationClassifier.load()

    println(PersonClassifier.test())
    //    println(OrganizationClassifier.test())
    //    println(LocationClassifier.test())

    //        val out = EntityRelationSensors.testSentences.asScala.flatMap( sentenceToTokens_GeneratingSensor )

    //      PersonClassifier(  )
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
