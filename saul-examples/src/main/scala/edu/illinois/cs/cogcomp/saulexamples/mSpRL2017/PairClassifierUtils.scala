package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.{ReportHelper, SpRLXmlReader}
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{NlpBaseElement, Phrase, Relation}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval._

import scala.collection.JavaConversions._

/** Created by taher on 2017-02-26.
  */
object PairClassifierUtils {

  def evaluate(
                predicted: List[Relation],
                dataPath: String,
                resultsDir: String,
                resultsFilePrefix: String,
                isTrain: Boolean,
                isTrajector: Boolean
              ): Seq[SpRLEvaluation] = {

    val reader = new SpRLXmlReader(dataPath)
    val actual = if(isTrajector) reader.getTrSpPairsWithArguments() else reader.getLmSpPairsWithArguments()

    val name = if (isTrajector) "TrSp" else "LmSp"
    ReportHelper.reportRelationResults(resultsDir, resultsFilePrefix + s"_$name", actual, predicted, new OverlapComparer, 2)
  }

}

