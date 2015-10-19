package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Constituent, Relation, TextAnnotation }
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

/** The SRL data model which contains all the entities needed to support the structured problem.
  *
  * @author Christos Christodoulopoulos
  */
object SRLDataModel extends DataModel {
  val predicate = node[Constituent]
  val argument = node[Constituent]
  val relation = node[Relation]
  val textAnnotation = node[TextAnnotation]
  val tree = node[Tree[String]]
  val token = node[Constituent]

  val taToRelation = edge(textAnnotation, relation)
  taToRelation.addSensor(SRLSensors.textAnnotationToRelation _)
  val taToTree = edge(textAnnotation, tree)
  //TODO PARSE_GOLD is only good for training; for testing we need PARSE_STANFORD or PARSE_CHARNIAK
  //taToTree.addSensor(SRLSensors.textAnnotationToTree(_, ViewNames.PARSE_GOLD))

  val relToArg = edge(relation, argument)
  relToArg.addSensor(SRLSensors.relToArgument _)
  val relToPred = edge(relation, predicate)
  relToPred.addSensor(SRLSensors.relToPredicate _)

  val taToConst = edge(textAnnotation, token)

  //TODO This is what I think the properties should look like
  //  val lemma = property(predicate)
  //  lemma.use(SRLSensors.lemmatizer _)
}