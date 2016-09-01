## Prerequisite:
 * [Scala](http://www.scala-lang.org/) 2.11.7
 * [Simple Build Tool(sbt)](http://www.scala-sbt.org/)

## How to compile the software
For details of how to compile the software refer to [the main readme file](../README.md#compiling-saul). 

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
