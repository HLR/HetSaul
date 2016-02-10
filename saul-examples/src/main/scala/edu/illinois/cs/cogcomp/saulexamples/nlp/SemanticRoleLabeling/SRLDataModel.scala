package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.nlp.corpusreaders.CoNLLColumnFormatReader
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors

/** The SRL data model which contains all the entities needed to support the structured problem. */
object SRLDataModel extends DataModel {
  val predicates = node[Constituent]

  val arguments = node[Constituent]

  val relations = node[Relation]

  //val onTheFlyRelationNode= join(predicates, arguments)

  val sentences = node[TextAnnotation]

  val trees = node[Tree[Constituent]]

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
  sentencesToTokens.addSensor(CommonSensors.textAnnotationToTokens _)

  val isPredicate = property(tokens, "p") {
    x: Constituent => x.getLabel.equals("Predicate")
  }
  val predicateSense = property(tokens, "s") {
    x: Constituent => x.getAttribute(CoNLLColumnFormatReader.SenseIdentifer)
  }

  val isArgument = property(tokens, "a") {
    x: Constituent => x.getLabel.equals("Argument")
  }
  val argumentLabel = property(relations, "l") {
    r: Relation => r.getRelationName
  }

  val posTag = property(tokens, "pos") {
    x: Constituent => x.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x).get(0).getLabel
  }

  val address = property(tokens, "add") {
    x: Constituent => x.getTextAnnotation.getCorpusId + ":" + x.getTextAnnotation.getId + ":" + x.getSpan
  }
}