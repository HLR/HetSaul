$(document).ready(function(){

    installGraphsTabClick();
    installLeftPanelClick();

    $("#hoverBar").click(function() {
        $('.page-header').toggle();
    });
    $("#errors").hide();

    setupExamples();

})

var setupExamples = function(){
        $("#ToyExample").click(function(){
            deleteAllFiles();
            var content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","import logging.Logger.{ error, info }", "","object $$$$$$ extends DataModel {","","    val firstNames = node[String]","    val lastNames = node[String]","    val name = edge(firstNames,lastNames)","    val prefix = property(firstNames,\"prefix\")((s: String) => s.charAt(1).toString)","    val prefix2 = property(firstNames,\"prefix\")((s: String) => s.charAt(0).toString)","","    def main(args : Array[String]): Unit ={","        firstNames.populate(Seq(\"Dave\",\"John\",\"Mark\",\"Michael\"))","        lastNames.populate(Seq(\"Dell\",\"Jacobs\",\"Maron\",\"Mario\"))","        name.populateWith(_.charAt(0) == _.charAt(0))","    }","}"];
            newFile(content);
        });

        $("#KnowEng").click(function(){
            deleteAllFiles();
            var content = ["/** This software is released under the University of Illinois/Research and Academic Use License. See","  * the LICENSE file in the root folder for details. Copyright (c) 2016","  *","  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign","  * http://cogcomp.cs.illinois.edu/","  */","package edu.illinois.cs.cogcomp.saulexamples.DrugResponse","","import edu.illinois.cs.cogcomp.saulexamples.bioInformatics._","","/** Created by Parisa on 1/24/16.","  */","object bioSensors {","","  def patientDrugMatchSensor(x: PatientDrug, patient: Patient): Boolean = {","    x.pid == patient.patient_id","  }","  def pgPatientMatchSensor(x: PatientGene, patient: Patient): Boolean = {","    x.sample_ID == patient.patient_id","  }","","  def pgGeneMatchSensor(x: PatientGene, gene: Gene): Boolean = {","    x.Gene_ID == gene.GeneID","  }","  def ggGeneMatchSensor(x: GeneGene, gene: Gene): Boolean = {","    x.sink_nodeID == gene.GeneID || x.source_nodeID == gene.GeneID","  }","}"];
            newFileWithFilename("bioSensors",content);
            content = ["/** This software is released under the University of Illinois/Research and Academic Use License. See","  * the LICENSE file in the root folder for details. Copyright (c) 2016","  *","  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign","  * http://cogcomp.cs.illinois.edu/","  */","package edu.illinois.cs.cogcomp.saulexamples.DrugResponse","","import edu.illinois.cs.cogcomp.lbjava.learn.StochasticGradientDescent","import edu.illinois.cs.cogcomp.saul.classifier.Learnable","import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.KnowEngDataModel._","import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.PatientDrug","/** Created by Parisa on 6/25/15.","  */","object Classifiers {","  object dResponseClassifier extends Learnable[PatientDrug](patientDrug) {","    def label = drugResponse","    override def feature = using(cP1)","    override lazy val classifier = new StochasticGradientDescent","  }","  class DrugResponseRegressor(pathway: String) extends Learnable[PatientDrug](patientDrug) {","    def label = drugResponse","    override def feature = using(pathWayGExpression(pathway))","    override lazy val classifier = new StochasticGradientDescent","  }","}",""];
            newFileWithFilename("Classifiers",content);
            content = ["/** This software is released under the University of Illinois/Research and Academic Use License. See","  * the LICENSE file in the root folder for details. Copyright (c) 2016","  *","  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign","  * http://cogcomp.cs.illinois.edu/","  */","package edu.illinois.cs.cogcomp.saulexamples.DrugResponse","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.bioSensors._","import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.Classifiers.dResponseClassifier","import edu.illinois.cs.cogcomp.saulexamples.bioInformatics._","","import scala.collection.JavaConversions._","","/** Created by Parisa on 6/24/15.","  */","object KnowEngDataModel extends DataModel {","","  val patients = node[Patient]","  val genes = node[Gene]","  val patientGene = node[PatientGene]","  val patientDrug = node[PatientDrug]","  val geneGene = node[GeneGene]","","  val pdPatient = edge(patientDrug, patients)","  val pgPatient = edge(patientGene, patients)","  val pgGenes = edge(patientGene, genes)","  val geneGenes = edge(geneGene, genes)","","  pdPatient.addSensor(patientDrugMatchSensor _)","  pgPatient.addSensor(pgPatientMatchSensor _)","  pgGenes.addSensor(pgGeneMatchSensor _)","  geneGenes.addSensor(ggGeneMatchSensor _)","","  val age = property(patients) {","    x: Patient => x.age.toDouble","  }","","  val gender = property(patients) {","    x: Patient => x.gender","  }","","  val ethnicity = property(patients) {","    x: Patient => x.ethnicity","  }","","  val geneName = property(genes) {","    x: Gene => x.GeneName","  }","","  val gene_GoTerm = property(genes) {","    x: Gene =>","      if (x.GO_term == null)","        List(\"\") else (x.GO_term.toList)","  }","","  val gene_KEGG = property(genes) {","    x: Gene =>","      if (x.KEGG == null)","        List(\"\") else x.KEGG.toList","  }","","  val gene_motif = property(genes) {","    x: Gene => x.motif_u5_gc.doubleValue()","  }","","  val gene_pfam_domain = property(genes) {","    x: Gene => x.pfam_domain.doubleValue()","  }","","  val geneExpression = property(patientGene) {","    x: PatientGene => x.singleGeneExp.doubleValue()","  }","","  val drugResponse = property(patientDrug) {","    x: PatientDrug => x.response.doubleValue()","  }","","  val genesGroupedPerPathway = genes().map(x => x.KEGG.map(y => (x.GeneName, y))).flatten.groupBy(_._2).map(x => (x._1, x._2.map(t1 => t1._1)))","  //val genesGroupedPerPathway3 = genes().map(x => x.KEGG.map(y => (x , y))).flatten.groupBy(_._2).map(x => (x._1, x._2.map(t1 => t1._1)))","","  //val genesGroupedPerPathway2 = SGroupBy(genes, gene_KEGG, geneName)","  val pathWayGExpression = (pathway: String) => property(patientDrug, ordered = true) {","    pd: PatientDrug =>","      val myPathwayGenes = genesGroupedPerPathway.get(pathway) // (\"hsa01040\")","      val a = this.patientGene().filter(y => pd.pid == y.sample_ID).filter(x => myPathwayGenes.contains(x.Gene_ID)).map(x => x.gExpression).asInstanceOf[List[Double]]","      a","  }","  // val pathwayNeighbors = genesGroupedPerPathway3.get(\"hsa01040\").foreach(gen => if (((genes(gen)~> -geneGenes) prop PPIBioGrid).equals(1)) {})","  //  val pathwayNeighbors4 = genesGroupedPerPathway3.get(\"hsa01040\").map(","  //      gen =>","  //      (genes(gen)~> -geneGenes).filter(rel=> PPIBioGrid(rel).equals(1))).flatten","","  val similarity = property(geneGene) {","    x: GeneGene => x.similarity.doubleValue()","  }","  val PPIBioGrid = property(geneGene) {","    x: GeneGene => x.PPI_BioGRID","  }","  val textSimilarity = property(geneGene) {","    x: GeneGene => x.STRING_textmining","  }","  val cP1 = property(patientDrug) {","    x: PatientDrug => (patientDrug(x) ~> pdPatient prop age).propValues.toList","  }","","  val responsePrediction = property(patientDrug) {","    x: PatientDrug => dResponseClassifier.classifier.realValue(x)","  }","}"];
            newFileWithFilename("knowEngDataModel",content);
            content = ["/** This software is released under the University of Illinois/Research and Academic Use License. See","  * the LICENSE file in the root folder for details. Copyright (c) 2016","  *","  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign","  * http://cogcomp.cs.illinois.edu/","  */","package edu.illinois.cs.cogcomp.saulexamples.DrugResponse","","import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils","import edu.illinois.cs.cogcomp.saul.datamodel.node.Path","import edu.illinois.cs.cogcomp.saul.util.Logging","import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.Classifiers.DrugResponseRegressor","import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.KnowEngDataModel._","import edu.illinois.cs.cogcomp.saulexamples.DrugResponse.Queries._","import edu.illinois.cs.cogcomp.saulexamples.bioInformatics._","","import scala.collection.JavaConversions._","/** Created by Parisa on 6/25/15.","  */","object myApp extends Logging {","","  def main(args: Array[String]): Unit = {","","    val patients_data = new Sample_Reader(\"./data/biology/individual_samples.txt\").patientCollection","    val patient_drug_data = new drugExampleReader().pdReader(\"./data/biology/auc_response.txt\").filter(x => x.drugId == \"D_0\")","    val GCollection = new Edges(\"./data/biology/edgesGG.txt\").geneCollection","    val patient_gene_data = new drugExampleReader().pgReader(\"./data/biology/gene2med_probe_expr.txt\").filter((x => GCollection.exists(y => (y.GeneID.equals(x.Gene_ID)))))","    val GGCollection = new Edges(\"./data/biology/edgesGG.txt\").edgeCollection","","    logger.info(\"Statistics about read data\")","    logger.info(s\"Number of patients: ${patients_data.size}\")","    logger.info(s\"Number of patient drug records: ${patient_drug_data.size}\")","    logger.info(s\"Number of Genes: ${GCollection.size} \")","    logger.info(s\"Number of patient_gene records: ${patient_gene_data.size} \")","    logger.info(s\"Number of edges: ${GGCollection.size}\")","","    patients.populate(patients_data.slice(0, 5))","    patients.populate(patients_data.slice(6, 10), train = false)","","    patientDrug.populate(patient_drug_data.slice(0, 10))","    patientDrug.populate(patient_drug_data.slice(11, 20), train = false)","","    patientGene.populate(patient_gene_data.slice(0, 10))","    patientGene.populate(patient_gene_data.slice(0, 10), train = false)","","    genes.populate(GCollection.slice(0, 5))","    genes.populate(GCollection.slice(0, 5), train = false)","","    geneGene.populate(GGCollection.slice(0, 10))","    geneGene.populate(GGCollection.slice(0, 10), train = false)","","    //first find all distinct pathways from the list of pathways that are in the list of pathways for each gene","    //then define a new regressor per pathway","    val myLearners = (genes() prop gene_KEGG).flatten.toList.distinct.map(pathwayX => new DrugResponseRegressor(pathwayX))","","    ClassifierUtils.TestClassifiers(myLearners)","    myLearners.foreach(_.testContinuous())","","    //myLearners.map(x => x.test()) //.SortwithAccuracy()","","    val genesGroupedPerPathway2 = SGroupBy(genes, gene_KEGG, geneName)","    // genes SGroupBy gene_KEGG Select geneName","","    val genesGroupedPerPathway = genes().map(x => x.KEGG.map(y => (x.GeneName, y))).flatten.groupBy(_._2).map(x => (x._1, x._2.map(t1 => t1._1)))","","    patientDrug().filter(x => drugResponse(x) > 12)","","    (patients() ~> -pgPatient ~> pgGenes) prop gene_KEGG","","    genes(genes().head) ~> -geneGenes prop textSimilarity","","    genes(Path.findPath(genes().head, genes, genes().head).asInstanceOf[Seq[Gene]]) prop gene_GoTerm","","    // dResponseClassifier.learn(1)","","    //dResponseClassifier.testContinuos(patient_drug_data)","    //DrugResponseRegressor.learn(1)","    //DrugResponseRegressor.testContinuos(patientDrug.getTrainingInstances)","    logger.info(\"finished!\")","  }","","}"];
            newFileWithFilename("myApp",content);
        });

        $("#EmailSpam").click(function(){
            deleteAllFiles();
            var content = ["/** This software is released under the University of Illinois/Research and Academic Use License. See","  * the LICENSE file in the root folder for details. Copyright (c) 2016","  *","  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign","  * http://cogcomp.cs.illinois.edu/","  */","package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam","","import edu.illinois.cs.cogcomp.saul.util.Logging","import edu.illinois.cs.cogcomp.saulexamples.data.DocumentReader","import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.SpamClassifiers._","","import scala.collection.JavaConversions._","","object SpamApp extends Logging {","","  val trainData = new DocumentReader(\"../data/EmailSpam/train\").docs.toList","  val testData = new DocumentReader(\"../data/EmailSpam/test\").docs.toList","","  object SpamExperimentType extends Enumeration {","    val TrainAndTest, CacheGraph, TestUsingGraphCache, TestSerialization = Value","  }","","  def main(args: Array[String]): Unit = {","    /** Choose the experiment you're interested in by changing the following line */","    val testType = SpamExperimentType.TestSerialization","","    testType match {","      case SpamExperimentType.TrainAndTest => TrainAndTestSpamClassifier()","      case SpamExperimentType.CacheGraph => SpamClassifierWithGraphCache()","      case SpamExperimentType.TestUsingGraphCache => SpamClassifierFromCache()","      case SpamExperimentType.TestSerialization => SpamClassifierWithSerialization()","    }","  }","","  /** A standard method for testing the Spam Classification problem. Simply training and testing the resulting model.*/","  def TrainAndTestSpamClassifier(): Unit = {","    /** Defining the data and specifying it's location  */","    SpamDataModel.docs populate trainData","    SpamClassifier.learn(30)","    SpamClassifier.test(testData)","  }","","  /** Spam Classifcation, followed by caching the data-model graph. */","  val graphCacheFile = \"models/temp.model\"","  def SpamClassifierWithGraphCache(): Unit = {","    /** Defining the data and specifying it's location  */","    SpamDataModel.docs populate trainData","    SpamDataModel.deriveInstances()","    SpamDataModel.write(graphCacheFile)","    SpamClassifierWithCache.learn(30)","    SpamClassifierWithCache.test(testData)","  }","","  /** Testing the functionality of the cache. `SpamClassifierWithCache` produces the temporary model file need for","    * this methdd to run.","    */","  def SpamClassifierFromCache() {","    SpamDataModel.load(graphCacheFile)","    SpamClassifierWithCache.learn(30)","    SpamClassifierWithCache.test(testData)","  }","","  /** Testing the serialization functionality of the model. We first train a model and save it. Then we load the model","    * and use it for prediction. We later check whether the predictions of the deserialized model are the same as the","    * predictions before serialization.","    */","  def SpamClassifierWithSerialization(): Unit = {","    SpamDataModel.docs populate trainData","    SpamClassifier.learn(30)","    SpamClassifier.save()","    DeserializedSpamClassifier.load(SpamClassifier.lcFilePath, SpamClassifier.lexFilePath)","    val predictionsBeforeSerialization = testData.map(SpamClassifier(_))","    val predictionsAfterSerialization = testData.map(DeserializedSpamClassifier(_))","    logger.info(predictionsBeforeSerialization.mkString(\"/\"))","    logger.info(predictionsAfterSerialization.mkString(\"/\"))","    logger.info(predictionsAfterSerialization.indices.forall(it => predictionsBeforeSerialization(it) == predictionsAfterSerialization(it)).toString)","  }","}"];
            newFile(content);
            content = ["/** This software is released under the University of Illinois/Research and Academic Use License. See","  * the LICENSE file in the root folder for details. Copyright (c) 2016","  *","  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign","  * http://cogcomp.cs.illinois.edu/","  */","package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam","","import edu.illinois.cs.cogcomp.lbjava.learn.SupportVectorMachine","import edu.illinois.cs.cogcomp.saul.classifier.Learnable","import edu.illinois.cs.cogcomp.saulexamples.data.Document","import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.SpamDataModel._","","object SpamClassifiers {","  object SpamClassifier extends Learnable[Document](docs) {","    def label = spamLabel","    override lazy val classifier = new SupportVectorMachine()","    override def feature = using(wordFeature)","  }","","  object SpamClassifierWithCache extends Learnable[Document](docs) {","    def label = spamLabel","    override lazy val classifier = new SupportVectorMachine()","    override def feature = using(wordFeature)","    override val useCache = true","  }","","  object DeserializedSpamClassifier extends Learnable[Document](docs) {","    def label = spamLabel","    override lazy val classifier = new SupportVectorMachine()","    override def feature = using(wordFeature)","  }","}"];
            newFileWithFilename("spamClassifiers",content);
            content = ["/** This software is released under the University of Illinois/Research and Academic Use License. See","  * the LICENSE file in the root folder for details. Copyright (c) 2016","  *","  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign","  * http://cogcomp.cs.illinois.edu/","  */","package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","import edu.illinois.cs.cogcomp.saulexamples.data.Document","","import scala.collection.JavaConversions._","","object SpamDataModel extends DataModel {","  val docs = node[Document]","","  val wordFeature = property(docs, \"wordF\") {","    x: Document => x.getWords.toList","  }","","  val bigramFeature = property(docs, \"bigram\") {","    x: Document =>","      val words = x.getWords.toList","","      /** bigram features */","      words.sliding(2).map(_.mkString(\"-\")).toList","  }","","  val spamLabel = property(docs, \"label\") {","    x: Document => x.getLabel","  }","}"];
            newFileWithFilename("spamDataModel",content);
        });

        $("#EdisonFeatures").click(function(){
            deleteAllFiles();
            var content = ["/** This software is released under the University of Illinois/Research and Academic Use License. See","  * the LICENSE file in the root folder for details. Copyright (c) 2016","  *","  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign","  * http://cogcomp.cs.illinois.edu/","  */","package edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures","","import edu.illinois.cs.cogcomp.annotation.BasicTextAnnotationBuilder","import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation","import edu.illinois.cs.cogcomp.saulexamples.data.{ Document, DocumentReader }","import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors","","import scala.collection.JavaConversions._","","/** We populate the data and use features in the application below. */","object edisonApp {","","  def main(args: Array[String]): Unit = {","","    import edisonDataModel._","","    val data: List[Document] = new DocumentReader(\"./data/20newsToy/train\").docs.toList.slice(1, 3)","","    /** this generates a list of strings each member is a textual content of a document */","    val documentIndexPair = CommonSensors.textCollection(data).zip(data.map(_.getGUID))","","    val documentList = documentIndexPair.map {","      case (doc, id) =>","        CommonSensors.annotateRawWithCurator(doc, id)","      //commonSensors.annotateWithPipeline(doc, id)","    }","","    val sentenceList = documentList.flatMap(_.sentences())","","    val constituentList = sentenceList.map(_.getSentenceConstituent)","","    /** instantiating nodes */","    documents.populate(documentList)","","    sentences.populate(sentenceList)","","    constituents.populate(constituentList)","","    /** instantiating edges */","    docToSen.populateWith(CommonSensors.textAnnotationSentenceAlignment(_, _))","","    senToCons.populateWith(CommonSensors.sentenceConstituentAlignment(_, _))","","    docToCons.populateWith(CommonSensors.textAnnotationConstituentAlignment(_, _))","","    /** query edges */","    val sentencesQueriedFromDocs = docToSen.forward.neighborsOf(documentList.head)","","    val docsQueriedFromSentences = docToSen.backward.neighborsOf(sentenceList.head)","","    val consQueriesFromSentences = senToCons.forward.neighborsOf(sentenceList.head)","","    val sentencesQueriesFromCons = senToCons.backward.neighborsOf(constituentList.head)","","    val consQueriesFromDocs = docToCons.forward.neighborsOf(documentList.head)","","    val docsQueriesFromCons = docToCons.backward.neighborsOf(constituentList.head)","","    println(sentencesQueriedFromDocs.map(_.toString).toSet == consQueriesFromDocs.map(_.toString).toSet)","","    println(sentencesQueriesFromCons.map(_.toString).toSet == consQueriesFromSentences.map(_.toString).toSet)","","    println(docsQueriedFromSentences.map(_.toString).toSet == docsQueriesFromCons.map(_.toString).toSet)","","    /** querty properties */","    val sentenceContentFromDoc = docToSen.to().prop(sentenceContent)","","    val sentencesContentFromCons = senToCons.from().prop(sentenceContent)","","    val docContentFromSentence = docToSen.from().prop(documentContent)","","    val docContentFromCons = docToCons.from().prop(documentContent)","","    val consContentFromSentence = senToCons.to().prop(constituentContent)","","    val consContentFromDocs = docToCons.to().prop(constituentContent)","","    println(sentenceContentFromDoc.toSet == sentencesContentFromCons.toSet)","    println(docContentFromSentence.toSet == docContentFromCons.toSet)","    println(consContentFromSentence.toSet == consContentFromDocs.toSet)","  }","}","","object toyDataGenerator {","  val documentStrings = List(\"Saul or Soul; that is the question\", \"when will I graduate?\")","  def generateToyDocuments(numDocs: Int): IndexedSeq[Document] = {","    (1 to numDocs).map { _ =>","      val randInt = scala.util.Random.nextInt(2)","      new Document(documentStrings(randInt).split(\" \").toList, randInt.toString)","    }","  }","","  /** Generate toy instances that have the same labels */","  def generateToyDocumentsSingleLabel(numDocs: Int): IndexedSeq[Document] = {","    val label = scala.util.Random.nextInt(2)","    (1 to numDocs).map(_ => new Document(documentStrings(label).split(\" \").toList, label.toString))","  }","","  def generateToyTextAnnotation(numDocs: Int): List[TextAnnotation] = {","    import scala.collection.JavaConversions._","","    (1 to numDocs).map { _ =>","      val numSentences = 5","      val documentsTokenized = (1 to numSentences).map(_ => documentStrings(0).split(\" \"))","      BasicTextAnnotationBuilder.createTextAnnotationFromTokens(documentsTokenized)","    }.toList","  }","}"];
            newFile(content);
            content = ["/** This software is released under the University of Illinois/Research and Academic Use License. See","  * the LICENSE file in the root folder for details. Copyright (c) 2016","  *","  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign","  * http://cogcomp.cs.illinois.edu/","  */","package edu.illinois.cs.cogcomp.saulexamples.nlp.EdisonFeatures","","import edu.illinois.cs.cogcomp.core.datastructures.textannotation._","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","","object edisonDataModel extends DataModel {","","  /** Node Types */","  val documents = node[TextAnnotation]","","  val sentences = node[Sentence]","","  val relations = node[Relation]","","  val constituents = node[Constituent]","","  /** Property Types */","  val label = property(constituents, \"label\") {","    x: Constituent => x.getLabel","  }","","  val constituentContent = property(constituents, \"consContent\") {","    x: Constituent => x.getSpan.toString","  }","","  val documentContent = property(documents, \"docContent\") {","    x: TextAnnotation => x.toString","  }","","  val sentenceContent = property(sentences, \"sentenceContent\") {","    x: Sentence => x.getText","  }","","  /** Edge Types */","  val docToSen = edge(documents, sentences)","","  val senToCons = edge(sentences, constituents)","","  val docToCons = edge(documents, constituents)","","  val consToCons = edge(constituents, constituents)","}"];
            newFile(content);
        });
/*
        $("#POSTagger").click(function(){
            deleteAllFiles();
            var content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","","object $$$$$$ extends DataModel {","","    val firstNames = node[String]","    val lastNames = node[String]","    val name = edge(firstNames,lastNames)","    val prefix = property(firstNames,\"prefix\")((s: String) => s.charAt(1).toString)","    val prefix2 = property(firstNames,\"prefix\")((s: String) => s.charAt(0).toString)","","    def main(args : Array[String]): Unit ={","        firstNames.populate(Seq(\"Dave\",\"John\",\"Mark\",\"Michael\"))","        lastNames.populate(Seq(\"Dell\",\"Jacobs\",\"Maron\",\"Mario\"))","        name.populateWith(_.charAt(0) == _.charAt(0))","    }","}"];
            newFile(content);
        });

        $("#EntityMentionRelation").click(function(){
            deleteAllFiles();
            var content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","","object $$$$$$ extends DataModel {","","    val firstNames = node[String]","    val lastNames = node[String]","    val name = edge(firstNames,lastNames)","    val prefix = property(firstNames,\"prefix\")((s: String) => s.charAt(1).toString)","    val prefix2 = property(firstNames,\"prefix\")((s: String) => s.charAt(0).toString)","","    def main(args : Array[String]): Unit ={","        firstNames.populate(Seq(\"Dave\",\"John\",\"Mark\",\"Michael\"))","        lastNames.populate(Seq(\"Dell\",\"Jacobs\",\"Maron\",\"Mario\"))","        name.populateWith(_.charAt(0) == _.charAt(0))","    }","}"];
            newFile(content);
        });

        $("#SemanticRoleLabeling").click(function(){
            deleteAllFiles();
            var content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","","object $$$$$$ extends DataModel {","","    val firstNames = node[String]","    val lastNames = node[String]","    val name = edge(firstNames,lastNames)","    val prefix = property(firstNames,\"prefix\")((s: String) => s.charAt(1).toString)","    val prefix2 = property(firstNames,\"prefix\")((s: String) => s.charAt(0).toString)","","    def main(args : Array[String]): Unit ={","        firstNames.populate(Seq(\"Dave\",\"John\",\"Mark\",\"Michael\"))","        lastNames.populate(Seq(\"Dell\",\"Jacobs\",\"Maron\",\"Mario\"))","        name.populateWith(_.charAt(0) == _.charAt(0))","    }","}"];
            newFile(content);
        });

        $("#SetCover").click(function(){
            deleteAllFiles();
            var content = ["package test","","import edu.illinois.cs.cogcomp.saulexamples.setcover._","import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","","import scala.collection.mutable.{ Map => MutableMap }","","object $$$$$$ extends DataModel {","","  val cities = node[City]","","  val neighborhoods = node[Neighborhood]","","  val cityContainsNeighborhoods = edge(cities, neighborhoods, 'cityID)","","  cityContainsNeighborhoods.populateWith((c, n) => c == n.getParentCity)","}","","object containsStationConstraint extends ConstrainedClassifier[Neighborhood, City]($$$$$$, new ContainsStation()) {","","  override def subjectTo = test2.containsStationConstrint","","  //    ConstraintClassifier.constraintOf[City]({","  //    x: City => {","  //      val containStation = new ContainsStation()","  //      x.getNeighborhoods _forAll {","  //        n: Neighborhood => {","  //          (containStation on n isTrue) ||| (","  //            n.getNeighbors _exists {","  //              n2: Neighborhood => containStation on n2 isTrue","  //            })","  //        }","  //      }","  //    }","  //  })","}"];
            newFile(content);
            content = ["package test","","import edu.illinois.cs.cogcomp.saulexamples.setcover._","import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier","import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._","","import scala.collection.JavaConversions._","","object $$$$$$ {","  val cities = new City(\"./data/SetCover/example.txt\")","  val ns = cities.getNeighborhoods.toList","","  val containsStationConstrint = ConstrainedClassifier.constraintOf[City]({","    x: City =>","      val containStation = new ContainsStation()","      x.getNeighborhoods _forAll {","        n: Neighborhood =>","          (containStation on n isTrue) ||| (","            n.getNeighbors _exists {","              n2: Neighborhood => containStation on n2 isTrue","            }","          )","      }","  })","","  def main(args: Array[String]) {","    test1.cities populate List(cities)","    test1.neighborhoods populate ns","    test1.cityContainsNeighborhoods.populateWith(_ == _.getParentCity)","","    }","}"];
            newFile(content);
        });

*/
}

var installLeftPanelClick = function(){
    setEditor("editor1","scala"); 
    $("#fileList").children("li").each(function(){
        installTabClickedAction($(this));
    });

    $("#selectedFile").change(function() {
        if (!window.FileReader) {
            alert('Your browser is not supported');
            return false;
        }
        var input = $("#selectedFile").get(0);

        var reader = new FileReader();
        if (input.files.length) {
            var textFile = input.files[0];

            reader.readAsText(textFile);
            $(reader).on('load', processFile);
        }else {
            alert('Please upload a file before continuing')
        } 
    });

    $("#compileBtn").click(function(){
        updateCode(0);
    });

    $("#populateBtn").click(function(){
        updateCode(1);
    });

    $("#runBtn").click(function() {
        updateCode(2);
    });

    $("#queryBtn").click(function() {
        updateCode(3);
    });

    $("#visualizeBtn").click(function() {
        updateCode(-1);
    });

    $("#newmodel").click(function(){
        var content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","","object $$$$$$ extends DataModel {","","}"];
        newFile(content);
    });
    $("#newcla").click(function(){
        var content = ["package test","","import edu.illinois.cs.cogcomp.saul.classifier.Learnable","","object $$$$$$ {","","    object $$$$$$Classifier extends Learnable[???](yourDataModel) {","    ","    }","}"];
        newFile(content);
    });
    $("#newapp").click(function(){
        var content = ["package test","","object $$$$$$ {","","    def main(args: Array[String]) {","    ","    }","}"];
        newFile(content);
    });

    $("#deleteFile").click(function(){
        deleteFile();
    });
}
function installGraphsTabClick(){
    $("#gtabs").children("li").each(function(){
        $(this).click(function(){
            $("#gtabs").children(".active").each(function(){
                $(this).removeClass("active");
            });
            $(this).addClass("active");
            $(".tab-content").children(".active").each(function(){
                $(this).removeClass("active");
                $(this).hide();
            });
            $("#"+ $(this).attr("value")).addClass("active").show();
        });
    });
}
function processFile(e) {
    var file = e.target.result,
        results;
    if (file && file.length) {
        results = file.split("\n");
        newFile(results);
    }
}


var deleteAllFiles = function(){
    while($("#fileList").children(".active").length != 0){
        deleteFile();
    }
}
var deleteFile = function(){
    $("#fileList").children(".active").each(function(){
        $(this).remove();
    });
    $("#workspace").children(".active").each(function(){
        $(this).remove();
    });
    $("#fileList").children("li").first().addClass("active");
    $("#workspace").children().first().addClass("active");
    $("#workspace").children().first().show();

}
var setEditor = function(editorId,mode, content){
    var editor = ace.edit(editorId);
    editor.setTheme("ace/theme/monokai");
    changeEditorMode(editor, mode);
    if(content) editor.getSession().getDocument().insertLines(0,content);
    editor.resize(true);
    editor.focus();
}

var changeEditorMode = function(editor, mode){
    var mode;
    if(mode == "scala"){
        mode = require("ace/mode/scala").Mode;
    }
    if(mode == "java"){
    
        mode = require("ace/mode/java").Mode;
    }
    editor.getSession().setMode(new mode());
}

var newFileWithFilename = function(filename,content){
    newFile(content);
    if(endsWith(filename,".java") || endsWith(filename,".scala")){
        $("#fileList").find(".active a").text(filename);
    }
    else{
        $("#fileList").find(".active a").text(filename+".scala");
    }
}
var newFile = function(content){
    content = content || [];
    var li = $("<li class='active'><a href='#'></a></li>");
    var idx = $("#fileList").children("li").size() + 1;
    li.children().each(function(){
        $(this).text('test'+idx+'.scala');
    });
    $("#fileList").children(".active").removeClass("active"); 
    li.attr('id','fileName' + idx);
    $("#fileList").append(li);
    installTabClickedAction(li);

    $("#workspace").children(".active").each(function(){
        $(this).removeClass("active");
        $(this).hide();
    })
    var editor = $("<div class='editor active' id='editor"+idx+"'></div>");
    $("#workspace").append(editor);
    for(var line in content){
        content[line] = content[line].replace("$$$$$$","test"+idx);
    }
    setEditor("editor"+idx,"scala",content);
}

//check string end with suffix
function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

var enableEditingTabName = function(tab){

    var text = tab.text();
    var input = $("<input id='tempName' type='text' value='" + text + "' />");
    tab.find("a").hide();
    tab.append(input);

    input.select();
    input.bind('blur keyup',function(e) {  
        if (e.type == 'blur' || e.keyCode == '13'){
            var text = $('#tempName').val();
            var label = $('#tempName').parent().find("a");
            var idx = tab.attr('id').slice(-1);
            if(endsWith(text,".java")) {
                label.text(text);
                changeEditorMode(ace.edit("editor"+idx),"java");
            }
            if(endsWith(text,".scala")){
                label.text(text);
                changeEditorMode(ace.edit("editor"+idx),"scala");
            }
            label.show();
            $('#tempName').remove()
            $("#fileList .active").removeData("executing");

        }
     });
}
var installTabClickedAction = function(tab){
    tab.click(function(){

        if($(this).hasClass("active")){        
            var $this = $(this);
            if ($this.data("executing")) return;
            $this.data("executing", true);
            //edit the file name
            enableEditingTabName($(this));
        }
        else{        
            //switch to other tabs
            $("#fileList").children(".active").each(function(){
                $(this).removeClass("active");
            })
            tab.addClass("active");
            var idx = tab.attr('id').match(/\d+$/)[0];
            $("#workspace").children(".active").each(function(){
                $(this).removeClass("active");
                $(this).hide(); 
            })
            $("#editor"+idx).show();
            $("#editor"+idx).addClass("active");
            ace.edit("editor"+idx).focus();
        }
    })
}

var getAllFiles = function(){
    var files = {}
    $("#fileList").children("li").each(function(index){
        var idx = $(this).attr('id').match(/\d+$/)[0];
        var codeId = "editor" + idx;
        var codeName = $(this).text();
        files[codeName] = ace.edit(codeId).getValue();
    })
    return JSON.stringify(files);
}

var updateCode = function(event){
    var rURL;
    var onSuccess;
    if (event == 0) {
        rURL = '/compileCode';
        onSuccess = onCompileSuccess;
    } else if (event == 1) {
        rURL = '/populate'
        onSuccess = onPopulateSuccess;
    } else if (event == 2) {
        rURL = '/runCode';
        onSuccess = onRunSuccess;
    } else if (event == 3) {
        rURL = '/query';
        onSuccess = onQuerySuccess;
    } else if (event == -1) {
        rURL = '/visualize';
        onSuccess = onVisualizeSuccess;
    }

    var callback = {
        success : onSuccess,
        error : onError
    }
    var query = $("#query")[0].value
    var files = getAllFiles()
    var dataJson = files
    if(event == 3) {
        dataJson = JSON.stringify({
            "files": files,
            "query": query
        })
    }
    $("#pbar").show();
    $.ajax({
        type : 'POST',
        url : rURL,
        headers: { 
                'Accept': 'application/json',
                'Content-Type': 'application/json' 
                },
        data : dataJson,
        success : onSuccess,
        error: onError
        });
};

var displayOutput = function(data) {
    $("#tab3").addClass("active");
    $("#tab3").html('')
    var info = $("<p></p>").text("Info: \n" + data["stdout"]);
    var error = $("<p style='color:red;'></p>").text("Error: \n" + data["stderr"]);
    $("#tab3").append(info);
    $("#tab3").append(error);
}


var alertError = function(data) {
    console.log(JSON.stringify(data));
    if(data['error']){
        var message = "";
        if(data['error'].constructor === Array){
            for(var index in data['error']){
                for(var index2 in data['error'][index]){
                    message += data['error'][index][index2] + "<br>";
                }
            }
        }else{
                message += data['error'] + "<br>";
        }
        $("#errors").html(message);
        $("#errors").show();
    }else{
        $("#errors").hide();
    }
}

var onCompileSuccess = function(data){
    $("#pbar").hide();
    alertError(data);
    $("#gtab1").click();
    generateSchemaGraphFromJson(data);
    
}

var onPopulateSuccess = function(data) {
    $("#pbar").hide();
    alertError(data);
    $("#gtab2").click();
    generatePopulatedGraphFromJson(data);    
}

var onRunSuccess = function(data) {
    $("#pbar").hide();
    alertError(data);
    $("#gtab3").click();
    displayOutput(data);
}

var onQuerySuccess = function(data) {
    onPopulateSuccess(data);
}

var onVisualizeSuccess = function(data) {
    $("#pbar").hide();
    alertError(data);
    $("#gtab1").click();
    generateSchemaGraphFromJson(data['dataModelSchema']);
    if(data['populatedModel'] != null){
        $("#gtab2").click();
        generatePopulatedGraphFromJson(data['populatedModel']);
    }  
    if(data['log'] != null){ 
        ("#gtab3").click();
        displayOutput(data['log']);
    }
}

var onError = function(data){
    $("#pbar").hide();
    alert("error"+data);
}
