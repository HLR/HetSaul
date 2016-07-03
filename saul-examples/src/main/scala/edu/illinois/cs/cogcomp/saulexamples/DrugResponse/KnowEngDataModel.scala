/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.DrugResponse

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.bioSensors._
import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.Classifiers.dResponseClassifier
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics._

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

  val genesGroupedPerPathway = genes().map(x => x.KEGG.map(y => (x.GeneName, y))).flatten.groupBy(_._2).map(x => (x._1, x._2.map(t1 => t1._1)))
  //val genesGroupedPerPathway3 = genes().map(x => x.KEGG.map(y => (x , y))).flatten.groupBy(_._2).map(x => (x._1, x._2.map(t1 => t1._1)))

  //val genesGroupedPerPathway2 = SGroupBy(genes, gene_KEGG, geneName)
  val pathWayGExpression = (pathway: String) => property(patientDrug, ordered = true) {
    pd: PatientDrug =>
      val myPathwayGenes = genesGroupedPerPathway.get(pathway) // ("hsa01040")
      val a = this.patientGene().filter(y => pd.pid == y.sample_ID).filter(x => myPathwayGenes.contains(x.Gene_ID)).map(x => x.gExpression).asInstanceOf[List[Double]]
      a
  }
  // val pathwayNeighbors = genesGroupedPerPathway3.get("hsa01040").foreach(gen => if (((genes(gen)~> -geneGenes) prop PPIBioGrid).equals(1)) {})
  //  val pathwayNeighbors4 = genesGroupedPerPathway3.get("hsa01040").map(
  //      gen =>
  //      (genes(gen)~> -geneGenes).filter(rel=> PPIBioGrid(rel).equals(1))).flatten

  val similarity = property(geneGene) {
    x: GeneGene => x.similarity.doubleValue()
  }
  val PPIBioGrid = property(geneGene) {
    x: GeneGene => x.PPI_BioGRID
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