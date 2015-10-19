# Email Spam Classification 
The spam classifier example is a simple example designed to showcase a binary text classification task in Saul.

The `Document` class consists of a document constructor that defines a `Document` and its contents
The document reader consists of an iterator that iterates through all the documents using the constructor in the `Document` class and adds each document to an `ArrayList`.

 - Notes: The `Document` and `DocumentReader` classes are defined elsewhere.

##The data Model:

###Entities

The spam classifier simply has `Document`s as one type of nodes, so only a collection of `Document`s is defined.

```scala
object spamDataModel extends DataModel{
  /** Collection of documents */ 
  val docs = node[Document]     
}
```

###Features and Properties

The properties of a document are its body and its label.
Two features are defined based on the content of the document.
A label is also defined.

####Bag of Words:
Simply a list of all the words in the document is returned.

```scala
 /** Discrete Feature of the document */ 
 val wordFeature = property[Document]('wordF){    
    /** Get all of the words in a `Document` and return it */ 
    x: Document => x.getWords.toList 
  }
```

####Bigram
A concatenation of 2 consecutive words (bigrams) at a time is returned, and the set of grams contained in a document are used as its features.    

```scala
  val bigramFeature = property[Document]('bigram) {
    x: Document => 
      val words = x.getWords.toList
      /** bigram features */
      words.sliding(2).map(_.mkString("-")).toList
  }
```
####SpamLabel
The label is defined here

```scala
val spamLable = property[Document]('label){
   /** The label obtained from the document through a getLabel function defined in the document class */ 
  x: Document => x.getLabel
}
```

###Classifier
The classifier is defined using the following construct for Spam Classification.
It is created as an object inside an object for modularity

```scala
object classifiers {
  /** Object extend `Learnable` with entity `Document` using the Spam Data Model */ 
  object spamClassifier extends Learnable[Document](spamDataModel) {   

    /** the label we are training for, to check if it's spam or not using binary classification */ 
    def label: Property[Document] = spamLable is "spam"               
  }
}
```

## Running and testing
To run and test, we create the following application. We use slices of the data to train and test.
We learn for 50 iterations of the training data and then test it on 10 data from the same slice.


```scala
object spamApp {

  def main(args: Array[String]): Unit = {
   /** Defining the data and specifying it's location  */ 
   val dat:List[Document] = new DocumentReader("data/spam/train").docs.toList      
   
   /** Adding a slice to the data model */ 
   spamDataModel.docs populate dat.slice(0,50)                                               
   
   /** learning with 50 iterations */ 
   spamClassifier.learn(50)                                                      
   
   /** Adding 10 more data as test data with "testWith" */ 
   spamDataModel.testWith(dat.slice(60,70))                                      
   
   /** Running the test */ 
   spamClassifier.test()                                                         
  }
}
```

