## Prerequisite:
 * JDK 1.6 or above
 * [Scala](http://www.scala-lang.org/) 2.11.7
 * [Simple Build Tool(sbt)](http://www.scala-sbt.org/)
 * [Gurobi](http://www.gurobi.com/) This is optional if you want to use Gurobi as the backend solver.


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