package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation }
import edu.illinois.cs.cogcomp.lbjava.learn.{ SparseNetworkLearner, SparseAveragedPerceptron }
import edu.illinois.cs.cogcomp.saul.classifier.{ Learnable, SparseNetworkLBP }
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlDataModel._

/** Created by Parisa on 12/30/15.
  */
object srlClassifiers {

  //TODO This needs to be overriden by the user; change it to be dynamic
  val parameters = new SparseAveragedPerceptron.Parameters()
  //  parameters.modelDir = new ExamplesConfigurator().getDefaultConfig.getString(ExamplesConfigurator.MODELS_DIR.getFirst)
  object predicateClassifier extends Learnable[Constituent](srlDataModel, parameters) {
    //TODO These are not used during Learner's initialization
    def label: Property[Constituent] = srlDataModel.isPredicateGold
    override def feature = using(posTag, subcategorization, phraseType, headword)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object argumentClassifier extends Learnable[Constituent](srlDataModel, parameters) {
    def label = srlDataModel.isArgumentGold
    override lazy val classifier = new SparseNetworkLBP
  }

  object predicateSenseClassifier extends Learnable[Constituent](srlDataModel, parameters) {
    def label = srlDataModel.predicateSenseGold
    override lazy val classifier = new SparseNetworkLBP
  }

  object argumentTypeLearner extends Learnable[Relation](srlDataModel, parameters) {
    def label = srlDataModel.argumentLabelGold
    import srlDataModel._
    override def feature = using(headwordRelation, syntacticFrameRelation, pathRelation, subcategorizationRelation, phraseTypeRelation, predPosTag, predLemma, linearPosition)
    override lazy val classifier = new SparseNetworkLBP
  }

  object argumentXuIdentifierGivenApredicate extends Learnable[Relation](srlDataModel, parameters) {
    def label = srlDataModel.isArgumentXuGold
    override def feature = using(headwordRelation, syntacticFrameRelation, pathRelation, subcategorizationRelation, phraseTypeRelation, predPosTag, predLemma, linearPosition)
    override lazy val classifier = new SparseNetworkLBP
  }

}

