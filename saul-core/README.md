# Saul: Declarative Learning  Based Programming

Saul is a modeling language implemented as s domain specific language (DSL) in Scala. The main goal of Saul is to facilitate designing machine learning models with arbitrary configurations for the application programmer, including: 

* designing local models i.e. single classifiers. (Learning only models (LO)). 
* designing CCM models in which the independent classifiers are trained but they are used later together for global decision making in prediction time. (Learning+Inference (L+I)). 
* designing global models for joint training and joint inference (Inference-Based-Training (IBT)).
* Pipeline models for complex problems where the output of each layer is used as the input of the next layer.
* Graph-based query language for design of complex features. 

The flexibility in designing the above configurations helps rapid development of software systems with one or more learned functions, designed for use with the Java and Scala programming language. 

Saul offers a convenient, declarative syntax for classifier and constraint definition directly in terms of the objects in the programmer's application. With Saul, the details of feature extraction, learning, model evaluation, and inference are all abstracted away from the programmer, leaving him to reason more directly about his application.

## Prerequisite:
 * JDK 1.6 or above
 * [Scala](http://www.scala-lang.org/) 2.11.7  
 * [Simple Build Tool(sbt)](http://www.scala-sbt.org/)
 * [Gurobi](http://www.gurobi.com/) 


## How to compile the software
 To compile the code, run: 
 
    $ sbt compile 
 
 To package the code simply run:
 
     $ sbt publish-local

This will generate a jar file in `target/scala-2.11/saul_2.11-1.0.jar` depending on your scala version. 
You can manually include this jar file in your projects. This will also publish the packages in the 
ivy repository (`~/.ivy/local`). Then the Saul package can be used in any other project as a dependency. 
If you are developing an sbt project on your machine, you can add the following line to read the 
Saul package from ivy repository: 
  
     $ libraryDependencies += "edu.illinois.cs.cogcomp" %% "saul" % "2.0"

## Installing Gurobi 
Saul uses the Gurobi solver for inference and therefore the Gurobi library needs to be installed 
prior to compilation. To download and install Gurobi visit [Gurobi Website](http://www.gurobi.com/)

Make sure to include Gurobi in your PATH and LD_LIBRARY variables

    export GUROBI_HOME="PATH-TO-GUROBI/linux64"
    export PATH="${PATH}:${GUROBI_HOME}/bin"
    export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"

## Developing and Contributing to Saul 

You can use Saul in any integrated development environment (IDE) appropriate for [Scala](http:link.to.scala)/[Java](http:link.to.java).
We suggest using  [IntelliJ IDEA](https://www.jetbrains.com/idea/download/). 

If you are interested in contributing to the Saul project, either by your ideas or codes, you are welcome 
to create pull requests here. 

## Credits 
_This project has been started by [Parisa Kordjamshidi](kordjam@illinois.edu) and the development has been done in collaboration with [Hao Wu](haowu4@illinois.edu)._


## Licensing
_To see the full license for this software, see the LICENSE (in `doc` directory) or visit the download page 
for this software and press `Download`. The next screen displays the license._
