package edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics._
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel.Classifiers.dResponseClassifier
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel.BioSensors._

import scala.collection.JavaConversions._
/** Created by Parisa on 6/24/15.
  */
object KnowEngDataModel extends DataModel {

  val patients = node[Patient]
  val genes = node[Gene]
  val patientGene = node[PatientGene]
  val patientDrug = node[PatientDrug]
  val geneGene = node[GeneGene]

  val pdPatient = edge(patientDrug, patients)
  val pgPatient = edge(patientGene, patients)
  val pgGenes = edge(patientGene, genes)
  val geneGenes = edge(geneGene, genes)

  pdPatient.addSensor(patientDrugMatchSensor _)
  pgPatient.addSensor(pgPatientMatchSensor _)
  pgGenes.addSensor(pgGeneMatchSensor _)
  geneGenes.addSensor(ggGeneMatchSensor _)

  val age = property(patients) {
    x: Patient => x.age.toDouble
  }
  val gender = property(patients) {
    x: Patient => x.gender
  }
  val ethnicity = property(patients) {
    x: Patient => x.ethnicity
  }
  val geneName = property(genes) {
    x: Gene => x.GeneName
  }

  val gene_GoTerm = property(genes) {
    x: Gene =>
      if (x.GO_term == null)
        List("") else (x.GO_term.toList)
  }
  val gene_KEGG = property(genes) {
    x: Gene =>
      if (x.KEGG == null)
        List("") else x.KEGG.toList
  }
  val gene_motif = property(genes) {
    x: Gene => x.motif_u5_gc.doubleValue()
  }
  val gene_pfam_domain = property(genes) {
    x: Gene => x.pfam_domain.doubleValue()
  }

  val geneExpression = property(patientGene) {
    x: PatientGene => x.singleGeneExp.doubleValue()
  }

  val drugResponse = property(patientDrug) {
    x: PatientDrug => x.response.doubleValue()
  }
  val similarity = property(geneGene) {
    x: GeneGene => x.similarity.doubleValue()
  }
  val textSimilarity = property(geneGene) {
    x: GeneGene => x.STRING_textmining
  }
  val cP1 = property(patientDrug) {
    x: PatientDrug => (patientDrug(x) ~> pdPatient prop age).propValues.toList
  }

  val responsePrediction = property(patientDrug) {
    x: PatientDrug => dResponseClassifier.classifier.realValue(x)
  }
}