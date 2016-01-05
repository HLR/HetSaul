package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation }
import edu.illinois.cs.cogcomp.lbjava.learn.{ SparseAveragedPerceptron, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.classifier.{ Learnable, SparseNetworkLBP }
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLDataModel._

/** Created by Parisa on 12/30/15.
  */
object SRLClassifiersForExperiment {

  //TODO This needs to be overriden by the user; change it to be dynamic
  val parameters = new SparseAveragedPerceptron.Parameters()
  //  parameters.modelDir = new ExamplesConfigurator().getDefaultConfig.getString(ExamplesConfigurator.MODELS_DIR.getFirst)
  object predicateClassifier1 extends Learnable[Constituent](SRLDataModel, parameters) {

    //TODO These are not used during Learner's initialization
    def label: Property[Constituent] = SRLDataModel.isPredicate_Gth
    override def feature = using(posTag, subcategorization, phraseType, headword)
    override lazy val classifier = new SparseNetworkLearner()
  }

  object argumentClassifier1 extends Learnable[Constituent](SRLDataModel, parameters) {
    def label = SRLDataModel.isArgument_Gth
    override lazy val classifier = new SparseNetworkLBP
  }

  object predicateSenseClassifier1 extends Learnable[Constituent](SRLDataModel, parameters) {
    def label = SRLDataModel.predicateSense_Gth
    override lazy val classifier = new SparseNetworkLBP
  }

  object argumentTypeLearner1 extends Learnable[Relation](SRLDataModel, parameters) {
    def label = SRLDataModel.argumentLabel_Gth
    import SRLDataModel._
    override def feature = using(headwordRelation, syntacticFrameRelation, pathRelation, subcategorizationRelation, phraseTypeRelation, predPosTag, predLemma, linearPosition)
    override lazy val classifier = new SparseNetworkLBP
  }

  object argumentXuIdentifierGivenApredicate1 extends Learnable[Relation](SRLDataModel, parameters) {
    def label = SRLDataModel.isArgumentXu_Gth
    override def feature = using(headwordRelation, syntacticFrameRelation, pathRelation, subcategorizationRelation, phraseTypeRelation, predPosTag, predLemma, linearPosition)
    override lazy val classifier = new SparseNetworkLBP
  }

}

