# Set-Cover Problem 
This example contains the Saul implementation of the solution to a set cover
problem.  A city is reviewing the location of its fire stations. The city is made up of a number of neighborhoods. 
A fire station can be placed in any neighborhood. It is able to handle the fires for both its neighborhood and any 
adjacent neighborhood (any neighborhood with a non-zero border with its home neighborhood). The objective is to minimize 
the number of fire stations used. We are asked to find a set of such subsets of neighborods that covers the set of all 
neighborhoods in the sense that every neighborhood appears in the service subset associated with at least one fire station.


Here the problem is formulated as an Integer Linear Program. In Saul, we'll write the constraints in First Order Logic, 
and they will be translated into equivalent [linear inequalities](http://mat.gsia.cmu.edu/orclass/integer/node8.html).  

The main file containing the declarations of the constraints is in 
[`SetCoverSolver.scala`](/saul-examples/src/main/scala/edu/illinois/cs/cogcomp/examples/setcover/SetCoverSolver.scala). 
The data is in `./data/SetCover/example.txt. 

The classes `City` and `Neighborhood` are used as the internal representation of our problem. An instance of the class 
`City` will become the "head" object of an inference problem, which means that it contains all the data needed to
represent that inference problem. Instances of the class `Neighborhood` are the ones we need to classify.

The `DumbLearner` and `ContainsStation` classes are hand written classes that
implement an unconstrained learner.  Normally, we would use Saul to learn such
a classifier, but there was nothing to learn in this case.  The `SetCoverSolver.scala`
code then operates on `ContainsStation` and respects the constraints.

The `SetCoverSolver.scala` takes raw data representing a `City` as input and produces the constrained predictions.

## LBJava way of solving this problem 
Here is the [LBJava](https://github.com/IllinoisCogComp/lbjava/) way of solving the same task: 

```java
constraint noEmptyNeighborhoods(City c) {
  forall (Neighborhood n in c.getNeighborhoods())
    ContainsStation(n) :: true
    \/ (exists (Neighborhood n2 in n.getNeighbors())
          ContainsStation(n2) :: true);
}

inference SetCover head City c {
  Neighborhood n  { return n.getParentCity(); }
  subjectto { @noEmptyNeighborhoods(c); } 
  with new ILPInference(new GLPKHook())
}

discrete{false, true} containsStationConstrained(Neighborhood n) <-
  SetCover(ContainsStation)
```

The constraint`noEmptyNeighborhoods` mandates that for any city at least one city in its neighborhood is covered. 
The main program is defined as `inference SetCover`. The final classifer (whcih tells us whether a city is covered or not), 
is defined as `containsStationConstrained`. 