# Part-of-Speech Tagging (POSTagging)

Part-of-Speech Tagging is the identification of words as nouns, verbs, adjectives, adverbs, etc. The system implemented 
here is based of the following paper: 

```
@inproceedings{Even-ZoharRo01,
    author = {Y. Even-Zohar and D. Roth},
    title = {A Sequential Model for Multi Class Classification},
    booktitle = {EMNLP},
    pages = {10-19},
    year = {2001},
    url = " http://cogcomp.cs.illinois.edu/papers/emnlp01.pdf"
}
```

## Performance
The data for the experiments was extracted from the Penn Treebank WSJ and Brown corpora. The training corpus consists 
of 1,044,112 words. The test corpus consists of 129,654 words of which 3,331 are unknown words (that is, they do not occur in the training corpus).

The accuracy on the test test data is 96.439%.  

## Testing the POS Tagger interactively

For a quick demo of the POS Tagger, you can run the following command in the project's root folder.

```shell
sbt "project saulExamples" "runMain edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSTaggerApp"
```

## Using it in your project

If you want to use this in your project, you need to take two steps. First add the dependencies, and then call the functions in your program. 
Here is how you can add maven dependencies into your program: 

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

Similarly the dependencies in sbt: 

```
  resolvers += "CogcompSoftware" at "http://cogcomp.cs.illinois.edu/m2repo/"
  libraryDependencies += "edu.illinois.cs.cogcomp" % "saul-examples_2.11" % "0.1"
```

Here is how you can make calls to the POS tagger in Scala 

```scala  
    import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
    import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator
    import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.{POSClassifiers, POSDataModel}

    import scala.collection.JavaConversions._

    /** Read your data as collection of `Constituent`s. */
    // Your way of reading data. 
    val testData = ... 
    // For example the following, generates dummy constituents: 
    // DummyTextAnnotationGenerator.generateBasicTextAnnotation(1).getView(ViewNames.TOKENS).getConstituents
    
    /** Populate your data in the model */
    POSDataModel.tokens.populate(testData, train = false)

    /** Load the models for the POS classifier */
    POSClassifiers.loadModelsFromPackage()

    /** Make prediction on the input instances */
    testData.foreach { constituent =>
      val predicted = POSClassifiers.POSClassifier(constituent)
      println(predicted)
    }
```

And similarly in Java: 

```java 
    import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
    import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
    import edu.illinois.cs.cogcomp.core.utilities.DummyTextAnnotationGenerator;
    import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers;
    import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSDataModel;
    
    /* Read your data as collection of `Constituent`s. */
    // Your way of reading data.
    // The following is just a bunch of dummy constituents: 
    java.util.List<Constituent> testData = DummyTextAnnotationGenerator.generateBasicTextAnnotation(1).getView(ViewNames.TOKENS).getConstituents();
    scala.collection.Iterable<Constituent> testDataAsScalaCollection = scala.collection.JavaConversions.asScalaBuffer(testData);
    
    // For example the following, generates dummy constituents:
    
    /** Populate your data in the model */
    POSDataModel.tokens().populate(testDataAsScalaCollection, false);
    
    /** Load the models for the POS classifier */
    POSClassifiers.loadModelsFromPackage();
    
    /** Make prediction on the input instances */
    for(Constituent constituent : testData ) {
      String predicted = POSClassifiers.POSClassifier(constituent);
      System.out.println(predicted);
    }
```


Side note: In order to convert raw text to `Constituent`s, you can use the static method 
`BasicTextAnnotationBuilder.createTextAnnotationFromTokens(docs)`, where `docs` is an array of `String`s.  
