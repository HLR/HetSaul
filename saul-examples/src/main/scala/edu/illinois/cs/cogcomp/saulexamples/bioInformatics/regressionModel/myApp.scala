package edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel

import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel.Classifiers.dResponseClassifier
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel.knowEngDataModel._
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.{ Edges, Sample_Reader, drugExampleReader }

import scala.collection.JavaConversions._
/** Created by Parisa on 6/25/15.
  */
object myApp {

  def main(args: Array[String]): Unit = {

    val patients_data = new Sample_Reader("./data/biology/individual_samples.txt").patientCollection.slice(0, 10)
    val patient_drug_data = new drugExampleReader().pdReader("./data/biology/auc_response.txt").filter(x => x.drugId == "D_0").slice(0, 10)
    val GCollection = new Edges("./data/biology/edgesGG.txt").geneCollection
    val patient_gene_data = new drugExampleReader().pgReader("./data/biology/gene2med_probe_expr.txt").filter((x => GCollection.exists(y => (y.GeneID.equals(x.Gene_ID))))).slice(0, 10)
    val GGCollection = new Edges("./data/biology/edgesGG.txt").edgeCollection.slice(0, 30)

    patients.populate(patients_data)
    patientDrug.populate(patient_drug_data)
    patientGene.populate(patient_gene_data)
    genes.populate(GCollection)
    geneGene.populate(GGCollection)

    patients() ~> -pdPatient
    // patients() ~>

    //    Si.genes.names.filter(logical expression on gene.value).foreach.getNeighborhood.aggregate().zipidex()
    //    %Si.genses.names.filter(logical expression on gene.value).getgenes.findBestmaching(a list of pathways,k-best)
    //    %G.foreach.getall(logical expresion).sum.filter(logical ).zipwithindex

    dResponseClassifier.learn(1)
    print("finished learning.")
    dResponseClassifier.testContinuos(patient_drug_data)
  }
}
