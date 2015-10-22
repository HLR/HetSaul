package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.lbjava.learn.SparseAveragedPerceptron
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationBasicDataModel._

object classifiers {

  val parameters = new SparseAveragedPerceptron.Parameters()
  parameters.modelDir = "model"

  object orgClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel,parameters) {
    def label: Property[ConllRawToken] = entityType is "Org"
    override def feature = using(word)
  }

  object PersonClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel,parameters) {
    def label: Property[ConllRawToken] = entityType is "Peop"
  }

  object LocClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel,parameters) {
    def label: Property[ConllRawToken] = entityType is "Loc"
  }

  object workForClassifier extends Learnable[ConllRelation](entityRelationBasicDataModel,parameters) {
    override def label: Property[ConllRelation] = relationType is "Work_For"
  }
  object LivesInClassifier extends Learnable[ConllRelation](entityRelationBasicDataModel,parameters) {
    override def label: Property[ConllRelation] = relationType is "Live_In"
  }
}

