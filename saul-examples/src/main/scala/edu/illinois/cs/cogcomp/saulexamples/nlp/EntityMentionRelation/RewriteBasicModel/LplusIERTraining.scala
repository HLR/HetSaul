package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel
import IndependentERTraining._
import edu.illinois.cs.cogcomp.saul.classifier.trainingParadigms.forgetAll
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationClassifiers._

/** Created by Parisa on 12/7/15.
  */
object LplusIERTraining {

  populate_ER_graph
  forgetAll(personClassifier, orgClassifier, locationClassifier)

}
