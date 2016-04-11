package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saul.classifier.JointTrain
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._

object EntityRelationApp {
  def main(args: Array[String]): Unit = {
    /** Choose the experiment you're interested in by changing the following line */

    val testType = ERExperimentType.TestFromModel

    testType match {
      case ERExperimentType.IndependentClassifiers => trainIndependentClassifiers()
      case ERExperimentType.TestFromModel => testIndependentClassifiers()
      case ERExperimentType.PipelineTraining => runPipelineTraining()
      case ERExperimentType.PipelineTestFromModel => testPipelineRelationModels()
      case ERExperimentType.LPlusI => runLPlusI()
      case ERExperimentType.JointTraining => runJointTraining()
    }
  }

  object ERExperimentType extends Enumeration {
    val IndependentClassifiers, LPlusI, TestFromModel, JointTraining, PipelineTraining, PipelineTestFromModel = Value
  }

  /** in this scenario we train and test classifiers independent of each other. In particular, the relation classifier
    * does not know the labels of its entity arguments, and the entity classifier does not know the labels of relations
    * in the sentence either
    */
  def trainIndependentClassifiers(): Unit = {
    EntityRelationDataModel.populateWithConll()
    val iter = 10
    println("==============================================")
    println("Person Classifier Evaluation")
    PersonClassifier.learn(iter)
    println("==============================================")
    println("Organization Classifier Evaluation")
    OrganizationClassifier.learn(iter)
    println("==============================================")
    println("Location Classifier Evaluation")
    LocationClassifier.learn(iter)
    println("==============================================")
    testEntityModels()
    saveEntityModels()

    println("==============================================")
    println("WorkFor Classifier Evaluation")
    WorksForClassifier.learn(iter)
    println("==============================================")
    println("LivesIn Classifier Evaluation")
    LivesInClassifier.learn(iter)
    println("==============================================")
    println("LocatedIn Classifier Evaluation")
    LocatedInClassifier.learn(iter)
    println("==============================================")
    println("OrgBasedIn Classifier Evaluation")
    OrgBasedInClassifier.learn(iter)
    println("==============================================")

    testIndependentRelationModels()
    saveIndependentRelationModels()
  }

  /** This function loads the classifiers trained in function [[trainIndependentClassifiers]] and evaluates on the
    * test data.
    */
  def testIndependentClassifiers() = {
    EntityRelationDataModel.populateWithConll()
    loadIndependentEntityModels()
    testEntityModels()
    loadIndependentRelationModels()
    testIndependentRelationModels()
  }

  /** in this scenario the named entity recognizers are trained independently, and given to a relation classifier as
    * a tool to extract features (hence the name "pipeline"). This approach first trains an entity classifier, and
    * then uses the prediction of entities in addition to other local features to learn the relation identifier.
    */
  def runPipelineTraining(): Unit = {
    EntityRelationDataModel.populateWithConll()

    loadIndependentEntityModels()

    // train pipeline relation models, which use the prediction of the entity classifiers
    val iter = 10
    WorksForClassifierPipeline.learn(iter)
    LivesInClassifierPipeline.learn(iter)
    testPipelineModels()
    savePipelineRelationModels()
  }

  /** this function loads the models of the pipeline classifiers and evaluates them on the test data */
  def testPipelineRelationModels(): Unit = {
    EntityRelationDataModel.populateWithConll()
    loadIndependentEntityModels()
    loadPipelineRelationModels()
    testPipelineModels()
  }

  /** In the scenario the classifiers are learned independently but at the test time we use constrained inference to
    * maintain structural consistency (which would justify the naming "Learning Plus Inference" (L+I).
    */
  def runLPlusI() {
    EntityRelationDataModel.populateWithConll()

    // independent entity classifiers
    loadIndependentEntityModels()

    // independent relation classifiers
    loadIndependentRelationModels()

    // test using the constraints
    println("==============================================")
    println("Person Classifier Evaluation with training")
    PerConstrainedClassifier.test(tokens())
    println("==============================================")
    println("Organization Classifier Evaluation")
    OrgConstrainedClassifier.test(tokens())
    println("==============================================")
    println("Location Classifier Evaluation")
    LocConstrainedClassifier.test(tokens())
    println("==============================================")
    println("WorkFor Classifier Evaluation")
    WorksFor_PerOrg_ConstrainedClassifier.test(pairs())
    println("==============================================")
    println("LivesIn Classifier Evaluation")
    LivesIn_PerOrg_relationConstrainedClassifier.test(pairs())
    println("==============================================")
  }

  /** here we meanwhile training classifiers, we use global inference, in order to overcome the poor local
    * classifications and yield accurate global classifications.
    */
  def runJointTraining() {
    populateWithConll()
    val testRels = pairs.getTrainingInstances.toList
    val testTokens = tokens.getTrainingInstances.toList

    // load pre-trained models
    loadIndependentEntityModels()
    loadIndependentRelationModels()

    // joint training
    val jointTrainIteration = 5
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
