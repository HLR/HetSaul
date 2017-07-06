package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.{CandidateGenerator, ReportHelper, SpRLXmlReader}
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Phrase, Relation}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval._

import scala.collection.JavaConversions._

/** Created by taher on 2017-02-26.
  */
object TripletClassifierUtils {

  def test(
            dataPath: String,
            resultsDir: String,
            resultsFilePrefix: String,
            isTrain: Boolean,
            trClassifier: (Relation) => String,
            spClassifier: (Phrase) => String,
            lmClassifier: (Relation) => String
          ): Seq[SpRLEvaluation] = {

    val predicted: List[Relation] = predict(trClassifier, spClassifier, lmClassifier, isTrain)
    val actual = new SpRLXmlReader(dataPath).getTripletsWithArguments()

    ReportHelper.reportRelationResults(resultsDir, resultsFilePrefix + "_triplet", actual, predicted, new OverlapComparer, 3)
  }

  def predict(
               trClassifier: (Relation) => String,
               spClassifier: (Phrase) => String,
               lmClassifier: (Relation) => String,
               isTrain: Boolean = false
             ): List[Relation] = {
    CandidateGenerator.generateTripletCandidates(trClassifier, spClassifier, lmClassifier, isTrain)
  }

}

