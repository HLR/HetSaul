## Prerequisite:
 * [Scala](http://www.scala-lang.org/) 2.11.7
 * [Simple Build Tool(sbt)](http://www.scala-sbt.org/)

## How to use the software

### Using Saul as dependency 
If you want to use Saul in your project, should first add it as your dependency. 
Note that this is the *recommended* way of using it; if you don't use any 
dependency management system, you can use compile it directly (next section).
Here are examples of how it is done in different dependency managment frameworks. 
For each sample code, replace `VERSION` with the latest [Saul version number](http://cogcomp.cs.illinois.edu/m2repo/edu/illinois/cs/cogcomp/saul_2.11/). 

 - Maven 

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
        <artifactId>saul</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

 - SBT 

```sbt
resolvers += "CogcompSoftware" at "http://cogcomp.cs.illinois.edu/m2repo/"
libraryDependencies += "edu.illinois.cs.cogcomp" % "saul_2.11" % "VERSION"
```

 - Gradle 
 
```
compile 'edu.illinois.cs.cogcomp:saul_2.11:VERSION'
```

### Compiling Saul  
Usually you need this options if you are developing using Saul, or 
you want to run the examples.  

First, run `sbt`. 

- `projects` will show the names of the existing module names. 
    - `project saulCore` will take you inside the core package. 
    -  `project saulExamples` will take you inside the examples package.
    - `project saulWebapp` will take you inside the webapp package. Then type `run` to start the server. Type `localhost:9000` in browser address bar to see the webapp.
- Inside each project you can `compile` it, or `run` it. 
- To fix the formatting problems, run `format`


## Installing Solvers for Inference 
Saul uses the the solvers included in [illinois-inference](https://gitlab-beta.engr.illinois.edu/cogcomp/inference/) 
for inference. For more details and instructions on how to install these libraries, visit the details of this library. 

### Installing Gurobi
One of the possible solvers Saul uses, is the Gurobi solver for inference. If you want to use this solver, it has to 
be installed prior to compilation. To download and install Gurobi visit [Gurobi Website](http://www.gurobi.com/)

Make sure to include Gurobi in your PATH and LD_LIBRARY variables

```bash
export GUROBI_HOME="PATH-TO-GUROBI/linux64"
export PATH="${PATH}:${GUROBI_HOME}/bin"
export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"
```

## Developing and Contributing to Saul

You can use Saul in any integrated development environment (IDE) appropriate for Scala/Java.
We suggest using  [IntelliJ IDEA](https://www.jetbrains.com/idea/download/).

If you are interested in contributing to the Saul project, either by your ideas or codes, you are welcome
to create pull requests here.
