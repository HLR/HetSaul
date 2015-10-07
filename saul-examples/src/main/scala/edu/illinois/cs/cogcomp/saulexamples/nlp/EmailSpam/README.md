The spam classifier example is a simple example designed to showcase a binary text classification task in saul.

 - Notes: The `Document` and `DocumentReader` classes are defined elsewhere.

The `Document` class consists of a document constructor that defines a `Document` and it's contents
The document reader consists of an iterator that iterates through all the documents using the constructor in the `Document` class and adds each document to an `ArrayList`.

#The data Model:

##Entities

The spam classifier simply has `Document`s as one type of nodes, so only a collection of `Document`s is defined.

```scala
object spamDataModel extends DataModel{
  val docs=node[Document]     //Collection of documents
}
```

##Features and Properties

The properties of a document are its body and its label.
Two features are defined based on the content of the document.
A label is also defined.

###Bag of Words:
Simply a list of all the words in the document is returned.

```scala
 val wordFeature=discreteAttributesGeneratorOf[Document]('wordF){    //Discrete Feature of the document
    x:Document=> {
      val words: List[String] = x.getWords.toList                      //Get all of the words in a list
      words                                                            //and return it
    }
  }
```

###Bigram
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
###SpamLabel
The label is defined here

```scala
val spamLable=discreteAttributeOf[Document]('label){
  x:Document=>{
    x.getLabel                                                      //The label obtained from the document through a getLabel function defined in the document class
  }
}
```

##Classifier
The classifier is defined using the following construct for Spam Classification.
It is created as an object inside an object for modularity

```scala
object classifiers {
  object spamClassifier extends Learnable[Document](spamDataModel) {   //Object extend learnable with entity Document using the Spam Data Model

    def label: Attribute[Document] = spamLable is "spam"               // the label we are training for, to check if it's spam or not using binary classification
  }
}
```

##Running and testing
To run and test, we create the following application. We use slices of the data to train and test.
We learn for 50 iterations of the training data and then test it on 10 data from the same slice.


```scala
object spamApp {

  def main(args: Array[String]): Unit = {
  val dat:List[Document]=new DocumentReader("data/spam/train").docs.toList      //Defining the data and specifying it's location
  spamDataModel++ dat.slice(0,50)                                               //Adding a slice to the data model
  spamClassifier.learn(50)                                                      //learning with 50 iterations
  spamDataModel.testWith(dat.slice(60,70))                                      //Adding 10 more data as test data with "testWith"
  spamClassifier.test()                                                         //Running the test
  }
}
```

