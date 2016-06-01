package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saul.classifier.{ ClassifierUtils, JointTrain }
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._

object EntityRelationApp {
  // learned models from the "saul-conll-er-tagger-models" jar package
  val jarModelPath = "edu/illinois/cs/cogcomp/saulexamples/nlp/EntityRelation/models/"

  def main(args: Array[String]): Unit = {
    /** Choose the experiment you're interested in by changing the following line */
    val testType = ERExperimentType.JointTraining

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
    // independent entity and relation classifiers
    ClassifierUtils.TrainClassifiers(iter, PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier)
    ClassifierUtils.TestClassifiers(PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier)
    ClassifierUtils.SaveClassifiers(PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier)
  }

  /** This function loads the classifiers trained in function [[trainIndependentClassifiers]] and evaluates on the
    * test data.
    */
  def testIndependentClassifiers() = {
    EntityRelationDataModel.populateWithConll()
    ClassifierUtils.LoadClassifier(
      jarModelPath,
      PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier
    )
    ClassifierUtils.TestClassifiers(PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier)
  }

  /** in this scenario the named entity recognizers are trained independently, and given to a relation classifier as
    * a tool to extract features (hence the name "pipeline"). This approach first trains an entity classifier, and
    * then uses the prediction of entities in addition to other local features to learn the relation identifier.
    */
  def runPipelineTraining(): Unit = {
    EntityRelationDataModel.populateWithConll()
    ClassifierUtils.LoadClassifier(jarModelPath, PersonClassifier, OrganizationClassifier, LocationClassifier)

    // train pipeline relation models, which use the prediction of the entity classifiers
    val iter = 10
    ClassifierUtils.TrainClassifiers(iter, WorksForClassifierPipeline, LivesInClassifierPipeline)
    ClassifierUtils.TestClassifiers(WorksForClassifierPipeline, LivesInClassifierPipeline)
    ClassifierUtils.SaveClassifiers(WorksForClassifierPipeline, LivesInClassifierPipeline)
  }

  /** this function loads the models of the pipeline classifiers and evaluates them on the test data */
  def testPipelineRelationModels(): Unit = {
    EntityRelationDataModel.populateWithConll()
    ClassifierUtils.LoadClassifier(jarModelPath, PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifierPipeline, LivesInClassifierPipeline)
    ClassifierUtils.TestClassifiers(WorksForClassifierPipeline, LivesInClassifierPipeline)
  }

  /** In the scenario the classifiers are learned independently but at the test time we use constrained inference to
    * maintain structural consistency (which would justify the naming "Learning Plus Inference" (L+I).
    */
  def runLPlusI() {
    EntityRelationDataModel.populateWithConll()

    // load all independent models
    ClassifierUtils.LoadClassifier(jarModelPath, PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier)

    // Test using constrained classifiers
    ClassifierUtils.TestClassifiers(PerConstrainedClassifier, OrgConstrainedClassifier, LocConstrainedClassifier,
      WorksFor_PerOrg_ConstrainedClassifier, LivesIn_PerOrg_relationConstrainedClassifier)
  }

  /** here we meanwhile training classifiers, we use global inference, in order to overcome the poor local
    * classifications and yield accurate global classifications.
    */
  def runJointTraining() {
    populateWithConll()
    val testRels = pairs.getTestingInstances.toSet.toList
    val testTokens = tokens.getTestingInstances.toSet.toList

    // load pre-trained independent models
    ClassifierUtils.LoadClassifier(jarModelPath, PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier)

    // joint training
    val jointTrainIteration = 5
    println(s"Joint training $jointTrainIteration iterations. ")
    JointTrain.train[ConllRelation](
      pairs,
      PerConstrainedClassifier :: OrgConstrainedClassifier :: LocConstrainedClassifier ::
        WorksFor_PerOrg_ConstrainedClassifier :: LivesIn_PerOrg_relationConstrainedClassifier :: Nil,
      jointTrainIteration
    )

    // TODO: merge the following two tests
    ClassifierUtils.TestClassifiers((testTokens, PerConstrainedClassifier), (testTokens, OrgConstrainedClassifier),
      (testTokens, LocConstrainedClassifier))

    ClassifierUtils.TestClassifiers(
      (testRels, WorksFor_PerOrg_ConstrainedClassifier),
      (testRels, LivesIn_PerOrg_relationConstrainedClassifier)
    )
  }
}
