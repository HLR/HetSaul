package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Constituent, Relation}
import edu.illinois.cs.cogcomp.lbjava.learn.SparseAveragedPerceptron
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property

object SRLClassifiers {
  //TODO This needs to be overriden by the user; change it to be dynamic
  val parameters = new SparseAveragedPerceptron.Parameters()
  //  parameters.modelDir = new ExamplesConfigurator().getDefaultConfig.getString(ExamplesConfigurator.MODELS_DIR.getFirst)
  object predicateClassifier extends Learnable[Constituent](SRLDataModel, parameters) {

    //TODO These are not used during Learner's initialization
    def label: Property[Constituent] = SRLDataModel.isPredicate
    override def algorithm = "SparseNetwork"
  }

  object argumentClassifier extends Learnable[Constituent](SRLDataModel, parameters) {
    def label = SRLDataModel.isArgument
  }

  object predicateSenseClassifier extends Learnable[Constituent](SRLDataModel, parameters) {
    def label = SRLDataModel.predicateSense
  }

  object relationClassifier extends Learnable[Relation](SRLDataModel, parameters) {
    def label = SRLDataModel.argumentLabel
    import SRLDataModel._
    override def feature= using(subcategorizationRelation, phraseTypeRelation, headwordRelation,syntacticFrameRelation,pathRelation)
  }

}
