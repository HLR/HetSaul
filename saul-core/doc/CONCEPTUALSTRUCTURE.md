# Conceptual Structure

Each Saul program is a general purpose Scala program in which the Saul DSL high level constructs are used to design intelligent applications.
The provided constructs are designed to enable declarative programing for the following conceptual
components of each application that uses learning and inference.

## Data Model 
The data model in Saul conceptually is represented with a graph containing nodes, the edges between them and their properties.

  - `Node`: The different types of objects, for example documents, sound files, pictures, text documents, etc.
  - `Edge`: In a graph with nodes of type `Node`, their connections can be defined with `Edge`s.
  - `Property`: The attributes of a node, for example a node of type `Document` can have properties
  such as `Title`, `Subject`, `Author`, `Body`, etc.

### Defining nodes

This is done using the `node` function,

```scala
val tokens = node[ConllRawToken]
val relations = node[ConllRawRelation]
```

This line of code defines an entity of type `ConllRawToken` and names it as `tokens`.

### Defining properties

This is done via the `property` function,

```scala
val pos = property(token) {
   (t: ConllRawToken) => t.POS
}
```

In this definition `pos` is defined to be a property of nodes of type token. The definition
inside `{ .... }` is  the definition of a sensor which given an object of type `ConllRawToken` i.e. the tye of node and
generates an output property value (in this case, using the POS tag of an object of type `ConllRawToken`).

If the content of a property is computationally intensive to compute, you can cache its value, by setting `cache` to be
`true`: 
```scala
val pos = property(token, cache = true) {
   (t: ConllRawToken) => t.POS
}
```

The first time that a property is called with a specific value, it would you remember the corresponding output, 
so next time it just looks up the value from the cache. 

Note that when training, the property cache is remove between two training interation in order not to interrupt 
the trainng procedure. 

#### Parameterized properties 
Suppose you want to define properties which get some parameters; this can be important when we want to programmatically 
define many properties which differ only in some parameters. Here are two example properties which differ slightly: 

```scala
val matchWordING = property(token) {
   (t: ConllRawToken) => t.rawString.contains("ing")
}

val matchWordTION = property(token) {
   (t: ConllRawToken) => t.rawString.contains("tion")
}
```

One matches for existence of "ing" and the other one checks for existence of "tion". Since they are almost the same, 
we can combine them as a parameterized property, by adding a parameter `(param: String) =>` before the property definition: 

```scala 
val mathchWord = (param: String) => property(token) {
   (t: ConllRawToken) => t.rawString.contains(param)
}
```

Note that there is no limitation on the number/types of the extra parameters passed to properties. 

**Caution:** never define a property with keyword `def`, and instead define it as `val` (as shown in the examples above).  

### Defining edges

This is done via several constructs depending on the type of the relationships. Here is an example definition,

```scala
val tokenSentenceEdge = edge(tokens, relations)
```

This definition creates edges between the two `Node`s we defined previously.

### Sensors

#### Sensors for properties

   As mentioned above in the body of property definition an arbitrary sensor can be called.
   `(t: ConllRawToken) => t.POS`
   This will return a primitive data type i.e. String, real, etc.

#### Sensors for edges

   Defining the sensors on edges is a very important step to make the whole graph and the necessary connections.
   Conceptually there are two types of sensors:
   1) Generators : They get nodes of type `T` and generated nodes of type `U`, and during the generation establish an automatic connection between the instances of type `T` to the instances of type `U`.
    See this example which adds a generating sensor to an edge:
    `e2.addSensor((s: String) => s.toUpperCase)`

   2) Matching : They get nodes of type `T` and type `U` and evaluate a boolean expression over every pair, if the expression is true a connection will be established.
   See this example which adds a matching sensor to an edge:

   `e1.addSensor(_.charAt(0) == _.charAt(0))`


## Instantiation of Data Model
TODO

## Graph Queries
TODO

## Classifiers
Here are the basic types essential for using classifiers.

  - `Label`: The "category" of the one object. For example, in a classification task, the category
  of one text  document can be related to its topic, e.g. Sport, politics, etc.
  - `Features`: A set of properties of object that is used for the classifiers to be trained based on
  those, for example the set of words that occur in a document can be used as feature s of that document (Bag of words).
  - `Parameters`: Variables used to fine tune the classifier. It differs from one type of classification method to another.

A classifier can be defined in the following way:

```scala
object OrgClassifier extends Learnable[ConllRawToken](ErDataModelExample) {
  override def label: Property[ConllRawToken] = entityType is "Org"

  override def feature = using(word, phrase, containsSubPhraseMent, containsSubPhraseIng,
    containsInPersonList, wordLen, containsInCityList)
}
```

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
val PersonWorkFor=ConstraintClassifier.constraintOf[ConllRelation] {
  x:ConllRelation => {
    ((workForClassifier on x) isTrue) ==> ((PersonClassifier on x.e1) isTrue)
  }
}
```

## Constrained Classifiers
A constrained classifier can be defined in the following form:

```scala
object LocConstraintClassifier extends ConstraintClassifier[ConllRawToken, ConllRelation](ErDataModelExample, LocClassifier) {

  def subjectTo = Per_Org

  override val pathToHead = Some('containE2)
  //    override def filter(t: ConllRawToken,h:ConllRelation): Boolean = t.wordId==h.wordId2
}
```

## Running Applications and Evaluation
* Training and Prediction Paradigms:
* Running Applications and Evaluation: application is a program that uses the declared classifiers and acts upon them: train them, test them or use them in predictions for further analysis in the program.

The following is the usual construct in the application program:

```scala
object exampleApp {
  def main(args: Array[String]): Unit = {
    val TrainData: List[Post] = new ExampleDataReader("PathToTrainData").VariableOfdata.toList
    val TestData: List[Post] = new ExampleDataReader("data/20news/20news.test.shuffled").VariableOfDate.toList

    /** Add the training data to the data model */
    newsGroupDataModel.populate(TrainData)
    /** Learn, given the number of Training iterations */
    newsClassifer.learn(40)
    /** Add the testing data */
    newsGroupDataModel.testWith(dat2)
    /** Run evaluation on the test data*/
    newsClassifer.test()
  }
}
```


### Logging 
We have logging inside Saul core to keep track of its states. We use the
following standard logging levels: 

 - off: no logging. 
 - error: runtime errors or unexpected conditions. 
 - warn: suspicious behavior. 
 - info: important run-time behavior. 
 - trace: most detailed information.
 - debug: detailed information on the flow through the system.  

If you would like to set change the logging level for a specific class, 
use the following pattern:

```scala 
loggerConfig.Logger("PACKAGE-NAME").setLevel(LEVEL-NAME)
```

For example: 

```scala 
loggerConfig.Logger("edu.illinois.cs.cogcomp.saul.classifier.Learnable").setLevel(Level.ERROR)
```

To keep the default behavior less verbose, the default logging level is `info`. 
If you want to keep track of the changes and settings in your program, 
 you can use the logging provided in Saul. For that, you can just extend 
 your class or object with the `Logging` trait, and make calls to its 
 `logger` object.
 For example 
 ```scala 
 logger.debug("This is a log") 
 ```
 
