var colors = ["#000000", "#FFFF00", "#1CE6FF", "#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
        "#FFDBE5", "#7A4900", "#0000A6", "#63FFAC", "#B79762", "#004D43", "#8FB0FF", "#997D87"];

$(document).ready(function(){

  sigma.classes.graph.addMethod('neighbors', function(nodeId) {
    var k,
        neighbors = {},
        index = this.allNeighborsIndex[nodeId] || {};

    for (k in index)
      neighbors[k] = this.nodesIndex[k];

    return neighbors;
  });

        $("#errors").hide();
        setEditor("editor1","scala"); 
        $("#fileList").children("li").each(function(){
            installTabClickedAction($(this));
        })

        $("#selectedFile").change(function() {
            if (!window.FileReader) {
                alert('Your browser is not supported');
                return false;
            }
            var input = $("#selectedFile").get(0);

            // Create a reader object
            var reader = new FileReader();
            if (input.files.length) {
                var textFile = input.files[0];
                // Read the file
                reader.readAsText(textFile);
                // When it's loaded, process it
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

        $("#ToyExample").click(function(){
            deleteAllFiles();
            var content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","","object $$$$$$ extends DataModel {","","    val firstNames = node[String]","    val lastNames = node[String]","    val name = edge(firstNames,lastNames)","    val prefix = property(firstNames,\"prefix\")((s: String) => s.charAt(1).toString)","    val prefix2 = property(firstNames,\"prefix\")((s: String) => s.charAt(0).toString)","","    def main(args : Array[String]): Unit ={","        firstNames.populate(Seq(\"Dave\",\"John\",\"Mark\",\"Michael\"))","        lastNames.populate(Seq(\"Dell\",\"Jacobs\",\"Maron\",\"Mario\"))","        name.populateWith(_.charAt(0) == _.charAt(0))","    }","}"];
            newFile(content);
        });

        $("#EdisonFeatures").click(function(){
            deleteAllFiles();
            var content = ["package test","","import edu.illinois.cs.cogcomp.annotation.BasicTextAnnotationBuilder","import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation","import edu.illinois.cs.cogcomp.saulexamples.data.{ Document, DocumentReader }","import edu.illinois.cs.cogcomp.saulexamples.nlp.CommonSensors","","import scala.collection.JavaConversions._","","/** We populate the data and use features in the application below. */","object $$$$$$ {","","  def main(args: Array[String]): Unit = {","","    import test2._","","    val data: List[Document] = new DocumentReader(\"./data/20newsToy/train\").docs.toList.slice(1, 3)","","    /** this generates a list of strings each member is a textual content of a document */","    val documentIndexPair = CommonSensors.textCollection(data).zip(data.map(_.getGUID))","","    val documentList = documentIndexPair.map {","      case (doc, id) =>","        CommonSensors.annotateRawWithCurator(doc, id)","      //commonSensors.annotateWithPipeline(doc, id)","    }","","    val sentenceList = documentList.flatMap(_.sentences())","","    val constituentList = sentenceList.map(_.getSentenceConstituent)","","    /** instantiating nodes */","    documents.populate(documentList)","","    sentences.populate(sentenceList)","","    constituents.populate(constituentList)","","    /** instantiating edges */","    docToSen.populateWith(CommonSensors.textAnnotationSentenceAlignment(_, _))","","    senToCons.populateWith(CommonSensors.sentenceConstituentAlignment(_, _))","","    docToCons.populateWith(CommonSensors.textAnnotationConstituentAlignment(_, _))","","    /** query edges */","    val sentencesQueriedFromDocs = docToSen.forward.neighborsOf(documentList.head)","","    val docsQueriedFromSentences = docToSen.backward.neighborsOf(sentenceList.head)","","    val consQueriesFromSentences = senToCons.forward.neighborsOf(sentenceList.head)","","    val sentencesQueriesFromCons = senToCons.backward.neighborsOf(constituentList.head)","","    val consQueriesFromDocs = docToCons.forward.neighborsOf(documentList.head)","","    val docsQueriesFromCons = docToCons.backward.neighborsOf(constituentList.head)","","    println(sentencesQueriedFromDocs.map(_.toString).toSet == consQueriesFromDocs.map(_.toString).toSet)","","    println(sentencesQueriesFromCons.map(_.toString).toSet == consQueriesFromSentences.map(_.toString).toSet)","","    println(docsQueriedFromSentences.map(_.toString).toSet == docsQueriesFromCons.map(_.toString).toSet)","","    /** querty properties */","    val sentenceContentFromDoc = docToSen.to().prop(sentenceContent)","","    val sentencesContentFromCons = senToCons.from().prop(sentenceContent)","","    val docContentFromSentence = docToSen.from().prop(documentContent)","","    val docContentFromCons = docToCons.from().prop(documentContent)","","    val consContentFromSentence = senToCons.to().prop(constituentContent)","","    val consContentFromDocs = docToCons.to().prop(constituentContent)","","    println(sentenceContentFromDoc.toSet == sentencesContentFromCons.toSet)","    println(docContentFromSentence.toSet == docContentFromCons.toSet)","    println(consContentFromSentence.toSet == consContentFromDocs.toSet)","  }","}","","object toyDataGenerator {","  val documentStrings = List(\"Saul or Soul; that is the question\", \"when will I graduate?\")","  def generateToyDocuments(numDocs: Int): IndexedSeq[Document] = {","    (1 to numDocs).map { _ =>","      val randInt = scala.util.Random.nextInt(2)","      new Document(documentStrings(randInt).split(\" \").toList, randInt.toString)","    }","  }","","  /** Generate toy instances that have the same labels */","  def generateToyDocumentsSingleLabel(numDocs: Int): IndexedSeq[Document] = {","    val label = scala.util.Random.nextInt(2)","    (1 to numDocs).map(_ => new Document(documentStrings(label).split(\" \").toList, label.toString))","  }","","  def generateToyTextAnnotation(numDocs: Int): List[TextAnnotation] = {","    import scala.collection.JavaConversions._","","    (1 to numDocs).map { _ =>","      val numSentences = 5","      val documentsTokenized = (1 to numSentences).map(_ => documentStrings(0).split(\" \"))","      BasicTextAnnotationBuilder.createTextAnnotationFromTokens(documentsTokenized)","    }.toList","  }","}"]
            newFile(content);
            content = ["package test","","import edu.illinois.cs.cogcomp.core.datastructures.textannotation._","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","","object $$$$$$ extends DataModel {","","  /** Node Types */","  val documents = node[TextAnnotation]","","  val sentences = node[Sentence]","","  val relations = node[Relation]","","  val constituents = node[Constituent]","","  /** Property Types */","  val label = property(constituents, \"label\") {","    x: Constituent => x.getLabel","  }","","  val constituentContent = property(constituents, \"consContent\") {","    x: Constituent => x.getSpan.toString","  }","","  val documentContent = property(documents, \"docContent\") {","    x: TextAnnotation => x.toString","  }","","  val sentenceContent = property(sentences, \"sentenceContent\") {","    x: Sentence => x.getText","  }","","  /** Edge Types */","  val docToSen = edge(documents, sentences)","","  val senToCons = edge(sentences, constituents)","","  val docToCons = edge(documents, constituents)","","  val consToCons = edge(constituents, constituents)","}"]
            newFile(content);
        });

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

        $("#KnowEng").click(function(){
            deleteAllFiles();
            var content = ["package test","","import edu.illinois.cs.cogcomp.saulexamples.bioInformatics._","/** Created by Parisa on 1/24/16. Modified by Joey on 2/16/16","  */","object bioSensors {","","  def patientDrugMatchSensor(x: PatientDrug, patient: Patient): Boolean = {","    x.pid == patient.patient_id","  }","  def pgPatientMatchSensor(x: PatientGene, patient: Patient): Boolean = {","    x.sample_ID == patient.patient_id","  }","","  def pgGeneMatchSensor(x: PatientGene, gene: Gene): Boolean = {","    x.Gene_ID == gene.GeneID","  }","  def ggGeneMatchSensor(x: GeneGene, gene: Gene): Boolean = {","    x.sink_nodeID == gene.GeneID || x.source_nodeID == gene.GeneID","  }","}"];
            newFileWithFilename("bioSensors",content);
            content = ["package test","","import edu.illinois.cs.cogcomp.lbjava.learn.StochasticGradientDescent","import edu.illinois.cs.cogcomp.saul.classifier.Learnable","import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.PatientDrug","import test.knowEngDataModel._","import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._","/** Created by Parisa on 6/25/15.","  */","object Classifiers {","  object dResponseClassifier extends Learnable[PatientDrug](knowEngDataModel) {","    def label = drugResponse","    override def feature = using(cP1)","    override lazy val classifier = new StochasticGradientDescent","  }","}",""];
            newFileWithFilename("Classifiers",content);
            content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","import test.Classifiers.dResponseClassifier","import edu.illinois.cs.cogcomp.saulexamples.bioInformatics._","import test.bioSensors._","","import scala.collection.JavaConversions._","/** Created by Parisa on 6/24/15.","  */","object knowEngDataModel extends DataModel {","","  val patients = node[Patient]","  val genes = node[Gene]","  val patientGene = node[PatientGene]","  val patientDrug = node[PatientDrug]","  val geneGene = node[GeneGene]","","  val pdPatient = edge(patientDrug, patients)","  val pgPatient = edge(patientGene, patients)","  val pgGenes = edge(patientGene, genes)","  val geneGenes = edge(geneGene, genes)","","  pdPatient.addSensor(patientDrugMatchSensor _)","  pgPatient.addSensor(pgPatientMatchSensor _)","  pgGenes.addSensor(pgGeneMatchSensor _)","  geneGenes.addSensor(ggGeneMatchSensor _)","","  val age = property(patients) {","    x: Patient => x.age.toDouble","  }","  val gender = property(patients) {","    x: Patient => x.gender","  }","  val ethnicity = property(patients) {","    x: Patient => x.ethnicity","  }","  val geneName = property(genes) {","    x: Gene => x.GeneName","  }","","  val gene_GoTerm = property(genes) {","    x: Gene =>","      if (x.GO_term == null)","        List(\"\") else (x.GO_term.toList)","  }","  val gene_KEGG = property(genes) {","    x: Gene =>","      if (x.KEGG == null)","        List(\"\") else x.KEGG.toList","  }","  val gene_motif = property(genes) {","    x: Gene => x.motif_u5_gc.doubleValue()","  }","  val gene_pfam_domain = property(genes) {","    x: Gene => x.pfam_domain.doubleValue()","  }","","  val geneExpression = property(patientGene) {","    x: PatientGene => x.singleGeneExp.doubleValue()","  }","","  val drugResponse = property(patientDrug) {","    x: PatientDrug => x.response.doubleValue()","  }","  val similarity = property(geneGene) {","    x: GeneGene => x.similarity.doubleValue()","  }","  val textSimilarity = property(geneGene) {","    x: GeneGene => x.STRING_textmining","  }","  val cP1 = property(patientDrug) {","    x: PatientDrug => (patientDrug(x) ~> pdPatient prop age).propValues.toList","  }","","  val responsePrediction = property(patientDrug) {","    x: PatientDrug => dResponseClassifier(x)","  }","}"];
            newFileWithFilename("knowEngDataModel",content);
            content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.node.Path","import test.Classifiers.dResponseClassifier","import test.knowEngDataModel._","import edu.illinois.cs.cogcomp.saulexamples.bioInformatics.{ Gene, Edges, Sample_Reader, drugExampleReader }","","import scala.collection.JavaConversions._","/** Created by Parisa on 6/25/15.","  */","object myApp {","","  def main(args: Array[String]): Unit = {","","    val patients_data = new Sample_Reader(\"./data/biology/individual_samples.txt\").patientCollection.slice(0, 5)","    val patient_drug_data = new drugExampleReader().pdReader(\"./data/biology/auc_response.txt\").filter(x => x.drugId == \"D_0\").slice(0, 5)","    val GCollection = new Edges(\"./data/biology/edgesGG.txt\").geneCollection.slice(0, 10)","    val patient_gene_data = new drugExampleReader().pgReader(\"./data/biology/gene2med_probe_expr.txt\").filter((x => GCollection.exists(y => (y.GeneID.equals(x.Gene_ID))))).slice(0, 5)","    val GGCollection = new Edges(\"./data/biology/edgesGG.txt\").edgeCollection.slice(0, 20)","","    patients.populate(patients_data)","    patientDrug.populate(patient_drug_data)","    patientGene.populate(patient_gene_data)","    genes.populate(GCollection)","    geneGene.populate(GGCollection)","","    patientDrug().filter(x => drugResponse(x) > 12)","","    (patients() ~> -pgPatient ~> pgGenes) prop gene_KEGG","","    genes(genes().head) ~> -geneGenes prop textSimilarity","","    genes(Path.findPath(genes().head, genes, genes().head).asInstanceOf[Seq[Gene]]) prop gene_GoTerm","    //.filter(x=> gene_GoTerm(x.asInstanceOf[Gene]).equals(\"Go1\"))","","    dResponseClassifier.learn(1)","","    dResponseClassifier.testContinuos(patient_drug_data)","","  }","}"];
            newFileWithFilename("myApp",content);
        });

        $("#EmailSpam").click(function(){
            deleteAllFiles();
            var content = ["package test","","import edu.illinois.cs.cogcomp.saulexamples.data.DocumentReader","import test.spamClassifiers.{ spamClassifierWithCache, deserializedSpamClassifier, spamClassifier }","","import scala.collection.JavaConversions._","","object $$$$$$ {","","  val trainData = new DocumentReader(\"./data/EmailSpam/train\").docs.toList","  val testData = new DocumentReader(\"./data/EmailSpam/test\").docs.toList","","  object SpamExperimentType extends Enumeration {","    val TrainAndTest, CacheGraph, TestUsingGraphCache, TestSerializatin = Value","  }","","  def main(args: Array[String]): Unit = {","    /** Choose the experiment you're interested in by changing the following line */","    val testType = SpamExperimentType.TrainAndTest","","    testType match {","      case SpamExperimentType.TrainAndTest => TrainAndTestSpamClassifier()","      case SpamExperimentType.CacheGraph => SpamClassifierWithGraphCache()","      case SpamExperimentType.TestUsingGraphCache => SpamClassifierFromCache()","      case SpamExperimentType.TestSerializatin => SpamClassifierWithSerialization()","    }","  }","","  /** A standard method for testing the Spam Classification problem. Simply training and testing the resulting model.*/","  def TrainAndTestSpamClassifier(): Unit = {","    /** Defining the data and specifying it's location  */","    spamDataModel.docs populate trainData","    spamClassifier.learn(30)","    spamDataModel.testWith(testData)","    spamClassifier.test(testData)","  }","","  /** Spam Classifcation, followed by caching the data-model graph. */","  val graphCacheFile = \"models/temp.model\"","  def SpamClassifierWithGraphCache(): Unit = {","    /** Defining the data and specifying it's location  */","    spamDataModel.docs populate trainData","    spamDataModel.deriveInstances()","    spamDataModel.write(graphCacheFile)","","    spamClassifierWithCache.learn(30)","    spamClassifierWithCache.learn(30)","    spamDataModel.testWith(testData)","    spamClassifierWithCache.test(testData)","  }","","  /** Testing the functionality of the cache. `SpamClassifierWithCache` produces the temporary model file need for","    * this methdd to run.","    */","  def SpamClassifierFromCache() {","    spamDataModel.load(graphCacheFile)","    spamClassifierWithCache.learn(30)","    spamClassifierWithCache.learn(30)","    spamDataModel.testWith(testData)","    spamClassifierWithCache.test(testData)","  }","","  /** Testing the serialization functionality of the model. We first train a model and save it. Then we load the model","    * and use it for prediction. We later check whether the predictions of the deserialized model are the same as the","    * predictions before serialization.","    */","  def SpamClassifierWithSerialization(): Unit = {","    spamDataModel.docs populate trainData","    spamClassifier.learn(30)","","    spamClassifier.save()","","    println(deserializedSpamClassifier.classifier.getPrunedLexiconSize)","    deserializedSpamClassifier.load(spamClassifier.lcFilePath, spamClassifier.lexFilePath)","","    val predictionsBeforeSerialization = testData.map(spamClassifier(_))","    val predictionsAfterSerialization = testData.map(deserializedSpamClassifier(_))","    println(predictionsBeforeSerialization.mkString(\"/\"))","    println(predictionsAfterSerialization.mkString(\"/\"))","    println(predictionsAfterSerialization.indices.forall(it => predictionsBeforeSerialization(it) == predictionsAfterSerialization(it)))","  }","}"];
            newFile(content);
            content = ["package test","","import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner","import edu.illinois.cs.cogcomp.saul.classifier.Learnable","import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._","import edu.illinois.cs.cogcomp.saulexamples.data.Document","import test.spamDataModel._","","object spamClassifiers {","  object spamClassifier extends Learnable[Document](spamDataModel) {","    def label = spamLabel","    override lazy val classifier = new SparseNetworkLearner()","    override def feature = using(wordFeature)","  }","","  object spamClassifierWithCache extends Learnable[Document](spamDataModel) {","    def label = spamLabel","    override lazy val classifier = new SparseNetworkLearner()","    override def feature = using(wordFeature)","    override val useCache = true","  }","","  object deserializedSpamClassifier extends Learnable[Document](spamDataModel) {","    def label = spamLabel","    override lazy val classifier = new SparseNetworkLearner()","    override def feature = using(wordFeature)","  }","}"];
            newFileWithFilename("spamClassifiers",content);
            content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","import edu.illinois.cs.cogcomp.saulexamples.data.Document","","import scala.collection.JavaConversions._","","object spamDataModel extends DataModel {","  val docs = node[Document]","","  val wordFeature = property(docs, \"wordF\") {","    x: Document => x.getWords.toList","  }","","  val bigramFeature = property(docs, \"bigram\") {","    x: Document =>","      val words = x.getWords.toList","","      /** bigram features */","      words.sliding(2).map(_.mkString(\"-\")).toList","  }","","  val spamLabel = property(docs, \"label\") {","    x: Document => x.getLabel","  }","}"];
            newFileWithFilename("spamDataModel",content);
        });
})

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
                //switch to other tabs
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
//            $("#code"+idx).addClass("active");
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

    //jsRoutes.controllers.Application.updateCode($("#code1").text()).ajax(callback);
    var rURL;
    var onSuccess;
    if (event == 0) {
        rURL = '/compileCode';
        onSuccess = onCompileSuccess;
    } else if (event == 1) {
        rURL = '/populate'
        onSuccess = onPopulateSuccess;
    } else {
        rURL = '/runCode';
        onSuccess = onRunSuccess;
    }

    var callback = {
        success : onSuccess,
        error : onError
    }
    $("#pbar").show();
    $.ajax({
        type : 'POST',
        url : rURL,
        headers: { 
                'Accept': 'application/json',
                'Content-Type': 'application/json' 
                },
        data : getAllFiles(),
        success : onSuccess,
        error: onError
        });
};

var enableColoringNeighbors = function(s){
    s.graph.nodes().forEach(function(n) {
        n.originalColor = n.color;
    });
    s.graph.edges().forEach(function(e) {
        e.originalColor = e.color;
    });

      // When a node is clicked, we check for each node
      // if it is a neighbor of the clicked one. If not,
      // we set its color as grey, and else, it takes its
      // original color.
      // We do the same for the edges, and we only keep
      // edges that have both extremities colored.
    s.bind('overNode',function(e){
        $("#nodevalue").text(e.data.node.label);
    });

    s.bind('clickNode', function(e) {

        var nodeId = e.data.node.id,
            toKeep = s.graph.neighbors(nodeId);
        toKeep[nodeId] = e.data.node;

        s.graph.nodes().forEach(function(n) {
          if (toKeep[n.id])
            n.color = n.originalColor;
          else
            n.color = '#eee';
        });

        s.graph.edges().forEach(function(e) {
          if (toKeep[e.source] && toKeep[e.target])
            e.color = e.originalColor;
          else
            e.color = '#eee';
        });
        s.refresh();
    });

    s.bind('clickStage', function(e) {
        s.graph.nodes().forEach(function(n) {
          n.color = n.originalColor;
        });

        s.graph.edges().forEach(function(e) {
          e.color = e.originalColor;
        });

        // Same as in the previous event:
        s.refresh();
    });
}
var generatePopulatedGraphFromJson = function(data) {

    $('#populatedGraphContainer').remove();
    $('#graphParent2').html('<div id="populatedGraphContainer"></div>');
    var s = new sigma({renderer: {
        container: document.getElementById('populatedGraphContainer'),
        type: 'canvas'
    },
        settings: {
        edgeLabelSize: 'proportional',
        labelThreshold: 10
    }});
    var dragListener = sigma.plugins.dragNodes(s, s.renderers[0]);
    /*dragListener.bind('drag', function(event) {
        s.graph.neighbors(event.node.id).forEach(function(e) {
            e.x = event.node.x;
            e.y = event.node.y;
        })
    });
*/
    var nodeId = 0;
    var nodeDict = {};
    var nodePropertyCount = {};
    var totalNumNodes = 0;
    for(var nodeGroup in data['nodes']) {
        totalNumNodes += data['nodes'][nodeGroup].length;
    }

    var getNodeByLabel = function(label){
        var id = nodeDict[label];
        return s.graph.nodes(id);
    }
    var nodeGroupCount = 0;
    for(var nodeGroup in data['nodes']) {

        nodeGroupCount++;
        for(var node in data['nodes'][nodeGroup]) {
            nodePropertyCount[data['nodes'][nodeGroup][node]] = 0;
            nodeDict[data['nodes'][nodeGroup][node]] = 'n' + ++nodeId;
            s.graph.addNode({
                id: 'n' + nodeId,
                label: data['nodes'][nodeGroup][node],
                size: 3,
                x: (totalNumNodes > 30 ? 3*(3+(nodeId / 30)) : 1) * Math.cos(2 * nodeId * Math.PI / (totalNumNodes > 30 ? 30 : totalNumNodes)),
                y: (totalNumNodes > 30 ? 3*(3+(nodeId / 30)) : 1) * Math.sin(2 * nodeId * Math.PI / (totalNumNodes > 30 ? 30 : totalNumNodes)),
                color: colors[nodeGroupCount % colors.length]
            });
            
        };
    };

    var edgeId = 0;
    for(var source in data['edges']) {
        for(var targetNode in data['edges'][source]) {

            s.graph.addEdge({
                id: 'e' + edgeId++,
                source: nodeDict[source],
                target: nodeDict[data['edges'][source][targetNode]],
                type: 'curve'
            });
        }
    };


    var propertyCount = 0
    for(var node in data['properties']){
        for(var propertyIndex in data['properties'][node]){
            for(var pI in data['properties'][node][propertyIndex]){
            propertyCount ++;
            ++nodePropertyCount[node];
            var parentNode = getNodeByLabel(node);
            s.graph.addNode({
                id: 'p' + propertyCount,
                label: pI+": "+data['properties'][node][propertyIndex][pI],
                size: 1,
                x: parentNode.x + 1 * Math.cos(2 * nodePropertyCount[node] * Math.PI / 6),
                y: parentNode.y + 1 * Math.sin(2 * nodePropertyCount[node] * Math.PI / 6),
                color: colors[propertyCount % colors.length]
            });
            s.graph.addEdge({
                id: 'e'+ edgeId++,
                source: nodeDict[node],
                target: 'p' + propertyCount,
                type: 'curve'
            });
            }
        }
    }

    enableColoringNeighbors(s);
    s.refresh();
    $("#populatedGraphContainer").css("position","absolute");
}

var generateSchemaGraphFromJson = function(data){
    $('#schemaGraphContainer').remove();
    $('#graphParent1').html('<div id="schemaGraphContainer"></div>');
    var s = new sigma({renderer: {
        container: document.getElementById('schemaGraphContainer'),
        type: 'canvas'
    },
        settings: {
        edgeLabelSize: 'proportional',
        labelThreshold: 0
    }});

    var dragListener = sigma.plugins.dragNodes(s, s.renderers[0]);
    var nodeId = 0;
    var nodeDict = {};
    var nodePropertyCount = {};

    for(var node in data['nodes']){
        nodePropertyCount[data['nodes'][node]] = 0;
        nodeDict[data['nodes'][node]] = 'n'+ ++nodeId;
        s.graph.addNode({
            id: 'n'+ nodeId,
            label: data['nodes'][node],
            size: 3,
            x: Math.cos(2 * nodeId * Math.PI / data['nodes'].length),
            y: Math.sin(2 * nodeId * Math.PI / data['nodes'].length),
            color: "#ec5148"
        });
    };

    var edgeId = 0;
    for(var edge in data['edges']){
        s.graph.addEdge({
            id: 'e'+ edgeId++,
            // Reference extremities:
            source: nodeDict[data['edges'][edge][0]],
            target: nodeDict[data['edges'][edge][1]],
            type: 'curve'
        });
    };

    var getNodeByLabel = function(label){
        var id = nodeDict[label];
        return s.graph.nodes(id);
    }

    //generate properties nodes and edges
    for(var property in data['properties']){
        ++nodePropertyCount[data['properties'][property]];
        var parentNode = getNodeByLabel(data['properties'][property]);
        s.graph.addNode({
            id: 'p' + property + nodePropertyCount[data['properties'][property]],
            label: property,
            size: 1,
            x: parentNode.x + 0.5 * Math.cos(2 * nodePropertyCount[data['properties'][property]] * Math.PI / 6),
            y: parentNode.y + 0.5 * Math.sin(2 * nodePropertyCount[data['properties'][property]] * Math.PI / 6),
            color: "#0000ff"
        });
        s.graph.addEdge({
            id: 'e'+ edgeId++,
            source: nodeDict[data['properties'][property]],
            target: 'p' + property + nodePropertyCount[data['properties'][property]],
            type: 'curve'
        });
    }

    enableColoringNeighbors(s);
    s.refresh();
    $("#schemaGraphContainer").css("position","absolute");
}

var displayOutput = function(data) {
    var stdout = $("<p></p>").text("stdout: " + data["stdout"]);
    var stderr = $("<p></p>").text("stderr: " + data["stderr"]);
    $("#tab3").append(stdout);
    $("#tab3").append(stderr);
}

jQuery(document).ready(function() {
    $('.tabs .tab-links a').on('click', function(e)  {
        var currentAttrValue = jQuery(this).attr('href');
        changeTab(currentAttrValue)
        e.preventDefault();
    });
});

var changeTab = function(currentAttrValue) {

    // Show/Hide Tabs
    $('.tabs #' + currentAttrValue).show().siblings().hide();
    $("." + currentAttrValue).addClass('active');
    $("." + currentAttrValue).siblings().removeClass('active');


}

var alertError = function(data) {
    alert(JSON.stringify(data));
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
    changeTab("tab1");
    generateSchemaGraphFromJson(data);
}

var onPopulateSuccess = function(data) {
    $("#pbar").hide();
    alertError(data);
    changeTab("tab2");
    generatePopulatedGraphFromJson(data);
}

var onRunSuccess = function(data) {
    $("#pbar").hide();
    alertError(data);
    changeTab("tab3");
    displayOutput(data)
}

var onError = function(data){
    $("#pbar").hide();
    alert("error"+data);
}
