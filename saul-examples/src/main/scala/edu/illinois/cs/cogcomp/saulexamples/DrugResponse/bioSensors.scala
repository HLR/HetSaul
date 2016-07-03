/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.DrugResponse

import edu.illinois.cs.cogcomp.saulexamples.bioInformatics._

/** Created by Parisa on 1/24/16.
  */
object bioSensors {

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
