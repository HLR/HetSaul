package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saul.classifier.{ SparseNetworkLBP, Learnable }
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationBasicDataModel._

object entityRelationClassifiers {

  object orgClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel) {
    def label: Property[ConllRawToken] = entityType is "Org"
    override def feature = using(word)
    override val classifier = new SparseNetworkLBP()
  }

  object personClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel) {
    def label: Property[ConllRawToken] = entityType is "Peop"
    override val classifier = new SparseNetworkLBP()
  }

  object locationClassifier extends Learnable[ConllRawToken](entityRelationBasicDataModel) {
    def label: Property[ConllRawToken] = entityType is "Loc"
    override val classifier = new SparseNetworkLBP()
  }

  object worksForClassifier extends Learnable[ConllRelation](entityRelationBasicDataModel) {
    override def label: Property[ConllRelation] = relationType is "Work_For"
    override val classifier = new SparseNetworkLBP()
  }
  object livesInClassifier extends Learnable[ConllRelation](entityRelationBasicDataModel) {
    override def label: Property[ConllRelation] = relationType is "Live_In"
    override val classifier = new SparseNetworkLBP()
  }
}

