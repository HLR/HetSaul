# Saul Examples 

## Application examples

In this package two application examples in Saul are described. 
The list of the examples is listed bellow. To see more details on each example, click on its link to 
visit its README file. 

1. [Set Cover](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/setcover/README.md): The Set Cover problem which is a classical constraint programming 
problem. This example shows declarative first order constraint programming in Saul. The constraints 
are propositionalized and form an integer linear program (ILP) which are solved using Gurobi as our backend solver.
 Note that there is no training/learning involved in this example. 
 
2. [Entity-Relation Extraction](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/EntityMentionRelation/README.md): The entity-relation extraction task through 
which designing various training and prediction configurations are exemplified. 
One can see how local, global and pipeline configurations are designed, used and evaluated in Saul.

3. [Spam Classification](src/main/scala/edu/illinois/cs/cogcomp/saulexamples/nlp/EmailSpam/README.md): A third example which is a binary classification task 
to classify text documents as either Spam or Not Spam was also created.

* Note: Examples are under active development. 

## Prerequisites for this examples package 

* JDK 1.7 or Higher
* Saul 
* Gurobi (required for constrained inference)

## Conceptual Structure

Each Saul program is a general purpose Scala program in which Saul DSL provides a number of high level constructs to design application programs at a high level and declaratively.
The provided constructs are designed to enable programing for the following conceptual components of each application that uses learning and inference.

### Data Model: 
The data model in Saul conceptually is represented with a graph containing nodes, edges, 
and their properties.

  -`Node`: The graph contains different types of nodes to represent various data elements i.e. objects. For example nodes can represent documents, sound files, pictures, text documents, etc.
  -`Edge`: The `Node`s in the graph are connected to each other via `Edge`s.
  -`Property`: The nodes can have properties, for example a node of type `Document` can have properties
  such as `Title`, `Subject`, `Author`, `Body`, etc.  
  
#### Defining nodes

This is done using the `node` function,

```scala
val tokens = node[ConllRawToken](
  PrimaryKey = {
     t : ConllRawToken => String.valueOf(t.sentId) + ":" +String.valueOf(t.wordId)
  }
)
```

This line of code defines a node of type `ConllRawToken` and names it as 'tokens' and defines a 'primary key' for it based on the original variables in the original ConllRawToken class.

#### Defining properties
This is done via several constructs depending on the type of the feature for discrete features for example the `propertyOf` is used,

```scala
val pos = propertyOf[ConllRawToken]('pos) {
   (t: ConllRawToken) => t.POS :: Nil
}
  ```
This line of code defines a feature vector that is generated using the pos-tag of the original class of the entitiy that this property is assigned to (that is ConllRawToken class). The type of the feature is discrete. 
The list of possibilities for other types of feature functions are listed in:  `edu.illinois.cs.cogcomp.lfs.data_model.properties.features._`. 

#### Defining edges 

This is done via several constructs depending on the type of the relationships (see Saul paper). For example for 1:n relations, 
"oneToManyRelationOf" is used,

```scala
val RelationToPer = edge[ConllRelation,ConllRawToken]('containE1)('sid === 'sid, 'e1id === 'wordid)
```

Each pair of token (with original class: ConllRelation) contains two tokens (with original type ConllRawToken) and those two tokens are found using the keys of the contained tokens. 

### Classifiers
Here are the basic types essential for using classifiers. 

  - `Label`: The "category" of the one object. For example, in a classification task, the category of one text  document can be related to its topic, e.g. Sport, politics, etc. 
  - `Features`: A set of properties of object that is used for the classifiers to be trained based on those, for example the set of words that occur in a document can be used as feature s of that document (Bag of words). 
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

object EXAMPLEapp {
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
