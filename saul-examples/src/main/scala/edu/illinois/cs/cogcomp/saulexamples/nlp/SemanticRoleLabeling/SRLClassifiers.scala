package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Relation, Constituent }
import edu.illinois.cs.cogcomp.lbjava.learn.SparseAveragedPerceptron
import edu.illinois.cs.cogcomp.saul.classifier.{ SparseNetworkLBP, Learnable }
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property

object SRLClassifiers {
  //TODO This needs to be overriden by the user; change it to be dynamic
  val parameters = new SparseAveragedPerceptron.Parameters()
  //  parameters.modelDir = new ExamplesConfigurator().getDefaultConfig.getString(ExamplesConfigurator.MODELS_DIR.getFirst)
  object predicateClassifier extends Learnable[Constituent](SRLDataModel, parameters) {
    //TODO These are not used during Learner's initialization
    def label: Property[Constituent] = SRLDataModel.isPredicate
    override val classifier = new SparseNetworkLBP()
  }

  object argumentClassifier extends Learnable[Constituent](SRLDataModel, parameters) {
    def label = SRLDataModel.isArgument
    override val classifier = new SparseNetworkLBP()
  }

  object predicateSenseClassifier extends Learnable[Constituent](SRLDataModel, parameters) {
    def label = SRLDataModel.predicateSense
    override val classifier = new SparseNetworkLBP()
  }

  object relationClassifier extends Learnable[Relation](SRLDataModel, parameters) {
    def label = SRLDataModel.argumentLabel
    override val classifier = new SparseNetworkLBP()
  }

}
