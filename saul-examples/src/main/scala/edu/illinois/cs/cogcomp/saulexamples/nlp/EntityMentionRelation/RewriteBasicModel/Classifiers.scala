package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.Attribute
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationDataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationBasicDataModel._

object classifiers {

  object orgClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel) {
    def label: Attribute[ConllRawToken] = entityType is "Org"
  }

  object PersonClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel) {
    def label: Attribute[ConllRawToken] = entityType is "Peop"
  }

  object LocClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel) {
    def label: Attribute[ConllRawToken] = entityType is "Loc"
  }

  object workForClassifier extends Learnable[ConllRelation](entityRelationBasicDataModel) {
    override def label: Attribute[ConllRelation] = relationType is "Work_For"
  }
  object LivesInClassifier extends Learnable[ConllRelation](entityRelationDataModel) {
    override def label: Attribute[ConllRelation] = relationType is "Live_In"
  }

}

