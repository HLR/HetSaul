## Learners
Learners get an input and make a prediction. It can be a classifier, a regression (which are both trained) or any rule-based system. 
Here are the basic parameters used definition of a learners:  

  - `label`: A `Property` which defines the output prediction. In case of classification 
    problems, it is the "category" of the one object. For example, in a document classification 
    task, the category of one text document can be related to its topic, e.g. Sport, politics, etc.
  - `feature`: A set of `Property`s fed to the learner as the input. For example in the case of classification, the classifiers will be trained based on such properties. Example properties are set of words that occur in a document (Bag of words).
  - `classifier`: defines the type of the learning algorithm used. 

Here is an example classifier:

```scala
object OrgClassifier extends Learnable[ConllRawToken](ErDataModelExample) {
  def label: Property[ConllRawToken] = entityType is "Org"
  override def feature = using(word, phrase, containsSubPhraseMent, containsSubPhraseIng,
    containsInPersonList, wordLen, containsInCityList)
  override lazy val classifier = new SparseNetworkLearner()
}
```
### Training and Testing classifiers

-Call `train()` method to train your classifier using the populated data in the data model's training instances:

```scala
OrgClassifier.learn(numberOfIterations)
```
where number of iteration determines how many times the training algorithm should iterate over training data.

-Call `test()` method to test your classifier using the populated data model's test instance:

```scala
OrgClassifier.test()
```

### Availale algorithms 
Here is a list of available algorithms in Saul:
 - [LBJava learning algorithms](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/doc/ALGORITHMS.md) 
 - [Weka learning algorithms](https://github.com/IllinoisCogComp/saul/blob/master/saul-core/src/main/java/edu/illinois/cs/cogcomp/saul/learn/SaulWekaWrapper.md)


### Saving and loading classifiers
 Simply call the `save()` method:

```scala
OrgClassifier.save()
```

By default the classifier will be save into two files (a `.lc` model file and a `.lex` lexicon file). In order to
 save the classifier in another location, you can set the location in parameter `modelDir`; for example:

```scala
OrgClassifier.modelDir = "myFancyModels/"
OrgClassifier.save()
```

This will save the two model files into the directory `myFancyModels`.

To load the models you can call the `load()` method.

```scala
OrgClassifier.load()
```

If you have different versions of the same classifier (say, different features, different number of iterations, etc),
you can add a suffix to the model files of each variation:

```scala
OrgClassifier.modelSuffix = "20-iterations"
OrgClassifier.save()
```

This would add the suffix "20-iterations" to the files of the classifier at the time of saving them. Note that at
the time of calling `load()` method it will look for model files with suffix "20-iterations".

## Constraints
A "constraint" is a logical restriction over possible values that can be assigned to a number of variables;
For example, a binary constraint could be `{if {A} then NOT {B}}`.
In Saul, the constraints are defined for the assignments to class labels.
A constraint classifiers is a classifier that predicts the class labels with regard to the specified constraints.

This is done with the following construct

```scala
val PersonWorkFor=ConstraintClassifier.constraintOf[ConllRelation]
 {
  x:ConllRelation =>
  {
    ((workForClassifier on x) isTrue) ==> ((PersonClassifier on x.e1) isTrue)
  }
 }
```

## Constrained Classifiers

A constrained classifier can be defined in the following form:

```scala
object LocConstraintClassifier extends ConstraintClassifier[ConllRawToken, ConllRelation](ErDataModelExample, LocClassifier)
 {
   def subjectTo = Per_Org
   override val pathToHead = Some('containE2)
   //override def filter(t: ConllRawToken,h:ConllRelation): Boolean = t.wordId==h.wordId2
 }
 ```