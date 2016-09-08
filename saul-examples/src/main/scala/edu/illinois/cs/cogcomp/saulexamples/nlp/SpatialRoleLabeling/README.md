# Spatial Role Labeling
In paper [1], the task of spatial role labeling is introduced and an annotation scheme proposed that is language-independent and facilitates the application of machine learning techniques. The framework consists of a set of spatial roles. For a basic example, in the following sentence:


Give me [the gray book] (trajector) [on] (spatialindicator) [the big table] (landmark).


The phrase headed by the token book is referring to a trajector object, the phrase headed by the token table is referring to the role of a landmark and these are related by the spatial expression on denoted as spatial indicator. The spatial indicator (often a preposition) establishes the type of spatial relation.


Analogous to semantic role labeling, spatial role labeling is defined as the task of automatic labeling of words or phrases in a sentence with a set of spatial roles. More speciﬁcally, it involves identifying and classifying the spatial arguments of the spatial expressions mentioned in a sentence.


## SpRL using Saul
In this example, we implement the best performing algorithm for [task 3 of SemEval 2012](https://www.cs.york.ac.uk/semeval-2012/task3.html) proposed in [2] using Saul.
In order to identify spatial relations, the authors of [2] used a simple method.
A set of triples used to represent a spatial relations. They generated many candidates and trained a classifier to classify them. So here is the [`DataModel`](RobertsDataModel.scala) :
```scala
  // data model
  val sentences = node[SpRLSentence]
  val relations = node[RobertsRelation]
  val sentencesToRelations = edge(sentences, relations)
```
In this model, we have a set of sentences, each sentence connects to many relations through `sentencesToRelations` edges, which some of them can be `GOLD` or positive spatial relation and others that are `CANDIDATE` or non-spatial relations.
In order to use many easy to use features that `TextAnnotation` provides, we defined a container class `SpRLSentence` that contains `TextAnnotaiton.Sentence` . By doing so, we can access to many built-in NLP features.

Next step is to determine sensors:
```
  // sensors
  sentenceToRelations.addSensor(SpRLSensors.sentencesToRelations _)
```
The [`sentenceToRelations`](SpRLSensors.scala) sensor, generates candidate relations from the specified sentence, using the method described in the paper.

Now we can specify the features, all features are constructed using `property` method of `DataModel`. First of all we specify the label that the classifier tries to predict:
```
  // classifier labels
  val relationLabel = property(relations) {
    x: RobertsRelation => x.getLabel.toString
  }
```

The paper used many features in order to achieve the desired performance. `TextAnnotation` makes feature extraction very easy, see [Feature Extraction](FeatureExtraction.md) for detailed explanation.


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
