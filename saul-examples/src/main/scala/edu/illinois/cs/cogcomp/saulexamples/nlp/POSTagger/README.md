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
TODO 

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
        <dependency>
            <groupId>edu.illinois.cs.cogcomp</groupId>
            <artifactId>saul-examples-postager-mode</artifactId>
            <version>0.1</version>
        </dependency>
    </dependencies>
```

Similarly the dependencies in sbt: 

```
  resolvers += "CogcompSoftware" at "http://cogcomp.cs.illinois.edu/m2repo/",
  "edu.illinois.cs.cogcomp" % "saul-examples" % "0.1",
  "edu.illinois.cs.cogcomp" % "saul-examples-postager-mode" % "0.1"
```

Here is how you can make calls to the POS tagger in Scala 

```scala 
  TODO 
```

And similarly in Java: 

```java 
  TODO 
```
