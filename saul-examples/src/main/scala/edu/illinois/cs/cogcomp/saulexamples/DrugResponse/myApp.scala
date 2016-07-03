/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.DrugResponse

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saul.datamodel.node.Path
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.Classifiers.DrugResponseRegressor
import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.KnowEngDataModel._
import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.Queries._
import edu.illinois.cs.cogcomp.saulexamples.bioInformatics._

import scala.collection.JavaConversions._
/** Created by Parisa on 6/25/15.
  */
object myApp extends Logging {

  def main(args: Array[String]): Unit = {

    val patients_data = new Sample_Reader("./data/biology/individual_samples.txt").patientCollection
    val patient_drug_data = new drugExampleReader().pdReader("./data/biology/auc_response.txt").filter(x => x.drugId == "D_0")
    val GCollection = new Edges("./data/biology/edgesGG.txt").geneCollection
    val patient_gene_data = new drugExampleReader().pgReader("./data/biology/gene2med_probe_expr.txt").filter((x => GCollection.exists(y => (y.GeneID.equals(x.Gene_ID)))))
    val GGCollection = new Edges("./data/biology/edgesGG.txt").edgeCollection

    logger.info("Statistics about read data")
    logger.info(s"Number of patients: ${patients_data.size}")
    logger.info(s"Number of patient drug records: ${patient_drug_data.size}")
    logger.info(s"Number of Genes: ${GCollection.size} ")
    logger.info(s"Number of patient_gene records: ${patient_gene_data.size} ")
    logger.info(s"Number of edges: ${GGCollection.size}")

    patients.populate(patients_data.slice(0, 5))
    patients.populate(patients_data.slice(6, 10), train = false)

    patientDrug.populate(patient_drug_data.slice(0, 10))
    patientDrug.populate(patient_drug_data.slice(11, 20), train = false)

    patientGene.populate(patient_gene_data.slice(0, 10))
    patientGene.populate(patient_gene_data.slice(0, 10), train = false)

    genes.populate(GCollection.slice(0, 5))
    genes.populate(GCollection.slice(0, 5), train = false)

    geneGene.populate(GGCollection.slice(0, 10))
    geneGene.populate(GGCollection.slice(0, 10), train = false)

    //first find all distinct pathways from the list of pathways that are in the list of pathways for each gene
    //then define a new regressor per pathway
    val myLearners = (genes() prop gene_KEGG).flatten.toList.distinct.map(pathwayX => new DrugResponseRegressor(pathwayX))

    ClassifierUtils.TestClassifiers(myLearners)
    myLearners.foreach(_.testContinuous())

    //myLearners.map(x => x.test()) //.SortwithAccuracy()

    val genesGroupedPerPathway2 = SGroupBy(genes, gene_KEGG, geneName)
    // genes SGroupBy gene_KEGG Select geneName

    val genesGroupedPerPathway = genes().map(x => x.KEGG.map(y => (x.GeneName, y))).flatten.groupBy(_._2).map(x => (x._1, x._2.map(t1 => t1._1)))

    patientDrug().filter(x => drugResponse(x) > 12)

    (patients() ~> -pgPatient ~> pgGenes) prop gene_KEGG

    genes(genes().head) ~> -geneGenes prop textSimilarity

    genes(Path.findPath(genes().head, genes, genes().head).asInstanceOf[Seq[Gene]]) prop gene_GoTerm

    // dResponseClassifier.learn(1)

    //dResponseClassifier.testContinuos(patient_drug_data)
    //DrugResponseRegressor.learn(1)
    //DrugResponseRegressor.testContinuos(patientDrug.getTrainingInstances)
    logger.info("finished!")
  }

}
