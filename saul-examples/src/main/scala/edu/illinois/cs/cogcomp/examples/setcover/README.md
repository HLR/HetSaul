# Set-Cover Problem 
This example contains the Saul implementation of the solution to a set cover
problem. The particular problem comes from the following web page, in which
the problem is formulated as an Integer Linear Program. In Saul as in LBJava, we'll write
the constraints in First Order Logic, and they will be translated into the same
linear inequalities shown in [this web page](http://mat.gsia.cmu.edu/orclass/integer/node8.html): 


## Relevant files 
The data is in: `data/setcover.txt`

The reader and the relevant data structures are written in java: 
```
main/java/edu/illinois/cs/cogcomp/setcover/City.java
main/java/edu/illinois/cs/cogcomp/setcover/ContainsStation.java
main/java/edu/illinois/cs/cogcomp/setcover/Neighborhood.java
main/java/edu/illinois/cs/cogcomp/setcover/SetCoverSolver.java
main/java/edu/illinois/cs/cogcomp/setcover/DumbLearner.java
```

The main file containing the declarations of the constraints is here:
`main/scala/edu/illinois/cs/cogcomp/examples/setcover/SetCoverSolver.scala` 

The classes `City` and `Neighborhood` are used as the internal representation of our
problem.  An instance of the class `City` will become the "head" object of an
inference problem, which means that it contains all the data needed to
represent that inference problem.  Instances of the class `Neighborhood` are the
ones we need to classify.

The `DumbLearner` and `ContainsStation` classes are hand written classes that
implement an unconstrained learner.  Normally, we would use Saul to learn such
a classifier, but there was nothing to learn in this case.  The `SetCoverSolver.scala`
code then operates on `ContainsStation` and respects the constraints.

The `SetCoverSolver.scala` takes raw data representing a `City` as input
and produces the constrained predictions.
