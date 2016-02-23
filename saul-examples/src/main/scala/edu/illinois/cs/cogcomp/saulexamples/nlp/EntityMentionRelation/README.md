# Entity-Mention-Relation classification 

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
types.

In this example we show how to model this problem and show different training/inference paradigms 
for this problem. 

 - Pipeline Training: 
 - L+I Training 
 - Joint Training: 


[1] D. Roth and W-t Yih. "A Linear Programming Formulation for Global Inference in Natural Language Tasks." In Proceedings of CoNLL-2004. 2004.
[2] M. Chang, L. Ratinov, and D. Roth. "Structured learning with constrained conditional models." Machine Learning,
    88(3):399–431, 6 2012.