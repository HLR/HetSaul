# Saul Examples 

## Application examples

In this package two application examples in Saul are described. 
The list of the examples is listed bellow. To see more details on each example, click on its link to 
visit its README file. 

1. [Set Cover](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/setcover/README.md): 
The Set Cover problem which is a classical constraint programming 
problem. This example shows declarative first order constraint programming in Saul. The constraints 
are propositionalized and form an integer linear program (ILP) which are solved using Gurobi as our backend solver.
 Note that there is no training/learning involved in this example. 
 
2. [Entity-Relation Extraction](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/EntityMentionRelation/README.md): 
The entity-relation extraction task through 
which designing various training and prediction configurations are exemplified. 
One can see how local, global and pipeline configurations are designed, used and evaluated in Saul.

3. [Spam Classification](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/EmailSpam/README.md): 
A third example which is a binary classification task 
to classify text documents as either Spam or Not Spam was also created.

4. [Semantic Role Labeling](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/README.md): 
a task in natural language processing consisting of the detection of the semantic arguments associated with the predicate or verb of a sentence and their classification into their specific roles.

* Note: Examples are under active development. 

## Prerequisites for this examples package 

* JDK 1.7 or Higher
* Saul 
* Gurobi (required for constrained inference)

## Conceptual Structure

Each Saul program is a general purpose Scala program in which Saul DSL provides a number of high level 
constructs to design application programs at a high level. high level and declaratively. 
The provided constructs are design to enable programing for the following conceptual 
components of each application that uses learning and inference.

### Data Model: 
The data model in Saul conceptually is represented with a graph containing nodes, edges, 
and their properties. Defining entities with the following constructs:

  - `Node`: The different types of objects, for example documents, sound files, pictures, text documents, etc.
  - `Edge`: In a graph with nodes of type `Node`, their connections can be defined with `Edge`s. 
  - `Property`: The attributes of a node, for example a node of type `Document` can have properties 
  such as `Title`, `Subject`, `Author`, `Body`, etc.  
  
#### Defining nodes

This is done using the `node` function, 

```scala
val tokens = node[ConllRawToken]
val relations = node[ConllRawRelation]
```

This line of code defines an entity of type `ConllRawToken` and names it as `tokens`.

#### Defining properties
This is done via the `property` function, 

```scala
val pos = property[ConllRawToken]("pos") {
   (t: ConllRawToken) => t.POS 
}
  ``` 
  
In this definition `"pos"` is the name of the property and it can be chosen arbitrarily. The definition 
inside `{ .... }` is  the definition of a sensor which given an object of type `ConllRawToken`
generates an output property value (in this case, using the POS tag of an object of type `ConllRawToken`). 



#### Defining edges 

This is done via several constructs depending on the type of the relationships. Here is an example definition, 

```scala
val tokenSentenceEdge = edge(tokens, relations)
```

This definition creates edges between the two `Node`s we defined previously.

#### Sensors
   ##### Sensors for properties

   As mentioned above in the body of property definition an arbitrary sensor can be called.
   `(t: ConllRawToken) => t.POS`
   This will return a primitive data type i.e. String, real, etc.

   ##### Sensors for edges

   Defining the sensors on edges is a very important step to make the whole graph and the necessary connections.
   Conceptually there are two types of sensors:
   1) Generators : They get nodes of type `T` and generated nodes of type `U`, and during the generation establish an automatic connection between the instances of type `T` to the instances of type `U`.
    See this example which adds a generating sensor to an edge:
    `e2.addSensor((s: String) => s.toUpperCase)`

   2) Matching : They get nodes of type `T` and type `U` and evaluate a boolean expression over every pair, if the expression is true a connection will be established.
   See this example which adds a matching sensor to an edge:

   `e1.addSensor(_.charAt(0) == _.charAt(0))`


### Instantiation Data Model 
TODO

### Graph Queries 
TODO

### Classifiers
Here are the basic types essential for using classifiers. 

  - `Label`: The "category" of the one object. For example, in a classification task, the category 
  of one text  document can be related to its topic, e.g. Sport, politics, etc. 
  - `Features`: A set of properties of object that is used for the classifiers to be trained based on 
  those, for example the set of words that occur in a document can be used as feature s of that document (Bag of words). 
  - `Parameters`: Variables used to fine tune the classifier. It differs from one type of classification method to another. 

A classifier can be defined in the following way: 

```scala
object orgClassifier extends Learnable[ConllRawToken](ErDataModelExample) {
  override def label: Property[ConllRawToken] = entityType is "Org"

  override def feature = using(word, phrase, containsSubPhraseMent, containsSubPhraseIng,
    containsInPersonList, wordLen, containsInCityList)
}
```

### Constraints 
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
### Constrained Classifiers
A constrained classifier can be defined in the following form: 

```scala
object LocConstraintClassifier extends ConstraintClassifier[ConllRawToken, ConllRelation](ErDataModelExample, LocClassifier) {

  def subjectTo = Per_Org

  override val pathToHead = Some('containE2)
  //    override def filter(t: ConllRawToken,h:ConllRelation): Boolean = t.wordId==h.wordId2
}
```

### Running Applications and Evaluation
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
