package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

/** The SRL data model which contains all the entities needed to support the structured problem. */
object SRLDataModel extends DataModel {
  val predicates = node[Constituent]

  val arguments = node[Constituent]

  val relations = node[Relation]

  val sentences = node[TextAnnotation]

  val trees = node[Tree[String]]

  val tokens = node[Constituent]

  val sentencesToRelations = edge(sentences, relations)

  sentencesToRelations.addSensor(SRLSensors.textAnnotationToRelation _)

  val sentencesToTrees = edge(sentences, trees)
  //TODO PARSE_GOLD is only good for training; for testing we need PARSE_STANFORD or PARSE_CHARNIAK
  sentencesToTrees.addSensor(SRLSensors.textAnnotationToTree _)

  val relationsToArguments = edge(relations, arguments)
  relationsToArguments.addSensor(SRLSensors.relToArgument _)

  val relationsToPredicates = edge(relations, predicates)
  relationsToPredicates.addSensor(SRLSensors.relToPredicate _)

  val sentencesToTokens = edge(sentences, tokens)
  sentencesToTokens.addSensor(SRLSensors.textAnnotationToTokens _)

  //TODO This is what I think the properties should look like
  //  val lemma = property(predicate)
  //  lemma.use(SRLSensors.lemmatizer _)
  // SRL Properties

  val predicateLabel = property[Constituent]("p") {
    x: Constituent => x.getLabel
  }
  val posTag = property[Constituent]("pos") {
    x: Constituent => { x.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x).get(0).getLabel }
  }
}