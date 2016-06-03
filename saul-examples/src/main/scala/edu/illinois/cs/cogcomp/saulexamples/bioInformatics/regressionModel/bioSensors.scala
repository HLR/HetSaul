package edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel

import edu.illinois.cs.cogcomp.saulexamples.bioInformatics._

/** Created by Parisa on 1/24/16.
  */
object BioSensors {

  def patientDrugMatchSensor(x: PatientDrug, patient: Patient): Boolean = {
    x.pid == patient.patient_id
  }
  def pgPatientMatchSensor(x: PatientGene, patient: Patient): Boolean = {
    x.sample_ID == patient.patient_id
  }

  def pgGeneMatchSensor(x: PatientGene, gene: Gene): Boolean = {
    x.Gene_ID == gene.GeneID
  }
  def ggGeneMatchSensor(x: GeneGene, gene: Gene): Boolean = {
    x.sink_nodeID == gene.GeneID || x.source_nodeID == gene.GeneID
  }
}
