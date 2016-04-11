package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.lbjava.learn.{ SparsePerceptron, StochasticGradientDescent, SupportVectorMachine, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawSentence, ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel._

object EntityRelationClassifiers {

  /** independent entity classifiers */
  object OrganizationClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Org"
    override lazy val classifier = new SparsePerceptron()
    override def feature = using(word, windowWithin[ConllRawSentence](-2, 2, List(pos)), phrase,
      containsSubPhraseMent, containsSubPhraseIng, wordLen)
    // The gazetteer properties are temporarily removed: containsInPersonList, containsInCityList
  }

  object PersonClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Peop"
    override def feature = using(word, windowWithin[ConllRawSentence](-2, 2, List(pos)), phrase,
      containsSubPhraseMent, containsSubPhraseIng, wordLen)
    override lazy val classifier = new SparsePerceptron()
    // The gazetteer properties are temporarily removed: containsInPersonList, containsInCityList
  }

  object LocationClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Loc"
    override def feature = using(word, windowWithin[ConllRawSentence](-2, 2, List(pos)), phrase, containsSubPhraseMent,
      containsSubPhraseIng, wordLen)
    override lazy val classifier = new SparsePerceptron()
    // The gazetteer properties are temporarily removed: containsInPersonList, containsInCityList
  }

  /** independent relation classifiers */
  object WorksForClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    def label: Property[ConllRelation] = relationType is "Work_For"
    override def feature = using(relFeature, relPos)
    override lazy val classifier = new SparsePerceptron()
  }

  object LivesInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    def label: Property[ConllRelation] = relationType is "Live_In"
    override def feature = using(relFeature, relPos)
    override lazy val classifier = new SparsePerceptron()
  }

  object OrgBasedInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "OrgBased_In"
    override lazy val classifier = new SparsePerceptron()
  }

  object LocatedInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Located_In"
    override lazy val classifier = new SparsePerceptron()
  }

  /** relation pipeline classifiers */
  object WorksForClassifierPipeline extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Work_For"
    override def feature = using(relFeature, relPos, entityPrediction)
    override lazy val classifier = new SparsePerceptron()
  }

  object LivesInClassifierPipeline extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Live_In"
    override def feature = using(relFeature, relPos, entityPrediction)
    override lazy val classifier = new SparsePerceptron()
  }

  def saveEntityModels() = {
    PersonClassifier.save()
    OrganizationClassifier.save()
    LocationClassifier.save()
  }

  def testEntityModels() = {
    println("Testing independent entity models: ")
    println("==============================================")
    println("Person Classifier Evaluation")
    PersonClassifier.test()
    println("==============================================")
    println("Organization Classifier Evaluation")
    OrganizationClassifier.test()
    println("==============================================")
    println("Location Classifier Evaluation")
    LocationClassifier.test()
    println("==============================================")
  }

  def saveIndependentRelationModels() = {
    WorksForClassifier.save()
    LivesInClassifier.save()
    LocatedInClassifier.save()
    OrgBasedInClassifier.save()
  }

  def testIndependentRelationModels() = {
    println("Testing independent relation models: ")
    println("==============================================")
    println("WorksFor Classifier Evaluation")
    WorksForClassifier.test()
    println("==============================================")
    println("LivesIn Classifier Evaluation")
    LivesInClassifier.test()
    println("==============================================")
    println("LocatedIn Classifier Evaluation")
    LocatedInClassifier.test()
    println("==============================================")
    println("OrgBasedIn Classifier Evaluation")
    OrgBasedInClassifier.test()
  }

  def savePipelineRelationModels() = {
    WorksForClassifierPipeline.save()
    LivesInClassifierPipeline.save()
  }

  def testPipelineModels() = {
    println("Testing pipeline relation models: ")
    println("==============================================")
    println("WorksFor Pipeline Classifier Evaluation")
    WorksForClassifierPipeline.test()
    println("==============================================")
    println("LivesIn Pipeline Classifier Evaluation")
    LivesInClassifierPipeline.test()
    println("==============================================")
  }

  // Loads learned models from the "saul-conll-er-tagger-models" jar package
  def loadModel[T <: AnyRef](x: Learnable[T]): Unit = {
    val jarModelPath = "edu/illinois/cs/cogcomp/saulexamples/nlp/EntityRelation/models/"
    val prefix = jarModelPath + x.getClassNameForClassifier
    x.load(prefix + ".lc", prefix + ".lex")
  }

  def loadIndependentEntityModels(): Unit = {
    loadModel[ConllRawToken](PersonClassifier)
    loadModel[ConllRawToken](OrganizationClassifier)
    loadModel[ConllRawToken](LocationClassifier)
  }

  def loadIndependentRelationModels(): Unit = {
    loadModel[ConllRelation](WorksForClassifier)
    loadModel[ConllRelation](LivesInClassifier)
    loadModel[ConllRelation](LocatedInClassifier)
    loadModel[ConllRelation](OrgBasedInClassifier)
  }

  def loadPipelineRelationModels(): Unit = {
    loadModel[ConllRelation](WorksForClassifierPipeline)
    loadModel[ConllRelation](LivesInClassifierPipeline)
  }
}

