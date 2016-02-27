package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawSentence, ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationDataModel._

object EntityRelationClassifiers {

  /** independent entity classifiers */
  object OrganizationClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Org"
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(word, windowWithin[ConllRawSentence](-2, 2, List(pos)), phrase, containsSubPhraseMent,
      containsSubPhraseIng, wordLen, containsInPersonList, containsInCityList)*/
  }

  object PersonClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Peop"
    override def feature = using(word, windowWithin[ConllRawSentence](-2, 2, List(pos)), phrase, containsSubPhraseMent,
      containsSubPhraseIng, wordLen) // , containsInPersonList, , containsInCityList)*/
    override lazy val classifier = new SparseNetworkLearner()
  }

  object LocationClassifier extends Learnable[ConllRawToken](EntityRelationDataModel) {
    def label: Property[ConllRawToken] = entityType is "Loc"
    override def feature = using(word, windowWithin[ConllRawSentence](-2, 2, List(pos)), phrase, containsSubPhraseMent,
      containsSubPhraseIng, wordLen) //, containsInPersonList, containsInCityList)*/
    override lazy val classifier = new SparseNetworkLearner()
  }

  /** independent relation classifiers */
  object WorksForClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    def label: Property[ConllRelation] = relationType is "Work_For"
    override def feature = using(relFeature, relPos)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object LivesInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    def label: Property[ConllRelation] = relationType is "Live_In"
    override def feature = using(relFeature, relPos)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object OrgBasedInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "OrgBased_In"
    override lazy val classifier = new SparseNetworkLearner()
  }

  object LocatedInClassifier extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Located_In"
    override lazy val classifier = new SparseNetworkLearner()
  }

  /** relation pipeline classifiers */
  object WorkForClassifierPipeline extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Work_For"
    override def feature = using(relFeature, relPos, entityPrediction)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object LivesInClassifierPipeline extends Learnable[ConllRelation](EntityRelationDataModel) {
    override def label: Property[ConllRelation] = relationType is "Live_In"
    override def feature = using(relFeature, relPos, entityPrediction)
    override lazy val classifier = new SparseNetworkLearner()
  }

  // methods for saving the learned classifiers
  def saveEntityModels() = {
    PersonClassifier.save()
    OrganizationClassifier.save()
    LocationClassifier.save()
  }

  def saveIndependentRelationModels() = {
    WorksForClassifier.save()
    LivesInClassifier.save()
    LocatedInClassifier.save()
    OrgBasedInClassifier.save()
  }

  def savePipelineRelationModels() = {
    WorkForClassifierPipeline.save()
    LivesInClassifierPipeline.save()
  }

  // Loads learned models from the "saul-conll-er-tagger-models" jar package
  def loadModel[T <: AnyRef](x: Learnable[T]): Unit = {
    val jarModelPath = "edu/illinois/cs/cogcomp/saulexamples/nlp/EntityRelationMention/models/"
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
}

