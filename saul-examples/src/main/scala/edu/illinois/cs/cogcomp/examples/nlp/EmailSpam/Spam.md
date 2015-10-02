The spam classifier example is a simple example designed to showcase a Binary text classification task in saul.

###Notes:

The Document class and the DocumentReader class are defined elsewhere.

The Document class consists of a document constructor that defines a document and it's contents
The Document reader consists of an iterator that iterates through all the documents using the constructor in the Document class and adds each document to an ArrayList.

###The data Model:

##Entities

The spam classifier simply has documents as one type of entity, so only a collection of Documents is defined.

```scala
object spamDataModel extends DataModel{
  import edu.illinois.cs.cogcomp.lfs.data_model.DataModel._

  val docs=node[Document]             //Collection of documents
  val NODES: List[Entity[_]] = ~~(docs)    // is the entity for this data model
```

##Features  and Properties

The properties of a document are it's body and it's label.
Two features are defined based on the content of the document.
A label is also defined.

#Bag of Words:
Simply a list of all the words in the document is returned.

```scala
 val wordFeature=discreteAttributesGeneratorOf[Document]('wordF){    //Discrete Feature of the document
  x:Document=> {
    val words: List[String] = x.getWords.toList                      //Get all of the words in a list
    words                                                            //and return it
   }
  }
```

#Bigram
A concatenation of 2 consecutive words (bigrams) at a time is returned, and the set of grams contained in a document are used as its features.    

```scala
val bigramFeature=discreteAttributesGeneratorOf[Document]('bigram) {

x:Document=>{

   val words = x.getWords.toList                                    //get all the words in a list
   var big:List[String]= List()                                     //create a new empty list called big
   for (i<- 0 until  words.size-1)                                  //loop through all the words in the word list
    big= (words.get(i)+"-"+words.get(i+1))::big                     //and concatenate successive words and add it to the list
   big                                                              //return the list
}
}
```
#SpamLabel
The label is defined here

```scala
val spamLable=discreteAttributeOf[Document]('label){
  x:Document=>{
    x.getLabel                                                      //The label obtained from the document through a getLabel function defined in the document class
  }
}
```

##Relationships and Feature List

A list of the features and relationships is defined here.
The two features are Bag of Words and Bigram, and there is no relationship between the entities

```scala
    val PROPERTIES: List[Attribute[_]] = List(wordFeature,bigramFeature) //bag of words and Bigram features
    val EDGES: List[Edge[_, _]] = Nil               //No relationship between the entities
```


###Classifier
The classifier is defined using the following construct for Spam Classification.
It is created as an object inside an object for modularity

```scala
object Classifiers {
  object spamClassifier extends Learnable[Document](spamDataModel) {   //Object extend learnable with entity Document using the Spam Data Model

    def label: Attribute[Document] = spamLable is "spam"               // the label we are training for, to check if it's spam or not using binary classification
  }
}
```

###Running and testing
To run and test, we create the following application. We use slices of the data to train and test.
We learn for 50 iterations of the training data and then test it on 10 data from the same slice.


```scala
object Spamapp {

  def main(args: Array[String]): Unit = {
  val dat:List[Document]=new DocumentReader("data/spam/train").docs.toList      //Defining the data and specifying it's location
  spamDataModel++ dat.slice(0,50)                                               //Adding a slice to the data model
  spamClassifier.learn(50)                                                      //learning with 50 iterations
  spamDataModel.testWith(dat.slice(60,70))                                      //Adding 10 more data as test data with "testWith"
  spamClassifier.test()                                                         //Running the test
  }
}
```

