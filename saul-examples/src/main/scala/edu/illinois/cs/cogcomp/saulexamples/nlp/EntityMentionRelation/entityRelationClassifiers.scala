package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner
import edu.illinois.cs.cogcomp.saul.classifier.{ ConstrainedClassifier, Learnable }
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationBasicDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationConstraints._

object entityRelationClassifiers {

  import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationDataModel._
  // TODO : Write the type conversion
  //  val orgFeature = List(pos,entityType)

  object orgClassifier extends Learnable[ConllRawToken](tokens) {
    def label: Property[ConllRawToken] = entityType is "Org"
    override lazy val classifier = new SparseNetworkLearner()
    //TODO add test units with explicit feature definition and remove these lines.
    //override def feature = using(
    //word,phrase,containsSubPhraseMent,containsSubPhraseIng,
    // containsInPersonList,wordLen,containsInCityList
    // )
  }

  object PersonClassifier extends Learnable[ConllRawToken](tokens) {
    def label: Property[ConllRawToken] = entityType is "Peop"
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(
      posWindow,
      word, phrase, containsSubPhraseMent, containsSubPhraseIng,
      containsInPersonList, wordLen, containsInCityList
    )
  }

  object LocClassifier extends Learnable[ConllRawToken](tokens) {
    def label: Property[ConllRawToken] = entityType is "Loc"
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(
      posWindow,
      word, phrase, containsSubPhraseMent, containsSubPhraseIng,
      containsInPersonList, wordLen, containsInCityList
    )
  }

  val ePipe = property[ConllRelation](pairedRelations, "e1pipe") {
    rel: ConllRelation =>
      "e1-org: " + orgClassifier.discreteValue(rel.e1) ::
        "e1-per: " + PersonClassifier.discreteValue(rel.e1) ::
        "e1-loc: " + LocClassifier.discreteValue(rel.e1) ::
        "e2-org: " + orgClassifier.discreteValue(rel.e1) ::
        "e2-per: " + PersonClassifier.discreteValue(rel.e1) ::
        "e2-loc: " + LocClassifier.discreteValue(rel.e1) ::
        Nil
  }

  object workForClassifier extends Learnable[ConllRelation](pairedRelations) {
    override def label: Property[ConllRelation] = relationType is "Work_For"
    override def feature = using(
      relFeature, relPos
    )
    override lazy val classifier = new SparseNetworkLearner()
  }

  object workForClassifierPipe extends Learnable[ConllRelation](pairedRelations) {
    override def label: Property[ConllRelation] = relationType is "Work_For"
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(
      relFeature, relPos, ePipe
    )
  }

  object LivesInClassifier extends Learnable[ConllRelation](pairedRelations) {
    override def label: Property[ConllRelation] = relationType is "Live_In"
    override def feature = using(
      relFeature, relPos
    )
    override lazy val classifier = new SparseNetworkLearner()
  }

  object LivesInClassifierPipe extends Learnable[ConllRelation](pairedRelations) {
    override def label: Property[ConllRelation] = relationType is "Live_In"
    override def feature = using(
      relFeature, relPos, ePipe
    )
    override lazy val classifier = new SparseNetworkLearner()
  }

  object org_baseClassifier extends Learnable[ConllRelation](pairedRelations) {
    override def label: Property[ConllRelation] = relationType is "OrgBased_In"
    override lazy val classifier = new SparseNetworkLearner()
  }
  object locatedInClassifier extends Learnable[ConllRelation](pairedRelations) {
    override def label: Property[ConllRelation] = relationType is "Located_In"
    override lazy val classifier = new SparseNetworkLearner()
  }

  object orgConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](orgClassifier) {
    override val pathToHead = RelationToOrg
    def subjectTo = Per_Org
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId1
  }

  object PerConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](PersonClassifier) {
    override val pathToHead = RelationToPer
    def subjectTo = Per_Org
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
  }

  object LocConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](LocClassifier) {
    override val pathToHead = RelationToLoc
    def subjectTo = Per_Org
    //TODO add test unit for this filter
    //    override def filter(t: ConllRawToken,h:ConllRelation): Boolean = t.wordId==h.wordId2
  }

  object P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](workForClassifier) {
    def subjectTo = Per_Org
  }

  object LiveIn_P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](LivesInClassifier) {
    def subjectTo = Per_Org
  }
}

