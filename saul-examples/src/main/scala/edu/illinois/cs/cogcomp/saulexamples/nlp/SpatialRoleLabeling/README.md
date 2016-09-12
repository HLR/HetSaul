# Spatial Role Labeling
In paper [1], the task of spatial role labeling is introduced and an annotation scheme proposed that is language-independent and facilitates the application of machine learning techniques. The framework consists of a set of spatial roles. For a basic example, in the following sentence:


Give me [the gray book]{trajector} [on]{spatialindicator} [the big table]{landmark}.


The phrase headed by the token "book" is referring to a trajector object, the phrase headed by the token "table" is referring to the role of a landmark and these are related by the spatial expression "on" denoted as spatial indicator. The spatial indicator (often a preposition) establishes the type of spatial relation.


Analogous to semantic role labeling, spatial role labeling is defined as the task of automatic labeling of words or phrases in a sentence with a set of spatial roles. More speciﬁcally, it involves identifying and classifying the spatial arguments of the spatial expressions mentioned in a sentence.


## SpRL using Saul
In this example, we implement the best performing algorithm for [task 3 of SemEval 2012](https://www.cs.york.ac.uk/semeval-2012/task3.html) proposed in [2] using Saul.

### Data representation and preparation
The SpRL data are in the form of annotated sentences, we need basic data structures to load these data and feed them to the Saul application. [`SpRLSentence`](../../../../../../../../java/edu/illinois/cs/cogcomp/saulexamples/nlp/SpatialRoleLabeling/SpRLSentence.java) and [`SpRelation`](../../../../../../../../java/edu/illinois/cs/cogcomp/saulexamples/nlp/SpatialRoleLabeling/Triplet/SpRelation.java) classes used for this purpose. In order to benefit from all of the built-in features provided by `TextAnnotation`, we used `TextAnnotation.Sentence` to represent the sentences in the `SpRLSentence` class.
The data sets are collections of xml files, so we used code generators to generate corresponding classes and a data reader for them.

So, the next step is to load these files and convert them to a collection of `SpRLSentence`. In this step we split sentences in each document and convert them to `SpRLSentence` objects.

```scala
    val reader = new SpRLDataReader(path, classOf[SpRL2013Document])
    reader.readData()
    val sentences = reader.documents.asScala.flatMap { doc =>
          val sentenceOffsetList = getDocumentSentences(doc, version)
          sentenceOffsetList.map { case (sentence, offset) =>
            val ta = TextAnnotationFactory.createTextAnnotation(version, doc.getFilename + offset, sentence)
            new SpRLSentence(offset, ta.sentences.get(0), getRelations(doc, offset).asJava)
          }
        }.toList
```
`SpRLDataReader` is the xml data reader which loads data from the dataset. `getDocumentSentences` splits the sentences and returns a list of tuples. Each tuple contains a sentence and its character offset in the document. 
`TextAnnotationFactory.createTextAnnotation` uses the Illinois-NLP-Pipeline to generate feature rich `TextAnnotation` for each sentence. `getRelations` returns the relations that correspond to the current sentence. 


### Defining the `DataModel`
In order to identify spatial relations, the authors of [2] used a simple method.
A set of triples used to represent the spatial relations. They generated many candidates and trained a classifier to classify them. So here is the [`DataModel`](SpRLDataModel.scala) :
```scala
  // data model
  val sentences = node[SpRLSentence]
  val relations = node[SpRelation]
  val sentencesToRelations = edge(sentences, relations)
```
In this model, we have a set of sentences, each sentence connects to many relations through `sentencesToRelations` edges, which some of them can be `GOLD` or positive spatial relation and others that are `CANDIDATE` or non-spatial relations.

### Sensors
Next step is to determine sensors:
```scala
  // sensors
  sentenceToRelations.addSensor(SpRLSensors.sentencesToRelations _)
```
The [`sentenceToRelations`](SpRLSensors.scala) sensor, generates candidate relations from the specified sentence, using the method described in the paper.

### Features
Now we can specify the features, all features are constructed using `property` method of `DataModel`. First of all we specify the label that the classifier tries to predict:
```scala
  // classifier labels
  val relationLabel = property(relations) {
    x: SpRelation => x.getLabel.toString
  }
```

The paper used many features in order to achieve the desired performance. `TextAnnotation` makes feature extraction very easy, see [`SpRLDataModel`](SpRLDataModel.scala) for detailed implementations.

### Classification
The paper used a SVM classifier for relation classification. Defining this classifier is straightforward using Saul:

```scala
  val relationFeatures = List(JF2_1, JF2_2, JF2_3, JF2_4, JF2_5, JF2_6, JF2_7, JF2_8,
    JF2_9, JF2_10, JF2_11, JF2_12, JF2_13, JF2_14, JF2_15, BH1)
  object relationClassifier extends Learnable[SpRelation](relations) {
    override lazy val classifier = new SupportVectorMachine()
    def label: Property[SpRelation] = relationLabel
    override def feature = using(relationFeatures)
  }
```
We extend `Learnable` class of Saul and specify the type of classifier we want. Next the target label for classification is determined by implementing `label` property and finally the set of features needed for classification is provided.
You can find this implementation in [`SpRLClassifiers`](SpRLClassifiers.scala)

## Configurations
All configurations needed to run this application are placed in 
[`SpRLConfigurator`](../../../../../../../../java/edu/illinois/cs/cogcomp/saulexamples/nlp/SpatialRoleLabeling/SpRLConfigurator.java). In order to run the application for training, set `IsTraining` to `Configurator.TRUE` and for testing set it to `Configurator.FALSE`. 

## Test results for SemEval 2012 data
The SemEval 2012 data-set is a subset of IAPR TC-12 Benchmark. 
The spatial relations labeled as **GOLD** and non-spatial relations as **CANDIDATE**. 
<pre>  
 Label   Precision Recall   F1   LCount PCount
-----------------------------------------------
CANDIDATE    99.700 99.844 99.772 149715 149932
GOLD         64.697 48.689 55.563    877    660
-----------------------------------------------
Accuracy     99.546   -      -      -    150592
</pre>


## Running
To run the main app with default properties:

```
sbt "project saulExamples" "runMain edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLApp"
```

## References
[1] Kordjamshidi, Parisa, Steven Bethard, and Marie-Francine Moens. "SemEval-2012 task 3: Spatial role labeling." Proceedings of the First Joint Conference on Lexical and Computational Semantics. Association for Computational Linguistics, 2012.

[2] Roberts, Kirk, and Sanda M. Harabagiu. "UTD-SpRL: A joint approach to spatial role labeling." Proceedings of the First Joint Conference on Lexical and Computational Semantics. Association for Computational Linguistics, 2012.
