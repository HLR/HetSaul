package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Constituent, Relation, TextAnnotation}
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLConfigurator
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SpRL2013Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.SpRL2015Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLSensors._
import org.apache.commons.lang.NotImplementedException

/**
  * Created by taher on 7/28/16.
  */
object SpRLDataModel extends DataModel{
  val sentences = node[TextAnnotation]
  val tokens = node[Constituent]
  val relations = node[Relation]

  val sentencesToTokens = edge(sentences, tokens)
  val sentencesToRelations = edge(sentences, relations)
  val relationsToTokens = edge(relations, tokens)

  sentencesToRelations.addSensor(textAnnotationToRelation _)
  sentencesToTokens.addSensor(textAnnotationToTokens _)
  relationsToTokens.addSensor(relationToToken _)

  // Classification labels
  val isSpatialIndicator = property(tokens, "sp") {
    x: Constituent => x.getLabel.equals("SpatialIndicator")
  }
  val isLandmark = property(tokens, "lm") {
    x: Constituent => x.getLabel.equals("Landmark")
  }
  val isTrajector = property(tokens, "tr") {
    x: Constituent => x.getLabel.equals("Trajector")
  }

  // features
  val posTag = property(tokens, "pos"){
    x: Constituent => getPOS(x)
  }
  //TODO: add more features


}
