# Entity-Relation classification 

This is the problem of recognizing the `kill (KFJ, Oswald)` relation in the sentence "J. V. 
Oswald was murdered at JFK after his assassin, R. U. KFJ..." This task requires making several
local decisions, such as identifying named entities in the sentence, in order to support the 
relation identification. For example, it may be useful to identify that Oswald and KFJ are 
people, and JFK is a location. This, in turn, may help to identify that the kill action is 
described in the sentence. At the same time, the relation kill constrains its arguments to 
be people (or at least, not to be locations) and helps to enforce that Oswald and KFJ are 
likely to be people, while JFK is not.

The problem is defined in terms of a collection of discrete random variables representing 
binary relations and their arguments; we seek an optimal assignment to the variables in 
the presence of the constraints on the binary relations between variables and the relation 
types. To read more on the formulation of the problem please refer to [1]. 

One important goal in this task is to show the training with different paradigms, which are 
described in the next secion. 

We evaluate the current systems on the Conll-2004 data. We use 2942 train sentences (6955 entities and 1079 relations) 
and 2573 test sentences (7219 entities and 969 relations). 

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
        false       99.447 98.472 98.957  61178  60578
        true        63.900 83.166 72.271   1990   2590
        ----------------------------------------------
        Accuracy    97.989   -      -      -     63168
        ==============================================
        Organization Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       99.069 98.693 98.881  61896  61661
        true        46.317 54.874 50.234   1272   1507
        ----------------------------------------------
        Accuracy    97.811   -      -      -     63168
        ==============================================
        Location Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.985 98.823 98.904  60760  60661
        true        71.480 74.419 72.920   2408   2507
        ----------------------------------------------
        Accuracy    97.893   -      -      -     63168
        ==============================================
```
            
And the summary of performances for independent relation classifiers:

```
         ==============================================
         WorksFor Classifier Evaluation
          Label   Precision Recall   F1   LCount PCount
         ----------------------------------------------
         false       96.916 51.765 67.485    850    454
         true        20.388 88.235 33.123    119    515
         ----------------------------------------------
         Accuracy    56.244   -      -      -       969
         ==============================================
         LivesIn Classifier Evaluation
          Label   Precision Recall   F1   LCount PCount
         ----------------------------------------------
         false       76.319 85.694 80.735    692    777
         true        48.438 33.574 39.659    277    192
         ----------------------------------------------
         Accuracy    70.795   -      -      -       969
         ==============================================
         LocatedIn Classifier Evaluation
          Label   Precision Recall   F1   LCount PCount
         ----------------------------------------------
         false       98.899 99.446 99.172    903    908
         true        91.803 84.848 88.189     66     61
         ----------------------------------------------
         Accuracy    98.452   -      -      -       969
         ==============================================
         OrgBasedIn Classifier Evaluation
          Label   Precision Recall   F1   LCount PCount
         ----------------------------------------------
         false       99.174 98.630 98.901    730    726
         true        95.885 97.490 96.680    239    243
         ----------------------------------------------
         Accuracy    98.349   -      -      -       969
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
        false       93.608 77.529 84.813    850    704
        true        27.925 62.185 38.542    119    265
        ----------------------------------------------
        Accuracy    75.645   -      -      -       969
        ==============================================
        LivesIn Pipeline Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       78.062 83.815 80.836    692    743
        true        50.442 41.155 45.328    277    226
        ----------------------------------------------
        Accuracy    71.620   -      -      -       969
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
        false       99.724 98.875 99.297 124800 123738
        true        71.802 91.269 80.373   3917   4979
        ----------------------------------------------
        Accuracy    98.644   -      -      -    128717
        ==============================================
        Organization Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       99.538 99.090 99.314 126219 125650
        true        62.537 76.781 68.931   2498   3067
        ----------------------------------------------
        Accuracy    98.657   -      -      -    128717
        ==============================================
        Location Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       99.465 99.255 99.360 123952 123691
        true        81.635 86.107 83.812   4765   5026
        ----------------------------------------------
        Accuracy    98.769   -      -      -    128717
        ==============================================
        WorkFor Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       98.253 81.967 89.374   1647   1374
        true        55.935 94.015 70.140    401    674
        ----------------------------------------------
        Accuracy    84.326   -      -      -      2048
        ==============================================
        LivesIn Classifier Evaluation
         Label   Precision Recall   F1   LCount PCount
        ----------------------------------------------
        false       85.581 96.005 90.494   1527   1713
        true        81.791 52.591 64.019    521    335
        ----------------------------------------------
        Accuracy    84.961   -      -      -      2048
        ==============================================           
```           
            
 - Joint Training: We seek to train classifiers so they will produce the correct global classification. To
                  this end, the key difference from the other approach is that here, feedback from the inference process 
                  determines which classifiers to train so that together, the classifiers and the inference procedure 
                  yield the desired result. 
```
        Peop
        tp: 1914.0 fp: 480.0 tn: 63142.0 fn: 13.0 
         accuracy    0.99247890890784 
         precision   0.7994987468671679 
         recall      0.9932537623248573 
         f1          0.8859060402684564 
        Peop
        tp: 1915.0 fp: 484.0 tn: 63138.0 fn: 12.0 
         accuracy    0.9924331416192467 
         precision   0.7982492705293872 
         recall      0.9937727036844837 
         f1          0.8853444290337494 
        Org
        tp: 1194.0 fp: 242.0 tn: 64081.0 fn: 32.0 
         accuracy    0.9958199209751484 
         precision   0.8314763231197771 
         recall      0.9738988580750407 
         f1          0.8970698722764838 
        Org
        tp: 1196.0 fp: 242.0 tn: 64081.0 fn: 30.0 
         accuracy    0.9958504325008772 
         precision   0.8317107093184979 
         recall      0.9755301794453507 
         f1          0.8978978978978979 
        Loc
        tp: 2290.0 fp: 1015.0 tn: 62177.0 fn: 67.0 
         accuracy    0.9834932645806953 
         precision   0.6928895612708018 
         recall      0.9715740347899873 
         f1          0.8089014482515012 
        Loc
        tp: 2298.0 fp: 1015.0 tn: 62177.0 fn: 59.0 
         accuracy    0.9836153106836107 
         precision   0.693631150015092 
         recall      0.9749681798896903 
         f1          0.8105820105820106 
        Work_For
        tp: 281.0 fp: 54.0 tn: 743.0 fn: 1.0 
         accuracy    0.9490268767377201 
         precision   0.8388059701492537 
         recall      0.9964539007092199 
         f1          0.9108589951377634 
        Work_For
        tp: 282.0 fp: 1.0 tn: 796.0 fn: 0.0 
         accuracy    0.9990732159406858 
         precision   0.9964664310954063 
         recall      1.0 
         f1          0.9982300884955753 
        Live_In
        tp: 244.0 fp: 147.0 tn: 688.0 fn: 0.0 
         accuracy    0.8637627432808156 
         precision   0.6240409207161125 
         recall      1.0 
         f1          0.768503937007874 
        Live_In
        tp: 243.0 fp: 18.0 tn: 817.0 fn: 1.0 
         accuracy    0.9823911028730306 
         precision   0.9310344827586207 
         recall      0.9959016393442623 
         f1          0.9623762376237623 
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
    
