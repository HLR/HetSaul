package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationBasicDataModel._

object entityRelationClassifiers {

  object orgClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel) {
    def label: Property[ConllRawToken] = entityType is "Org"
    override def feature = using(word)
  }

  object personClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel) {
    def label: Property[ConllRawToken] = entityType is "Peop"
  }

  object locationClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel) {
    def label: Property[ConllRawToken] = entityType is "Loc"
  }

  object worksForClassifier extends Learnable[ConllRelation](entityRelationBasicDataModel) {
    override def label: Property[ConllRelation] = relationType is "Work_For"
  }
  object livesInClassifier extends Learnable[ConllRelation](entityRelationBasicDataModel) {
    override def label: Property[ConllRelation] = relationType is "Live_In"
  }
}

