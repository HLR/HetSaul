package edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel

import edu.illinois.cs.cogcomp.saul.datamodel.node.Path
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel.Classifiers.dResponseClassifier
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.regressionModel.KnowEngDataModel._
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.{ Gene, Edges, Sample_Reader, drugExampleReader }

import scala.collection.JavaConversions._
/** Created by Parisa on 6/25/15.
  */
object myApp {

  def main(args: Array[String]): Unit = {

    val patients_data = new Sample_Reader("./data/biology/individual_samples.txt").patientCollection.slice(0, 5)
    val patient_drug_data = new drugExampleReader().pdReader("./data/biology/auc_response.txt").filter(x => x.drugId == "D_0").slice(0, 5)
    val GCollection = new Edges("./data/biology/edgesGG.txt").geneCollection.slice(0, 10)
    val patient_gene_data = new drugExampleReader().pgReader("./data/biology/gene2med_probe_expr.txt").filter((x => GCollection.exists(y => (y.GeneID.equals(x.Gene_ID))))).slice(0, 5)
    val GGCollection = new Edges("./data/biology/edgesGG.txt").edgeCollection.slice(0, 20)

    patients.populate(patients_data)
    patientDrug.populate(patient_drug_data)
    patientGene.populate(patient_gene_data)
    genes.populate(GCollection)
    geneGene.populate(GGCollection)

    patientDrug().filter(x => drugResponse(x) > 12)

    (patients() ~> -pgPatient ~> pgGenes) prop gene_KEGG

    genes(genes().head) ~> -geneGenes prop textSimilarity

    genes(Path.findPath(genes().head, genes, genes().head).asInstanceOf[Seq[Gene]]) prop gene_GoTerm

    dResponseClassifier.learn(1)

    //dResponseClassifier.testContinuos(patient_drug_data)

  }
}
