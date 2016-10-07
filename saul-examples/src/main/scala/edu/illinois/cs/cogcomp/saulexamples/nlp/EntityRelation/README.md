# Entity-Relation classification 

This is the problem of recognizing the `kill (KFJ, Oswald)` relation in the sentence

 "J. V. Oswald was murdered at JFK after his assassin, R. U. KFJ..."

 This task requires making several local decisions, such as identifying named entities in the sentence, in order to support the
 relation identification. For example, it may be useful to identify that `Oswald` and `KFJ` are
 people, and `JFK` is a location. This, in turn, may help to identify that the kill action is
  described in the sentence. At the same time, the relation `kill` constrains its arguments to
 be people (or at least, not to be locations) and helps to enforce that `Oswald` and `KFJ` are likely to be people, while `JFK` is not.

The problem is defined in terms of a collection of discrete random variables representing 
binary relations and their arguments; we seek an optimal assignment to the variables in 
the presence of the constraints on the binary relations between variables and the relation 
types. To read more on the formulation of the problem please refer to [1]. 

One important goal in this task is to show the training with different paradigms, which are 
described in the next section.

The type of entities that we extract in this example are E={Person, Location, Organization} and the typ of relations are R={WorksFor, LivesIn, LocatedIn, OrgBasedIn}.
We evaluate the current systems on the Conll-2004 data. We use 2942 train sentences (6955 entities and 1079 relations) and 2573 test sentences (7219 entities and 969 relations).

## Training and inference paradigms

In this example we show how to model this problem and show different training/inference paradigms 
for this problem. Also there is a summary of the performances for our main classifiers: 

 - Independent Training: In this scenario the entity and relation classifiers, are trained independently using only 
            local features. In particular, the relation classifier does not know the labels of its entity arguments,
            and the entity classifier does not know the labels of relations in the sentence either. 
            Here is a summary of performances for independent entity classifiers: 
             
```
        ==============================================
        Person Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.728 99.706 99.214  61178  61784
        true        86.994 60.503 71.369   1990   1384
        ----------------------------------------------
        Accuracy    98.471   -      -      -     63168
        ==============================================
        Organization Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.666 99.798 99.229  61896  62606
        true        77.758 34.355 47.655   1272    562
        ----------------------------------------------
        Accuracy    98.480   -      -      -     63168
        ==============================================
        Location Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.334 99.676 99.000  60760  61589
        true        87.524 57.392 69.325   2408   1579
        ----------------------------------------------
        Accuracy    98.064   -      -      -     63168
        ==============================================
```

And the summary of performances for independent relation classifiers:

```
         ==============================================
         WorksFor Classifier Evaluation
          Label   Precision Recall   F1   LCount PCount
         ----------------------------------------------
         false       91.317 71.765 80.369    850    668
         true        20.266 51.261 29.048    119    301
         ----------------------------------------------
         Accuracy    69.247   -      -      -       969
         ==============================================
         LivesIn Classifier Evaluation
          Label   Precision Recall   F1   LCount PCount
         ----------------------------------------------
         false       78.524 86.127 82.150    692    759
         true        54.286 41.155 46.817    277    210
         ----------------------------------------------
         Accuracy    73.271   -      -      -       969
         ==============================================
         LocatedIn Classifier Evaluation
          Label   Precision Recall   F1   LCount PCount
         ----------------------------------------------
         false       99.779 99.889 99.834    903    904
         true        98.462 96.970 97.710     66     65
         ----------------------------------------------
         Accuracy    99.690   -      -      -       969
         ==============================================
         OrgBasedIn Classifier Evaluation
          Label   Precision Recall   F1   LCount PCount
         ----------------------------------------------
         false       97.067 99.726 98.378    730    750
         true        99.087 90.795 94.760    239    219
         ----------------------------------------------
         Accuracy    97.523   -      -      -       969
         ==============================================
```

 - Pipeline Training: Pipeline, mimics the typical strategy in solving complex natural language problems – separating a 
            task into several stages and solving them sequentially. For example, a named entity recognizer may be 
            trained using a different corpus in advance, and given to a relation classifier as a tool to extract 
            features. This approach first trains an entity classifier, and then uses the prediction of entities in 
            addition to other local features to learn the relation identifier. Note that although the true labels of 
            entities are known when training the relation identifier, this may not be the case in the testing time since 
            only the predicted entity labels are available in testing, learning on the predictions of the entity 
            classifier presumably makes the relation classifier more tolerant to the mistakes of the entity classifier. 
            In fact, we also observe this phenomenon empirically. When the relation classifier is trained using the 
            true entity labels, the performance is much worse than using the predicted entity labels.
            Here is a summary of pipeline relation classifiers: 
```
        ==============================================
        WorksFor Pipeline Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       93.740 72.235 81.595    850    655
        true        24.841 65.546 36.028    119    314
        ----------------------------------------------
        Accuracy    71.414   -      -      -       969
        ==============================================
        LivesIn Pipeline Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       77.060 87.861 82.107    692    789
        true        53.333 34.657 42.013    277    180
        ----------------------------------------------
        Accuracy    72.652   -      -      -       969
        ==============================================
```
            
 - Learning Plus Inference (L+I): In the scenario the classifiers are learned independently but at the test time we use 
            constrained inference to maintain structural consistency. We device constraints to relate independent entity 
            and independent relations together. Here is a summary of the evaluations: 
             
```
        ==============================================
        Person Classifier Evaluation with training
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.761 99.703 99.230  61178  61761
        true        87.065 61.558 72.122   1990   1407
        ----------------------------------------------
        Accuracy    98.501   -      -      -     63168
        ==============================================
        Organization Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.681 99.615 99.146  61896  62482
        true        65.306 35.220 45.761   1272    686
        ----------------------------------------------
        Accuracy    98.319   -      -      -     63168
        ==============================================
        Location Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.375 99.617 98.992  60760  61527
        true        85.801 58.472 69.548   2408   1641
        ----------------------------------------------
        Accuracy    98.048   -      -      -     63168
        ==============================================
        WorkFor Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       90.530 84.353 87.333    850    792
        true        24.859 36.975 29.730    119    177
        ----------------------------------------------
        Accuracy    78.535   -      -      -       969
        ==============================================
        LivesIn Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       76.347 94.220 84.347    692    854
        true        65.217 27.076 38.265    277    115
        ----------------------------------------------
        Accuracy    75.026   -      -      -       969
        ==============================================
```           
            
 - Joint Training: We seek to train classifiers so they will produce the correct global classification. To
                  this end, the key difference from the other approach is that here, feedback from the inference process 
                  determines which classifiers to train so that together, the classifiers and the inference procedure 
                  yield the desired result. 
```
        ==============================================
        Person Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.804 99.696 99.248  61178  61730
        true        87.065 62.915 73.046   1990   1438
        ----------------------------------------------
        Accuracy    98.537   -      -      -     63168
        ==============================================
        Organization Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.692 99.573 99.131  61896  62449
        true        63.282 35.770 45.706   1272    719
        ----------------------------------------------
        Accuracy    98.289   -      -      -     63168
        ==============================================
        Location Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.498 99.516 99.004  60760  61388
        true        83.483 61.711 70.965   2408   1780
        ----------------------------------------------
        Accuracy    98.075   -      -      -     63168
        ==============================================
        WorkFor Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       91.711 80.706 85.857    850    748
        true        25.792 47.899 33.529    119    221
        ----------------------------------------------
        Accuracy    76.677   -      -      -       969
        ==============================================
        LivesIn Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       77.904 89.162 83.154    692    792
        true        57.627 36.823 44.934    277    177
        ----------------------------------------------
        Accuracy    74.200   -      -      -       969
        ==============================================
```



## Testing the Entity Classifier interactively

For a quick demo of the Entity Type Classifier, you can run the following command in the project's root folder:

```shell
sbt "project saulExamples" "runMain edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationApp"
```

## Using it in your code

Add Sual as your dependency in your maven: 

```xml
    <repositories>
        <repository>
            <id>CogcompSoftware</id>
            <name>CogcompSoftware</name>
            <url>http://cogcomp.cs.illinois.edu/m2repo/</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>edu.illinois.cs.cogcomp</groupId>
            <artifactId>saul-examples</artifactId>
            <version>0.1</version>
        </dependency>
    </dependencies>
```

or in your sbt: 

```
  resolvers += "CogcompSoftware" at "http://cogcomp.cs.illinois.edu/m2repo/"
  libraryDependencies += "edu.illinois.cs.cogcomp" % "saul-examples_2.11" % "0.1"
```

And then call the classifiers: 
```scala
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ConllRawSentence, ConllRawToken}
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers.{LocationClassifier, PersonClassifier}

import scala.collection.JavaConverters._

object SampleObject {
  def main(args: Array[String]): Unit = {
    val tokens = List("Yesterday", "dumb", "Bob", "destroyed", "my", "fancy", "iPhone",
      "in", "beautiful", "Denver", ",", "Colorado", ".")
    val pos = List("NN", "JJ", "NNP", "VBD", "PRP$", "JJ", "NN", "IN", "JJ", "NNP", ",", "NNP", ".")
    val sentence = new ConllRawSentence(0)
    tokens.zipWithIndex.foreach{ case (tok, idx) =>
        val conllTok = new ConllRawToken()
        conllTok.setPhrase(tok)
        conllTok.setPOS(pos(idx))
        sentence.addTokens(conllTok)
    }

    // populating the data in the data model
    EntityRelationDataModel.sentences.populate(List(sentence), train = false)

    // loading the entity models
    EntityRelationClassifiers.loadIndependentEntityModels()
    println("** Persons: ")
    sentence.sentTokens.asScala.foreach{ tok => if(PersonClassifier(tok) == "true") println(tok.phrase) }
    println("** Locations: ")
    sentence.sentTokens.asScala.foreach{ tok => if(LocationClassifier(tok) == "true") println(tok.phrase) }

    // load the relation models
    EntityRelationClassifiers.loadIndependentRelationModels()
    EntityRelationClassifiers.loadPipelineRelationModels()
    // and make prediction using relation classifiers, just like how we did for entities above ... 
  }
}
```
 
## Further reading 
 
[1] D. Roth and W-t Yih. "A Linear Programming Formulation for Global Inference in Natural Language Tasks." In 
Proceedings of CoNLL-2004. 2004.

[2] M. Chang, L. Ratinov, and D. Roth. "Structured learning with constrained conditional models." Machine Learning,
    88(3):399–431, 6 2012.
    
