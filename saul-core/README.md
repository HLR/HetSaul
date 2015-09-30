
# Saul : Declarative Learning  Based Programming

_This project has been started by [Parisa Kordjamshidi](kordjam@illinois.edu) and the development has been done in collaboration with [Hao Wu](haowu4@illinois.edu)._


## Licensing
_To see the full license for this software, see LICENSE (in doc directory) or visit the download page for this software and press 'Download'. The next screen displays the license._


# Description

Saul is a modeling language implemented as s domain specific language (DSL) in Scala. The main goal of Saul is to facilitate designing machine learning models with arbitrary configurations for the application programmer, including: 

* designing local models i.e. single classifiers. (Learning only models (LO)). 
* designing CCM models in which the independent classifiers are trained but they are used later together for global decision making in prediction time. (Learning+Inference (L+I)). 
* designing global models for joint training and joint inference (Inference-Based-Training (IBT)).
* Pipeline models for complex problems where the output of each layer is used as the input of the next  layer.

The flexibility in designing the above configurations helps rapid development of software systems with one or more learned functions, designed for use with the Java and Scala programming language. 

Saul offers a convenient, declarative syntax for classifier and constraint definition directly in terms of the objects in the programmer's application. With Saul, the details of feature extraction, learning, model evaluation, and inference are all abstracted away from the programmer, leaving him to reason more directly about his application.

# Prerequisite:
 * JDK 1.6 or above
 * Simple Build Tool(sbt)
 * Gurobi 




  
# How to compile the software
 

You can use Saul in any integrated development environment (IDE) appropriate for [Scala](http:link.to.scala)/[Java](http:link.to.java).
We suggest using  [IntelliJ IDEA](https://www.jetbrains.com/idea/download/). Saul uses the Gurobi solver for inference and therefore the Gurobi library needs to be installed 
prior to compilation. To download and install Gurobi visit [Gurobi Website](http://www.gurobi.com/)

Make sure to include Gurobi in your PATH and LD_LIBRARY variables

    export GUROBI_HOME="PATH-TO-GUROBI/linux64"
    export PATH="${PATH}:${GUROBI_HOME}/bin"
    export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"


To compile and package the Saul code simply run:

    $ sbt publish-local

and you will have a jar file in `target/scala-2.11/saul_2.11-1.0.jar` depending on your scala version. It will be also installed in your local Maven repo. 
Then the Saul package can be used in any other project as a dependency. 
